package org.orient.otc.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document(indexName = "request-log-#{@profileActive}")
public class RequestLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 服务名
     */
    private String serverName;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求参数
     */
    private String requestInfo;

    /**
     * 请求ip
     */
    private String ip;

    /**
     * 响应内容
     */
    private String responseData;

    /**
     * 请求用户ID
     */
    private Integer requestUserId;
    /**
     * 请求用户名称
     */
    private String requestUserName;

    /**
     * 请求耗时
     */
    private Long spendTimes;

    /**
     * 请求时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone="GMT+8")
    private Date requestTime;

}
