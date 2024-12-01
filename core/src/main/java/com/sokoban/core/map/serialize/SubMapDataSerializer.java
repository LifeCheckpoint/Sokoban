package com.sokoban.core.map.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sokoban.core.game.ObjectType;
import com.sokoban.core.map.SubMapData;

public class SubMapDataSerializer extends JsonSerializer<SubMapData> {
    @Override
    public void serialize(SubMapData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("height", value.height);
        gen.writeNumberField("width", value.width);

        gen.writeArrayFieldStart("mapLayer");
        for (ObjectType[][] layer : value.mapLayer) {
            gen.writeStartArray();
            for (ObjectType[] row : layer) {
                gen.writeStartArray();
                for (ObjectType cell : row) {
                    gen.writeString(cell.name()); // 写入枚举名称
                }
                gen.writeEndArray();
            }
            gen.writeEndArray();
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}

