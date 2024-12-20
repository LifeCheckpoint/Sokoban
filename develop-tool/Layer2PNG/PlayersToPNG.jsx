// Spine Layers to png
// This script exports photoshop layers as individual PNGs. It also
// writes a JSON file that can be imported into Spine where the images
// will be displayed in the same positions and draw order.

// Mod by JanusHuang
// Version 1.1.0
// (e-mail:januswow@gmail.com)
//
// Gives you the ability to easily do more optimization to texture resolution.
//
// You are able to set export scale and rotation values for a specific layer in Photoshop and maintain
// the size and rotation of attachments after importing to Spine, just the same as you see in Photoshop.
//
// v1.0.0
// 1. When exporting images, scale and rotation values will automatically apply according to the parameters specified in the Photoshop layer name.
// 2. When exporting JSON, scale and rotation values specified in the Photoshop layer name will be written as JSON data.
// v1.1.0
// 1. Prevent getting error from incorrect suffixes.
// 2. Export images through Save For Web.
// 3. Calculate data by layer bounds.
// 4. Allow corp pixles out of canvas or not.
// 5. Fixed incorrect position after layer rotated.
//
// You can define scale and rotation by adding suffixes to layer name.
// Define scale by adding suffix ' #s' to layer name.
// Define rotation by adding suffix ' #r' to layer name.
// ex. 'thisislayername #s0.5' (scale = 50%)
// ex. 'thisislayername #s0.8 #r-12' (scale = 80%, rotation = -12 degree)
//
// Original Version : https://gist.github.com/NathanSweet/c8e2f6e1d79dedd56e8c

// Setting defaults.
var writePngs = true;
var writeTemplate = false;
var writeJson = true;
var ignoreHiddenLayers = true;
var pngScale = 1;
var groupsAsSkins = false;
var useRulerOrigin = false;
var corpToCanvas = false;
var imagesDir = "./images/";
var projectDir = "";
var padding = 1;

// JH Version Variables
var JHVersion = "1.1.0"
var patternRotate = " #r";
var patternScale = " #s";
// minimum width/height pixels of output image
var customScaleMin = 4;
var docWidth;
var docHeight;

// IDs for saving settings.
const settingsID = stringIDToTypeID("settings");
const writePngsID = stringIDToTypeID("writePngs");
const writeTemplateID = stringIDToTypeID("writeTemplate");
const writeJsonID = stringIDToTypeID("writeJson");
const ignoreHiddenLayersID = stringIDToTypeID("ignoreHiddenLayers");
const groupsAsSkinsID = stringIDToTypeID("groupsAsSkins");
const useRulerOriginID = stringIDToTypeID("useRulerOrigin");
const corpToCanvasID = stringIDToTypeID("corpToCanvas");
const pngScaleID = stringIDToTypeID("pngScale");
const imagesDirID = stringIDToTypeID("imagesDir");
const projectDirID = stringIDToTypeID("projectDir");
const paddingID = stringIDToTypeID("padding");

var originalDoc;
try {
	originalDoc = app.activeDocument;
} catch (ignored) {}
var settings, progress;
loadSettings();
showDialog();

