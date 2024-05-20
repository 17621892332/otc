package org.orient.otc.api.quote.dto;

import lombok.Data;

import java.util.List;

/**
 * @author dzrh
 */
@Data
public class SyncUpdateDto {

    private List<Integer> ids;

    private Integer syncStatus;

    private String msg;
}
