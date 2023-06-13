package com.bazinga.exception;

/**
 * 〈系统异常〉<p>
 * 对于不可预知的异常或者Bug引起的异常出现时，抛出该异常
 *
 * @author zixiao
 * @date 17/12/4
 */
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 7817445559865341560L;

    public SystemException(String message) {
        this(message, null);
    }

    public SystemException(Throwable cause) {
        this(null, cause);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