// execute while OK button pressed.
function run () {
	// Output dirs.
	var absProjectDir = absolutePath(projectDir);
	new Folder(absProjectDir).create();
	var absImagesDir = absolutePath(imagesDir);
	var imagesFolder = new Folder(absImagesDir);
	imagesFolder.create();
	var relImagesDir = imagesFolder.getRelativeURI(absProjectDir);
	relImagesDir = relImagesDir == "." ? "" : (relImagesDir + "/");

	// Get ruler origin.
	var xOffSet = 0, yOffSet = 0;
	if (useRulerOrigin) {
		var ref = new ActionReference();
		ref.putEnumerated(charIDToTypeID("Dcmn"), charIDToTypeID("Ordn"), charIDToTypeID("Trgt"));
		var desc = executeActionGet(ref);
		xOffSet = desc.getInteger(stringIDToTypeID("rulerOriginH")) >> 16;
		yOffSet = desc.getInteger(stringIDToTypeID("rulerOriginV")) >> 16;
	}

	activeDocument.duplicate();

    // Save for web options
 	var outputExtension = ".png";
    var sfwOptions;
    sfwOptions = new ExportOptionsSaveForWeb();
    sfwOptions.format = SaveDocumentType.PNG;
    // save to PNG24
    sfwOptions.PNG8 = false;

	// Output template image.
	if (writeTemplate) {
		if (pngScale != 1) {
			scaleImage();
			storeHistory();
		}

		var file = new File(absImagesDir + "template" + outputExtension);
		if (file.exists) file.remove();

		activeDocument.exportDocument(file, ExportType.SAVEFORWEB, sfwOptions);

		if (pngScale != 1) restoreHistory();
	}

	if (!writeJson && !writePngs) {
		activeDocument.close(SaveOptions.DONOTSAVECHANGES);
		return;
	}

	// Rasterize all layers.
	try {
		executeAction(stringIDToTypeID( "rasterizeAll" ), undefined, DialogModes.NO);
	} catch (ignored) {}

	// Collect and hide layers.
	var layers = [];
	collectLayers(activeDocument, layers);
	var layersCount = layers.length;

	storeHistory();

	// Store the slot names and layers for each skin.
	var slots = {}, skins = { "default": [] };
	for (var i = layersCount - 1; i >= 0; i--) {
		var layer = layers[i];

		// Use groups as skin names.
		var potentialSkinName = trim(layer.parent.name);
		var layerGroupSkin = potentialSkinName.indexOf("-NOSKIN") == -1;
		var skinName = (groupsAsSkins && layer.parent.typename == "LayerSet" && layerGroupSkin) ? potentialSkinName : "default";

		var skinLayers = skins[skinName];
		if (!skinLayers) skins[skinName] = skinLayers = [];
		skinLayers[skinLayers.length] = layer;

		slots[layerName(layer)] = true;
	}

	// Output skeleton and bones.
	var json = '{"skeleton":{"images":"' + relImagesDir + '"},\n"bones":[{"name":"root"}],\n"slots":[\n';

	// Output slots.
	var slotsCount = countAssocArray(slots);
	var slotIndex = 0;

	// Output template slot
	if (writeTemplate) {
		json += '\t{"name":"' + "template" + '","bone":"root","attachment":"' + "template" + '"}';
		json += slotsCount > 0 ? ",\n" : "\n";
	}

	for (var slotName in slots) {

		if (!slots.hasOwnProperty(slotName)) continue;

		// Use image prefix if slot's attachment is in the default skin.
		var attachmentName = slotName;
		var defaultSkinLayers = skins["default"];
		
		for (var i = defaultSkinLayers.length - 1; i >= 0; i--) {
			if (layerName(defaultSkinLayers[i]) == slotName) {
				attachmentName = slotName;
				break;
			}
		}

		json += '\t{"name":"' + slotName + '","bone":"root","attachment":"' + attachmentName + '"}';
		slotIndex++;
		json += slotIndex < slotsCount ? ",\n" : "\n";
	}
	json += '],\n"skins":{\n';

	// Output skins.
	var skinsCount = countAssocArray(skins);
	var skinIndex = 0;
	docWidth = activeDocument.width.as("px");
	docHeight = activeDocument.height.as("px");

	for (var skinName in skins) {
		if (!skins.hasOwnProperty(skinName)) continue;
		json += '\t"' + skinName + '":{\n';

		var skinLayers = skins[skinName];
		var skinLayersCount = skinLayers.length;
		var skinLayerIndex = 0;

		// Output Template
		if (writeTemplate) {
			json += '\t\t"' + "template" + '":{"' + "template" + '":{'
			if (useRulerOrigin) {
				json += '"x":' + docWidth / 2 + ',"y":' + docHeight / 2 * -1;
			} else {
				json += '"x":' + docWidth / 2 + ',"y":' + docHeight / 2;
			}			
			json += ',"width":' + docWidth + ',"height":' + docHeight + '}}';
			json += skinLayersCount > 0 ? ",\n" : "\n";
		}

		for (var i = skinLayersCount - 1; i >= 0; i--) {
			var layer = skinLayers[i];

			var sourceLayerName = srcLayerName(layer);
			var slotName = layerName(layer);
			var placeholderName, attachmentName;
			if (skinName == "default") {
				placeholderName = slotName;
				attachmentName = placeholderName;
			} else {
				placeholderName = slotName;
				attachmentName = skinName + "/" + slotName;
			}

			layer.visible = true;

			// get data from bounds
			var boundsCenter = getBoundsCenter(layer);
			var x = boundsCenter[0];
			var y = boundsCenter[1];
			var customRotate = 0;
			var customScale = 1;

			// get custom scale and rotation from layer name
			if (hasCustomParameters(sourceLayerName)) {
				customRotate = GetRotate(sourceLayerName);
				customScale = GetScale(sourceLayerName);
				// prevent resolution of output image too small
				if (customScale != 1) customScale = checkMinimumCustomScale(layer, customScale);
			}
			
			// Rotate image
			if (customRotate != 0) {
				layer.rotate(customRotate, AnchorPosition.MIDDLECENTER);
				var rotatedCenter = getBoundsCenter(layer);
				var fixedCenter = fixRotatedOffset(rotatedCenter, boundsCenter, customRotate);
				x = fixedCenter[0];
				y = fixedCenter[1];
			}
			
			// Corp to canvas and trim layer
			if (!corpToCanvas || customRotate != 0) extendCanvas(layer);
			if (!layer.isBackgroundLayer) activeDocument.trim(TrimType.TRANSPARENT, false, false, true, true);
			if (corpToCanvas && customRotate == 0) {
				x = getCorpBoundsCenter(layer)[0];
				y = getCorpBoundsCenter(layer)[1];
			}
			if (!layer.isBackgroundLayer) activeDocument.trim(TrimType.TRANSPARENT, true, true, false, false);

			// Scale image
			if (pngScale != 1 || customScale != 1) {
				scaleImage(customScale);
			}

			// Padding
			if (padding > 0) {
				var canvasWidth = activeDocument.width.as("px") + padding * 2;
				var canvasHeight = activeDocument.height.as("px") + padding * 2;
				activeDocument.resizeCanvas(canvasWidth, canvasHeight, AnchorPosition.MIDDLECENTER);
			}

			// Save image
			if (writePngs) {
				if (skinName != "default") new Folder(absImagesDir + skinName).create();
				activeDocument.exportDocument(new File(absImagesDir + attachmentName + outputExtension), ExportType.SAVEFORWEB, sfwOptions);
			}

			restoreHistory();
			layer.visible = false;

			if (useRulerOrigin) {
				y = toRulerCoord([x,y])[1];
			} else {
				y = toSpineCoord([x,y])[1];
			}

			var outScale = mathRound(1 / customScale, 2);

			// Write JSON
			if (attachmentName == placeholderName) {
				json += '\t\t"' + slotName + '":{"' + placeholderName + '":{'
			} else {
				json += '\t\t"' + slotName + '":{"' + placeholderName + '":{"name":"' + attachmentName + '", '
			}
			// write position
			json += '"x":' + mathRound(x, 1) + ',"y":' + mathRound(y, 1);
			// write custom scale
			if (customScale != 1) json += ',"scaleX":' + outScale + ',"scaleY":' + outScale;
			// write custom rotate
			if (customRotate != 0) json += ',"rotation":' + customRotate;
			// write size
			json += ',"width":' + Math.round(canvasWidth) + ',"height":' + Math.round(canvasHeight);
			json += '}}';

			skinLayerIndex++;
			json += skinLayerIndex < skinLayersCount ? ",\n" : "\n";
		}
		json += "\t\}";

		skinIndex++;
		json += skinIndex < skinsCount ? ",\n" : "\n";
	}
	json += '},\n"animations":{"animation":{}}\n}';

	activeDocument.close(SaveOptions.DONOTSAVECHANGES);

	// Output JSON file.
	if (writeJson) {
		var name = decodeURI(originalDoc.name);
		name = name.substring(0, name.indexOf("."));
		var file = new File(absProjectDir + name + ".json");
		file.remove();
		file.open("w", "TEXT");
		file.lineFeed = "\n";
		file.write(json);
		file.close();
	}

	alert("Done!\n" + skinLayersCount + " layers exported.")
}

