package org.orient.otc.message.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReSendMailDto {
    /**
     * 邮件记录id集合
     */
    List<Integer> id;
}
