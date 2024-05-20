package org.orient.otc.quote.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orient.otc.quote.dto.volatility.VolatityDeltaDataDto;

import java.io.IOException;
import java.util.List;

public class VolatityDeltaDataListTypeHandler extends JacksonTypeHandler {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public VolatityDeltaDataListTypeHandler(Class<Object> type) {
        super(type);
    }

    @Override
    protected Object parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<VolatityDeltaDataDto>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
