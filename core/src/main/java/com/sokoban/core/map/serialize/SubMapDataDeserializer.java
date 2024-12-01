package com.sokoban.core.map.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sokoban.core.game.ObjectType;
import com.sokoban.core.map.SubMapData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubMapDataDeserializer extends JsonDeserializer<SubMapData> {
    @Override
    public SubMapData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        int height = node.get("height").asInt(); // 获取 height
        int width = node.get("width").asInt();   // 获取 width

        List<ObjectType[][]> mapLayer = new ArrayList<>();
        ArrayNode layersNode = (ArrayNode) node.get("mapLayer");

        for (JsonNode layerNode : layersNode) {
            ObjectType[][] layer = new ObjectType[height][width];
            int rowIndex = 0;

            for (JsonNode rowNode : layerNode) {
                ObjectType[] row = new ObjectType[width];
                int colIndex = 0;

                for (JsonNode cellNode : rowNode) {
                    row[colIndex++] = ObjectType.valueOf(cellNode.asText()); // 转换回枚举
                }

                layer[rowIndex++] = row;
            }

            mapLayer.add(layer);
        }

        SubMapData subMapData = new SubMapData(height, width);
        subMapData.mapLayer = mapLayer;
        return subMapData;
    }
}

