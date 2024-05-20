package org.orient.otc.openapi.vo;

import lombok.Data;

@Data
public class MarketInfoVo {

    private String instrumentId;

    private String lastPrice;

    private String updateTime;
}