// Dialog and settings:

function showDialog () {
	if (!originalDoc) {
		alert("Please open a document before running the LayersToPNG script.");
		return;
	}
	if (!hasFilePath()) {
		alert("Please save the document before running the LayersToPNG script.");
		return;
	}

	var dialog = new Window("dialog", "Spine LayersToPNG JH v" + JHVersion + " by JanusHuang");
	dialog.alignChildren = "fill";

	var checkboxGroup = dialog.add("group");
		var group = checkboxGroup.add("group");
			group.orientation = "column";
			group.alignChildren = "left";
			var writePngsCheckbox = group.add("checkbox", undefined, " Write layers as PNGs");
			writePngsCheckbox.value = writePngs;
			var writeTemplateCheckbox = group.add("checkbox", undefined, " Write a template PNG");
			writeTemplateCheckbox.value = writeTemplate;
			var writeJsonCheckbox = group.add("checkbox", undefined, " Write Spine JSON");
			writeJsonCheckbox.value = writeJson;
			var emptyCheckbox = group.add("checkbox", undefined, " Empty");
			emptyCheckbox.visible = false;
			emptyCheckbox.enabled = false;
		group = checkboxGroup.add("group");
			group.orientation = "column";
			group.alignChildren = "left";
			var ignoreHiddenLayersCheckbox = group.add("checkbox", undefined, " Ignore hidden layers");
			ignoreHiddenLayersCheckbox.value = ignoreHiddenLayers;
			var groupsAsSkinsCheckbox = group.add("checkbox", undefined, " Use groups as skins");
			groupsAsSkinsCheckbox.value = groupsAsSkins;
			var useRulerOriginCheckbox = group.add("checkbox", undefined, " Use ruler origin as 0,0");
			useRulerOriginCheckbox.value = useRulerOrigin;
			var corpToCanvasCheckbox = group.add("checkbox", undefined, " Corp to canvas");
			corpToCanvasCheckbox.value = corpToCanvas;

	var slidersGroup = dialog.add("group");
		group = slidersGroup.add("group");
			group.orientation = "column";
			group.alignChildren = "right";
			group.add("statictext", undefined, "PNG scale:");
			group.add("statictext", undefined, "Padding:");
		group = slidersGroup.add("group");
			group.orientation = "column";
			var scaleText = group.add("edittext", undefined, pngScale * 100);
			scaleText.characters = 4;
			var paddingText = group.add("edittext", undefined, padding);
			paddingText.characters = 4;
		group = slidersGroup.add("group");
			group.orientation = "column";
			group.add("statictext", undefined, "%");
			group.add("statictext", undefined, "px");
		group = slidersGroup.add("group");
			group.alignment = ["fill", ""];
			group.orientation = "column";
			group.alignChildren = ["fill", ""];
			var scaleSlider = group.add("slider", undefined, pngScale * 100, 1, 100);
			var paddingSlider = group.add("slider", undefined, padding, 0, 4);
	scaleText.onChanging = function () { scaleSlider.value = scaleText.text; };
	scaleSlider.onChanging = function () { scaleText.text = Math.round(scaleSlider.value); };
	paddingText.onChanging = function () { paddingSlider.value = paddingText.text; };
	paddingSlider.onChanging = function () { paddingText.text = Math.round(paddingSlider.value); };

	var outputGroup = dialog.add("panel", undefined, "Output directories");
		outputGroup.alignChildren = "fill";
		outputGroup.margins = [10,15,10,10];
		var textGroup = outputGroup.add("group");
			group = textGroup.add("group");
				group.orientation = "column";
				group.alignChildren = "right";
				group.add("statictext", undefined, "Images:");
				group.add("statictext", undefined, "JSON:");
			group = textGroup.add("group");
				group.orientation = "column";
				group.alignChildren = "fill";
				group.alignment = ["fill", ""];
				var imagesDirText = group.add("edittext", undefined, imagesDir);
				var projectDirText = group.add("edittext", undefined, projectDir);
		outputGroup.add("statictext", undefined, "Begin paths with \"./\" to be relative to the PSD file.").alignment = "center";

	var group = dialog.add("group");
		// group.alignment = "center";
		var runButton = group.add("button", undefined, "OK");
		var cancelButton = group.add("button", undefined, "Cancel");
		var JHButton = group.add("button", undefined, "!");
		JHButton.size = [24,20];
		JHButton.onClick = function () {
			alert("You can define scale and rotation by adding suffixes to layer name.\r\n\r\nDefine scale by adding suffix ' #s' to layer name.\r\nDefine roataion by adding suffix ' #r' to layer name.\r\n\r\n ex. 'thisislayername #s0.5' (scale = 50%)\r\n ex. 'thisislayername #s0.8 #r-12' (scale = 80%, rotation = -12 degree)")
		};
		cancelButton.onClick = function () {
			dialog.close(0);
			return;
		};

	function updateSettings () {
		writePngs = writePngsCheckbox.value;
		writeTemplate = writeTemplateCheckbox.value;
		writeJson = writeJsonCheckbox.value;
		ignoreHiddenLayers = ignoreHiddenLayersCheckbox.value;
		var scaleValue = parseFloat(scaleText.text);
		if (scaleValue > 0 && scaleValue <= 100) pngScale = scaleValue / 100;
		groupsAsSkins = groupsAsSkinsCheckbox.value;
		useRulerOrigin = useRulerOriginCheckbox.value;
		corpToCanvas = corpToCanvasCheckbox.value;
		imagesDir = imagesDirText.text;
		projectDir = projectDirText.text;
		var paddingValue = parseInt(paddingText.text);
		if (paddingValue >= 0) padding = paddingValue;
	}

	dialog.onClose = function() {
		updateSettings();
		saveSettings();
	};
	
	runButton.onClick = function () {
		if (scaleText.text <= 0 || scaleText.text > 100) {
			alert("PNG scale must be between > 0 and <= 100.");
			return;
		}
		if (paddingText.text < 0) {
			alert("Padding must be >= 0.");
			return;
		}
		dialog.close(0);

		var rulerUnits = app.preferences.rulerUnits;
		app.preferences.rulerUnits = Units.PIXELS;
		try {
			run();
		} catch (e) {
			alert("An unexpected error has occurred.\n\nTo debug, run the LayersToPNG script using Adobe ExtendScript "
				+ "with \"Debug > Do not break on guarded exceptions\" unchecked.");
			debugger;
		} finally {
			if (activeDocument != originalDoc) activeDocument.close(SaveOptions.DONOTSAVECHANGES);
			app.preferences.rulerUnits = rulerUnits;
		}
	};

	dialog.center();
	dialog.show();
}

