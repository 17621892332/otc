package org.orient.otc.openapi.vo;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class Token {
    private org.orient.otc.openapi.vo.Data data;
    private String state;
    private boolean status;
}
