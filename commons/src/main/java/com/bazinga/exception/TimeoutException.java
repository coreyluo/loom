package com.bazinga.exception;

/**
 * 〈超时异常〉<p>
 * 执行业务方法超时后，抛出该异常
 *
 * @author zixiao
 * @date 16/11/23
 */
public class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = 262632580841180603L;

    private long execMills;

    /**
     * 构造函数
     * @param execMills 执行时长(ms)
     */
    public TimeoutException(long execMills){
        this("执行超时，耗时超过"+execMills+"ms.");
        this.execMills = execMills;
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public long getExecMills() {
        return execMills;
    }
}