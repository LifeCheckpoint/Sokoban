## **类概述**

`MapFileParser` 是一个地图解析器类，负责从地图文件的字符串中解析地图信息并存储到 `MapData` 对象中。该类支持解析多子地图结构，并确保输入的格式和数据一致性。

---

## **方法详解**

### 1. `parseMapData`
**方法签名：**  
```java
public static MapData parseMapData(MapInfo mapFileInfo, String mapFileString)
```

**功能：**  
入口方法，将地图文件的字符串解析为 `MapData` 对象。该方法对整个流程进行协调，包括预处理、验证、子地图解析等。

**参数：**  
- `mapFileInfo`：地图的元信息，例如地图名称、作者等。  
- `mapFileString`：地图文件内容的原始字符串。

**返回值：**  
成功返回解析后的 `MapData` 对象，失败返回 `null`。

**示例：**
```java
String mapFileString = """
地图附加信息
2
4 5
1 0 0 0 0
1 1 0 0 0
1 0 0 1 1
1 1 1 0 0
-
3 3
0 1 1
1 0 1
0 0 1
""";

MapInfo mapInfo = new MapInfo("Test Map", "AuthorName");
MapData mapData = MapFileParser.parseMapData(mapInfo, mapFileString);
if (mapData != null) {
    System.out.println("地图解析成功！");
}
```

---

### 2. `preprocessLines`
**方法签名：**  
```java
private static String[] preprocessLines(String mapFileString)
```

**功能：**  
将输入的地图文件字符串分割为行，并去掉每行的首尾空格。

**参数：**  
- `mapFileString`：地图文件的原始文本字符串。

**返回值：**  
处理后的行数组。

**示例：**
```java
String mapFileString = " 附加信息 \n 2 \n 4 5 \n ";
String[] lines = MapFileParser.preprocessLines(mapFileString);
// lines = ["附加信息", "2", "4 5"]
```

---

### 3. `validateLineCount`
**方法签名：**  
```java
private static boolean validateLineCount(String[] lines)
```

**功能：**  
验证地图文件的行数是否大于等于 4 行（包括附加信息和子地图数据）。

**参数：**  
- `lines`：地图文件的行数组。

**返回值：**  
`true` 表示行数有效，`false` 表示行数不足。

**示例：**
```java
String[] lines = {"附加信息", "2", "4 5"};
boolean isValid = MapFileParser.validateLineCount(lines);
// isValid = false
```

---

### 4. `parseMetadata`
**方法签名：**  
```java
private static boolean parseMetadata(String[] lines, MapData mapData)
```

**功能：**  
解析地图附加信息和子地图数量，并存储到 `mapData` 中。

**参数：**  
- `lines`：地图文件的行数组。  
- `mapData`：存储解析结果的 `MapData` 对象。

**返回值：**  
`true` 表示解析成功，`false` 表示解析失败。

**示例：**
```java
String[] lines = {"附加信息", "2"};
MapData mapData = new MapData();
boolean success = MapFileParser.parseMetadata(lines, mapData);
// mapData.addtionalInfo = "附加信息"
// mapData.subMapNums = 2
```

---

### 5. `extractSubmapData`
**方法签名：**  
```java
private static List<List<String>> extractSubmapData(String[] lines, int subMapNums)
```

**功能：**  
提取子地图的文本数据，并将每个子地图的内容存储为独立的 `List`。

**参数：**  
- `lines`：地图文件的行数组。  
- `subMapNums`：子地图数量。

**返回值：**  
返回一个列表，其中每个子列表表示一个子地图的文本内容；失败返回 `null`。

**示例：**
```java
String[] lines = {"附加信息", "2", "4 5", "1 0 0 0 0", "-", "3 3", "0 1 1"};
List<List<String>> submapData = MapFileParser.extractSubmapData(lines, 2);
// submapData = [["4 5", "1 0 0 0 0"], ["3 3", "0 1 1"]]
```

---

### 6. `parseSubmaps`
**方法签名：**  
```java
private static boolean parseSubmaps(List<List<String>> submapFileTexts, MapData mapData)
```

**功能：**  
解析所有子地图的内容，将解析结果存储到 `mapData` 中。

**参数：**  
- `submapFileTexts`：子地图的文本内容列表。  
- `mapData`：存储解析结果的 `MapData` 对象。

**返回值：**  
`true` 表示解析成功，`false` 表示解析失败。

**示例：**
```java
List<List<String>> submapFileTexts = [["4 5", "1 0 0 0 0"], ["3 3", "0 1 1"]];
MapData mapData = new MapData();
boolean success = MapFileParser.parseSubmaps(submapFileTexts, mapData);
```

---

### 7. `parseSingleSubmap`
**方法签名：**  
```java
private static void parseSingleSubmap(List<String> subMapContent, MapData mapData, int subMapIndex)
```

**功能：**  
解析单个子地图的内容，包括高宽信息和每行数据。

**参数：**  
- `subMapContent`：单个子地图的文本内容。  
- `mapData`：存储解析结果的 `MapData` 对象。  
- `subMapIndex`：子地图的索引。

**返回值：**  
无；如果解析失败抛出异常。

**示例：**
```java
List<String> subMapContent = ["4 5", "1 0 0 0 0"];
MapData mapData = new MapData();
MapFileParser.parseSingleSubmap(subMapContent, mapData, 0);
```

---

### 8. `parseSubmapDimensions`
**方法签名：**  
```java
private static void parseSubmapDimensions(String dimensionLine, MapData mapData, int subMapIndex)
```

**功能：**  
解析子地图的高宽信息。

**参数：**  
- `dimensionLine`：高宽定义行。  
- `mapData`：存储解析结果的 `MapData` 对象。  
- `subMapIndex`：子地图的索引。

**返回值：**  
无；如果解析失败抛出异常。

**示例：**
```java
String dimensionLine = "4 5";
MapData mapData = new MapData();
MapFileParser.parseSubmapDimensions(dimensionLine, mapData, 0);
// mapData.allMaps.get(0) = new Things[4][5];
```

---

### 9. `parseSubmapRow`
**方法签名：**  
```java
private static void parseSubmapRow(String rowContent, MapData mapData, int subMapIndex, int rowIndex)
```

**功能：**  
解析子地图中的一行数据，并存储到 `Things[][]` 数组中。

**参数：**  
- `rowContent`：行数据字符串。  
- `mapData`：存储解析结果的 `MapData` 对象。  
- `subMapIndex`：子地图索引。  
- `rowIndex`：当前行索引。

**返回值：**  
无；如果解析失败抛出异常。

**示例：**
```java
String rowContent = "1 0 0 0 0";
MapData mapData = new MapData();
MapFileParser.parseSubmapRow(rowContent, mapData, 0, 0);
```

---

## **完整示例**

```java
String mapFileString = """
附加信息
1
3 3
0 0 0
0 1 0
0 0 0
""";

MapInfo mapInfo = new MapInfo("Demo Map", "Author");
MapData mapData = MapFileParser.parseMapData(mapInfo, mapFileString);
if (mapData != null) {
    System.out.println("地图解析成功！");
} else {
    System.out.println("地图解析失败！");
}
```