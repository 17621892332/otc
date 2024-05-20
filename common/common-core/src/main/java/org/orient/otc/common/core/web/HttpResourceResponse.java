package org.orient.otc.common.core.web;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.orient.otc.common.core.exception.BaseException;

import java.io.Serializable;

/**
 * 统一响应
 * @param <T> 响应对象
 */
@Getter
@ApiModel("统一响应")
public class HttpResourceResponse<T> implements Serializable {

    private static final long serialVersionUID = 3702894019834009719L;
    @ApiModelProperty("状态码")
    private int code = 0;
    @ApiModelProperty("响应消息")
    private String message = "success";
    private T data;


    private HttpResourceResponse(T data) {

        this.data = data;

    }


    private HttpResourceResponse(T data, String message) {

        this.data = data;

        this.message = message;

    }

    /**
     * 通过code 和message响应
     * @param code 响应代码
     * @param message 响应信息
     */
    public HttpResourceResponse(int code, String message) {

        this.code = code;

        this.message = message;

    }

    private HttpResourceResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }
    private HttpResourceResponse(int code, T data,String message) {
        this.code = code;
        this.data = data;
        this.message=message;
    }

    public static HttpResourceResponse<Boolean> success() {

        return new HttpResourceResponse<>(Boolean.TRUE);

    }


    public static HttpResourceResponse<Boolean> successWithMessage(String message) {

        return success(Boolean.TRUE, message);

    }


    public static <T> HttpResourceResponse<T> success(T data) {

        return new HttpResourceResponse<>(data);

    }

    public static <T> HttpResourceResponse<T> success(T data, String message) {

        return new HttpResourceResponse<>(data, message);

    }

    public static <T> HttpResourceResponse<T> success(int code, T data) {

        return new HttpResourceResponse<>(code, data);

    }

    public static HttpResourceResponse<?> error(int code, String message) {
        return new HttpResourceResponse<>(code, message);
    }
    public static <T> HttpResourceResponse<T> error(int code,T data, String message) {
        return new HttpResourceResponse<>(code,data, message);
    }
    public static <E extends BaseException> HttpResourceResponse<?> error(E error) {

        return error(error.getResponseEnum().getCode(), error.getMessage());

    }
}
