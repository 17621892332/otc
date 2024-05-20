package org.orient.otc.yl.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenCloseInfoVo {

    private OpenInfoVo openInfo;

    private List<TradeCloseInfoVo> closeInfos;
}
