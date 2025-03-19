package com.katomegumi.zxpicture.infrastructure.common;


import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 响应类
 * @param <T>
 */

@Data
@AllArgsConstructor
public class BaseResponse <T> implements Serializable {
    private int code;

    private T data;

    private String message;

    public BaseResponse(int code,String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),errorCode.getMessage());
    }
}
