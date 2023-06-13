package com.bazinga.exception;

/**
 * 〈重复异常〉<p>
 * 数据重复插入、重复调用等情况下，抛出该异常
 *
 * @author yunshan
 * @date 17/12/21
 */
public class DuplicationException extends RuntimeException {

    private static final long serialVersionUID = -901461454831685391L;

    public DuplicationException(String message) {
        this(message, null);
    }

    public DuplicationException(Throwable cause) {
        this(null, cause);
    }

    public DuplicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
