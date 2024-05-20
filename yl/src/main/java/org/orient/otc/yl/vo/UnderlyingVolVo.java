package org.orient.otc.yl.vo;

import lombok.Data;
import org.orient.otc.yl.entity.SingleVol;

import java.util.List;

/**
 * @author dzrh
 */
@Data
public class UnderlyingVolVo {
    /**
     * 标的代码
     */
    String underlyingCode;
    /**
     * 波动率曲面
     */
    List<SingleVol> volTable;
    /**
     * 波动率Bid曲面
     */
    List<SingleVol> volTableBid;
    /**
     * 波动率Ask曲面
     */
    List<SingleVol> volTableAsk;

}
