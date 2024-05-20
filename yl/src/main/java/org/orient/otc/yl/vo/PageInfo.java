package org.orient.otc.yl.vo;

import lombok.Data;

import java.util.List;

/**
 * @author dzrh
 */
@Data
public class PageInfo<T> {

    private Integer total;

    private Integer page;
    private  Integer totalPages;

    private List<T> rows;
}
