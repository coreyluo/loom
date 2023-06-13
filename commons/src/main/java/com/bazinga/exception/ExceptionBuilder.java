package com.bazinga.exception;

/**
 * 〈自定义异常Builder〉<p>
 * 自定义异常构造器
 *
 * @author yunshan
 * @date 17/12/4
 */
public class ExceptionBuilder {

    /**
     * 参数异常
     * @param field 字段名
     * @param errorReason 错误原因
     * @param value 字段值
     * @return
     */
    public static ArgumentException argument(String field, String errorReason, Object value){
        return new ArgumentException(field, errorReason, value);
    }

    /**
     * 参数异常
     * @param message 异常消息
     * @return
     */
    public static ArgumentException argument(String message){
        return argument(message, null);
    }

    /**
     * 参数异常
     * @param message 异常消息
     * @param e 原始异常
     * @return
     */
    public static ArgumentException argument(String message, Exception e){
        return new ArgumentException(message, e);
    }

    /**
     * 业务异常
     * @param message 异常消息
     * @return
     */
    public static BusinessException business(String message){
        return business(message, null);
    }

    /**
     * 业务异常
     * @param message 异常消息
     * @param e 原始异常
     * @return
     */
    public static BusinessException business(String message, Exception e){
        return new BusinessException(message, e);
    }

    /**
     * 超时异常
     * @param execMills 执行时长(ms)
     * @return
     */
    public static TimeoutException timeout(long execMills){
        return new TimeoutException(execMills);
    }

    /**
     * 超时异常
     * @param message 异常消息
     * @return
     */
    public static TimeoutException timeout(String message){
        return timeout(message, null);
    }

    /**
     * 超时异常
     * @param message 异常消息
     * @param e 原始异常
     * @return
     */
    public static TimeoutException timeout(String message, Exception e){
        return new TimeoutException(message, e);
    }

    /**
     * 重复异常
     * @param message 异常消息
     * @return
     */
    public static DuplicationException duplicate(String message){
        return duplicate(message, null);
    }

    /**
     * 重复异常
     * @param message 异常消息
     * @param e 原始异常
     * @return
     */
    public static DuplicationException duplicate(String message, Exception e){
        return new DuplicationException(message, e);
    }


    /**
     * 系统异常
     * @param message 异常消息
     * @return
     */
    public static SystemException system(String message){
        return system(message, null);
    }

    /**
     * 系统异常
     * @param message 异常消息
     * @param e 原始异常
     * @return
     */
    public static SystemException system(String message, Exception e){
        return new SystemException(message, e);
    }

}