function loadSettings () {
	try {
		settings = app.getCustomOptions(settingsID);
	} catch (e) {
		saveSettings();
	}
	if (typeof settings == "undefined") saveSettings();
	settings = app.getCustomOptions(settingsID);
	if (settings.hasKey(writePngsID)) writePngs = settings.getBoolean(writePngsID);
	if (settings.hasKey(writeTemplateID)) writeTemplate = settings.getBoolean(writeTemplateID);
	if (settings.hasKey(writeJsonID)) writeJson = settings.getBoolean(writeJsonID);
	if (settings.hasKey(ignoreHiddenLayersID)) ignoreHiddenLayers = settings.getBoolean(ignoreHiddenLayersID);
	if (settings.hasKey(pngScaleID)) pngScale = settings.getDouble(pngScaleID);
	if (settings.hasKey(groupsAsSkinsID)) groupsAsSkins = settings.getBoolean(groupsAsSkinsID);
	if (settings.hasKey(useRulerOriginID)) useRulerOrigin = settings.getBoolean(useRulerOriginID);
	if (settings.hasKey(corpToCanvasID)) corpToCanvas = settings.getBoolean(corpToCanvasID);
	if (settings.hasKey(imagesDirID)) imagesDir = settings.getString(imagesDirID);
	if (settings.hasKey(projectDirID)) projectDir = settings.getString(projectDirID);
	if (settings.hasKey(paddingID)) padding = settings.getDouble(paddingID);
}

