package org.orient.otc.yl.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SyncMarketCloseDataDto {

    private LocalDate startDate;
    Boolean isOnlyToday;
}
