package org.orient.otc.openapi.dto;

import lombok.Data;

@Data
public class StatusConvert {
    private Data data;

    public static StatusConvert createWithId(String id) {
        StatusConvert statusConvert = new StatusConvert();
        statusConvert.setData(new Data(id));
        return statusConvert;
    }
    @lombok.Data
    public static class Data {
        private String id;

        public Data(String id) {
            this.id = id;
        }
    }
}

