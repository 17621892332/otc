package org.orient.otc.quote.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.orient.otc.api.quote.vo.TradeObsDateVO;

import java.io.IOException;
import java.util.List;

public class TradeObsDateListTypeHandler extends JacksonTypeHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param type
     */
    public TradeObsDateListTypeHandler(Class<Object> type) {
        super(type);
    }

    @Override
    protected Object parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<TradeObsDateVO>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
