package org.orient.otc.quote.handler;

import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orient.otc.quote.dto.volatility.VolatityDataDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.List;

public class VolatityDataListTypeHandler extends FastjsonTypeHandler {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public VolatityDataListTypeHandler(Class<Object> type) {
        super(type);
    }

    @Override
    protected Object parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<VolatityDataDto>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
