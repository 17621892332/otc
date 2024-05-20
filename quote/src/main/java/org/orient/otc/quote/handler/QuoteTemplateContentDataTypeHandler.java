package org.orient.otc.quote.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orient.otc.quote.entity.QuoteTemplateContentData;

import java.io.IOException;

public class QuoteTemplateContentDataTypeHandler extends JacksonTypeHandler {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public QuoteTemplateContentDataTypeHandler(Class<Object> type) {
        super(type);
    }

    @Override
    protected Object parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<QuoteTemplateContentData>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
