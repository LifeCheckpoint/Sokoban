# 注意

***该类的方法已经由文本解析转为序列化 / 反序列化，原功能已被取代***

## 反序列化

负责将读入的地图文本字符串转化为地图对象，并且附加上 mapFileInfo

```java
    public static MapData parseMapData(MapInfo mapFileInfo, String mapFileString); 
```

## 序列化

负责将地图对象去除 mapFileInfo 后转化为文本字符串


```java
    public static String serializeMapData(MapData mapData);
```

## 传统格式地图读取支持

通过 `transformCharMapToIntegerMap` 转换传统格式的地图，可用于测试和导入关卡

---

*如果有任何补充，需要及时更新该文档*
