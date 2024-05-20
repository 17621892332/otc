package org.orient.otc.yl.dto;

import lombok.Data;

import java.util.List;

@Data
public class TradeDelSyncDto {

    private List<Integer> tradeIdList;

    private String tradeNumber;
}
