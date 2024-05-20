package org.orient.otc.api.quote.enums;

import lombok.Getter;


/**
 * 抵押枚举
 */
@Getter
public class CollateralEnum {

    /**
     * 抵押状态枚举
     */
    @Getter
    public enum CollateralStatusEnum {
        /**
         * 抵押
         */
        collateral("collateral","抵押"),
        /**
         * 赎回
         */
        redemption("redemption","赎回");

        private final String key;
        private final String value;

        CollateralStatusEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }

    /**
     * 执行状态枚举
     */
    @Getter
    public enum ExecuteStatusEnum {
        /**
         * 未确认
         */
        unconfirmed("unconfirmed", "未确认"),
        /**
         * 已确认
         */
        confirmed("confirmed", "已确认"),
        /**
         * 拒绝
         */
        refuse("refuse", "拒绝");
        private final String key;
        private final String value;

        ExecuteStatusEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