function saveSettings () {
	var settings = new ActionDescriptor();
	settings.putBoolean(writePngsID, writePngs);
	settings.putBoolean(writeTemplateID, writeTemplate);
	settings.putBoolean(writeJsonID, writeJson);
	settings.putBoolean(ignoreHiddenLayersID, ignoreHiddenLayers);
	settings.putDouble(pngScaleID, pngScale);
	settings.putBoolean(groupsAsSkinsID, groupsAsSkins);
	settings.putBoolean(useRulerOriginID, useRulerOrigin);
	settings.putBoolean(corpToCanvasID, corpToCanvas);
	settings.putString(imagesDirID, imagesDir);
	settings.putString(projectDirID, projectDir);
	settings.putDouble(paddingID, padding);
	app.putCustomOptions(settingsID, settings, true);
}

// Photoshop utility:

function scaleImage (customScale) {
	if (customScale == undefined) customScale = 1;
	var imageSize = activeDocument.width.as("px");
	activeDocument.resizeImage(UnitValue(imageSize * pngScale * customScale, "px"), null, null, ResampleMethod.BICUBICSHARPER);
}

function rotateImage (customRotate) {
	if (customRotate == undefined) customRotate = 0;
	activeDocument.rotateCanvas(customRotate);
}

function fixRotatedOffset(rotatedCenter, originalCenter, degrees){
	// Get right position of rotated layer to write JSON data
	var rcx = rotatedCenter[0];
	var rcy = rotatedCenter[1];
	var ocx = originalCenter[0];
	var ocy = originalCenter[1];
	// angle must be counter-clockwise
	var theta = toRadians(degrees * -1);

	// translate point to origin
	var tempX = ocx - rcx;
	var tempY = ocy - rcy;

	// now apply rotation
	var rotatedX = parseFloat(tempX * Math.cos(theta) - tempY * Math.sin(theta));
	var rotatedY = parseFloat(tempX * Math.sin(theta) + tempY * Math.cos(theta));

	// position to JSON
	var outX = ocx - rotatedX;
	var outY = ocy - rotatedY;

	return [outX, outY];
}

