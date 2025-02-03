package com.katomegumi.zxpicturebackend.exception;

/**
 * 异常工具类 断言类
 */
public class ThrowUtils {
    /**
     * 条件成立抛异常
     * @param condition
     * @param runtimeException
     */
    public static void ThrowIf(boolean condition,RuntimeException runtimeException) {
        if(condition) {
            throw runtimeException;
        }
    }
    public static void ThrowIf(boolean condition,ErrorCode errorCode) {
        ThrowIf(condition,new BusinessException(errorCode));
    }

    public static void ThrowIf(boolean condition,ErrorCode errorCode,String message) {
        ThrowIf(condition,new BusinessException(errorCode,message));
    }
}
