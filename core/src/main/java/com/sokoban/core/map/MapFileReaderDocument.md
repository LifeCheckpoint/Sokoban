# MapFileReader 类文档

## 概述

`MapFileReader` 是一个用于处理地图文件的工具类。它提供了地图文件的读取、列举、创建等功能，同时支持简单的错误处理。  
此类常用于 Sokoban 游戏地图的管理。

---

## 类属性

### **默认常量**

- `DEFAULT_MAPS_DIRECTORY`  
  默认的地图存储目录路径：`"./test-files/bin/maps"`。
  
- `MAP_FILE_EXTENSION`  
  地图文件的扩展名：`.map`。

### **成员变量**

- `mapsDirectory`  
  地图库的根目录路径，用户可以自定义。

---

## 构造方法

### **`MapFileReader()`**  
默认构造方法，设置地图根目录为 `DEFAULT_MAPS_DIRECTORY`。

### **`MapFileReader(String mapsDirectory)`**  
自定义构造方法，允许用户指定地图存储的根目录。

参数：  
- `mapsDirectory`：地图文件存储的目录路径。

---

## 公共方法

### **`listAllMaps()`**  
在地图根目录中列出所有地图文件的基本信息。

#### 返回值  
- 类型：`List<MapInfo>`  
  包含地图文件路径、相对路径和地图名称的列表。

#### 工作逻辑  
1. 检查 `mapsDirectory` 是否存在且为目录。
2. 使用 `Files.walk` 遍历目录，筛选符合 `.map` 扩展名的文件。
3. 提取地图文件的路径、相对路径和名称，并将其包装为 `MapInfo` 对象。

---

### **`readMapByLevelAndName(String levelName, String mapName)`**  
从指定的 `levelName` 文件夹中读取名称为 `mapName` 的地图内容。

#### 参数  
- `levelName`：地图所在的关卡文件夹名。
- `mapName`：地图名称（不包含扩展名）。

#### 返回值  
- 类型：`String`  
  成功读取时返回地图内容，失败时返回 `null`。

---

### **`readMapByPath(String path)`**  
直接从指定路径读取地图文件的内容。

#### 参数  
- `path`：地图文件的绝对路径。

#### 返回值  
- 类型：`String`  
  成功时返回地图内容；失败时返回 `null` 并打印错误信息。

---

### **`createEmptyMap(String path)`**  
创建一个内容为空的地图文件。

#### 参数  
- `path`：地图文件的绝对路径。

#### 返回值  
- 类型：`boolean`  
  返回 `true` 表示文件创建成功，`false` 表示失败。

---

### **`createMapWithContent(String path, String content)`**  
在指定路径创建一个包含内容的地图文件。

#### 参数  
- `path`：地图文件的绝对路径。
- `content`：地图文件的初始内容。

#### 返回值  
- 类型：`boolean`  
  返回 `true` 表示文件创建成功，`false` 表示失败。

#### 工作逻辑  
1. 检查文件是否已存在，若存在则返回 `false`。
2. 若目录不存在，自动创建目录。
3. 写入指定内容，创建地图文件。

---

## 示例代码

### **构造实例并列出地图**
```java
MapFileReader reader = new MapFileReader();
List<MapInfo> maps = reader.listAllMaps();

for (MapInfo map : maps) {
    System.out.println("Map Name: " + map.getMapName());
    System.out.println("Path: " + map.getPath());
    System.out.println("Relative Path: " + map.getRelativePath());
}
```

### **读取地图内容**
```java
String content = reader.readMapByLevelAndName("level1", "map1");
if (content != null) {
    System.out.println("Map Content: \n" + content);
} else {
    System.out.println("Failed to load map.");
}
```

### **创建新地图**
```java
boolean success = reader.createMapWithContent("./test-files/bin/maps/level1/map2.map", "####\n# P#\n####");
if (success) {
    System.out.println("Map created successfully!");
} else {
    System.out.println("Failed to create map.");
}
```

---

## 注意事项

1. **文件路径有效性**：使用 `Path` 类处理路径，避免平台差异。
2. **异常处理**：当操作文件失败时，方法会捕获 `IOException` 并打印错误日志，返回适当的值。
3. **文件覆盖**：创建文件时若文件已存在，则不会覆盖原文件。