function checkMinimumCustomScale (layer, customScale) {
	// Prevent resolution of output image too small
	var boundsSize = getBoundsSize(layer);
	var result = customScale;
	var minPixelsOrig = Math.min(boundsSize[0], boundsSize[1]);
	var minPixels = minPixelsOrig * pngScale * customScale;

	if (minPixels < customScaleMin) result = Math.ceil(mathRound(customScaleMin / minPixelsOrig, 3));

	return result;
}

var historyIndex;
function storeHistory () {
	historyIndex = activeDocument.historyStates.length - 1;
}
function restoreHistory () {
	activeDocument.activeHistoryState = activeDocument.historyStates[historyIndex];
}

function collectLayers (layer, collect) {
	for (var i = 0, n = layer.layers.length; i < n; i++) {
		var child = layer.layers[i];
		if (ignoreHiddenLayers && !child.visible) continue;
		if (child.bounds[2] == 0 && child.bounds[3] == 0) continue;
		if (child.layers && child.layers.length > 0)
			collectLayers(child, collect);
		else if (child.kind == LayerKind.NORMAL) {
			collect.push(child);
			child.visible = false;
		}
	}
}

function hasFilePath () {
	var ref = new ActionReference();
	ref.putEnumerated(charIDToTypeID("Dcmn"), charIDToTypeID("Ordn"), charIDToTypeID("Trgt"));
	return executeActionGet(ref).hasKey(stringIDToTypeID("fileReference"));
}

function absolutePath (path) {
	path = trim(path);
	if (path.length == 0)
		path = activeDocument.path.toString();
	else if (imagesDir.indexOf("./") == 0)
		path = activeDocument.path + path.substring(1);
	path = path.replace(/\\/g, "/");
	if (path.substring(path.length - 1) != "/") path += "/";
	return path;
}

// Mathematics utility:

function toRadians (degrees) {
	return degrees * Math.PI / 180;
}

function mathRound(num, decimal) {
	var f = decimal > 0 ? Math.pow(10, decimal) : 1;
	return Math.round(num * f) / f;
}

// JavaScript utility:

function countAssocArray (obj) {
	var count = 0;
	for (var key in obj)
		if (obj.hasOwnProperty(key)) count++;
	return count;
}

function trim (value) {
	return value.replace(/^\s+|\s+$/g, "");
}

