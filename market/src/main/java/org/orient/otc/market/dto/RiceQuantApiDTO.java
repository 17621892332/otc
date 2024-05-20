package org.orient.otc.market.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Set;

@Data
public class RiceQuantApiDTO {

    private String method;
    @JSONField(name = "order_book_ids")
    private Set<String> orderBookIds;
}
