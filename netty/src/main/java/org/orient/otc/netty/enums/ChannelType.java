package org.orient.otc.netty.enums;

import java.util.HashMap;
import java.util.Map;

public enum ChannelType {
   CTP_MD("ctp_md","柜台行情"),

   /**
    * 风险汇总
    */
   RISK_TOTAL("RISK_TOTAL","风险汇总"),

   /**
    * 调整DeltaLost
    */
   DELTA_ADJUSTMENT("DELTA_ADJUSTMENT","调整DeltaLost"),

   /**
    * 调整风险计算时间
    */
   EDIT_RISK_TIME("EDIT_RISK_TIME","调整风险计算时间"),

   /**
    * 调整交易计算波动率
    */
   EDIT_RISK_VOL("EDIT_RISK_VOL","调整交易计算波动率"),
   ;

   ChannelType(String name,String desc) {
      this.desc = desc;
      this.name = name;
   }

   private final String name;

   private final String desc;

   public String getName() {
      return name;
   }

   public String getDesc() {
      return desc;
   }

   public static Map<String,ChannelType> getChannelTypeMap(){
      Map<String,ChannelType> channelTypeMap = new HashMap<>();
      for(ChannelType channelType : ChannelType.values()){
         channelTypeMap.put(channelType.getName(),channelType);
      }
      return channelTypeMap;
   }

}