function endsWith (str, suffix) {
	return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function stripSuffix (str, suffix) {
	if (endsWith(str.toLowerCase(), suffix.toLowerCase())) str = str.substring(0, str.length - suffix.length);
	return str;
}

function layerName (layer) {
	// Get layer name without custom scale and rotation parameters string
	var result = stripSuffix(trim(layer.name), ".png").replace(/[:\/\\*\?\"\<\>\|]/g, "");
	var endIndex = result.indexOf(" #");
	if (endIndex != -1) {
		result = result.substring(0, endIndex);
	}

	return result
}

function srcLayerName (layer) {
	// Original version of layerName function
	return stripSuffix(trim(layer.name), ".png").replace(/[:\/\\*\?\"\<\>\|]/g, "");
}

// Custom properties utility :

function hasCustomParameters (layerName) {
	var result = false;
	if (layerName.search(patternRotate) != -1 || layerName.search(patternScale) != -1) {
		result = true;
	}

	return result;
}

function GetPropertyInfo (layerName, pattern) {
	var result = NaN;
	var patternIndex = layerName.lastIndexOf(pattern);
	var propertyInfo;

	if (patternIndex != -1) {
		var patternEndIndex = patternIndex + pattern.length;
		var propertyString = layerName.substring( patternEndIndex, layerName.length );
		var endIndex = propertyString.indexOf(" ");
		if (endIndex != -1) {
			propertyInfo = propertyString.substring(0, endIndex);
		} else {
			propertyInfo = propertyString;
		}
		result = parseFloat(propertyInfo);
	}

	return result;
}

function GetRotate (layerName) {
	var result = 0;
	var value = GetPropertyInfo(layerName, patternRotate);
	if (!isNaN(value)) result = value;

	return result;
}

function GetScale (layerName) {
	var result = 1;
	var value = GetPropertyInfo(layerName, patternScale);
	if (!isNaN(value)) result = value;

	return result;
}

// Bounds and Corp to canvas utility :

function clampXToCanvas(num, axis) {
	return Math.min( Math.max(num, 0), docWidth );
}

function clampYToCanvas(num, axis) {
	return Math.min( Math.max(num, 0), docHeight );
}

function clampBoundsToCanvas(bounds) {
	var x1 = clampXToCanvas(bounds[0]);
	var y1 = clampYToCanvas(bounds[1]);
	var x2 = clampXToCanvas(bounds[2]);
	var y2 = clampYToCanvas(bounds[3]);

	return [x1, y1, x2, y2];
}

function getBoundsValue(layer) {
	return [layer.bounds[0].value, layer.bounds[1].value, layer.bounds[2].value, layer.bounds[3].value];
}

function getBoundsSize(layer) {
	var bounds = getBoundsValue(layer);
	var width = bounds[2] - bounds[0];
	var height = bounds[3] - bounds[1];

	return [width, height];
}

function getBoundsCenter(layer) {
	var bounds = getBoundsValue(layer);
	var size = getBoundsSize(layer);
	var x = ( bounds[0] + size[0] / 2 ) * pngScale;
	var y = ( bounds[1] + size[1] / 2 ) * pngScale;

	return [x, y];
}

function getCorpBoundsCenter(layer) {
	var clampBounds = clampBoundsToCanvas(getBoundsValue(layer));
	var x1 = clampBounds[0];
	var y1 = clampBounds[1];
	var x2 = clampBounds[2];
	var y2 = clampBounds[3];
	var width = x2 - x1;
	var height = y2 - y1;
	var x = ( x1 + width / 2 ) * pngScale;
	var y = ( y1 + height / 2 ) * pngScale;

	return [x, y];
}

function getOutOfCanvasDist(layer) {
	var bounds = getBoundsValue(layer);
	var x1 = bounds[0];
	var y1 = bounds[1];
	var x2 = activeDocument.width.as("px") - bounds[2];
	var y2 = activeDocument.height.as("px") - bounds[3];
	var dist = Math.min(x1, y1, x2, y2);

	return dist;
}

function isOutOfCanvas(layer) {
	if (getOutOfCanvasDist(layer) < 0) {
		return true;
	} else {
		return false;
	}
}

function extendCanvas(layer) {
	// Extend Canvas size for not corpping visible pixels
	if (!isOutOfCanvas(layer)) return;
	var dist = Math.abs(getOutOfCanvasDist(layer)) * 2;
	var w = activeDocument.width.as("px") + dist;
	var h = activeDocument.height.as("px") + dist;
	activeDocument.resizeCanvas(w, h, AnchorPosition.MIDDLECENTER);
}

// Coordinates utility :

function toSpineCoord(pos) {
	return [pos[0], docHeight - pos[1]];
}

function toRulerCoord(pos) {
	return [pos[0], pos[1] * -1];
}