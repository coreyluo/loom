package com.bazinga.exception;

/**
 * 〈参数异常〉<p>
 * 参数不符合要求（字段为空，格式不正确等）时，抛出该异常
 *
 * @author yunshan
 * @date 17/9/30
 */
public class ArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = -1230239169670994589L;

    private static final String ARG_ERROR = "字段[%s]%s：%s";

    public ArgumentException(String field, String errorReason, Object value){
        this(String.format(ARG_ERROR, field, errorReason, value));
    }

    public ArgumentException(String message) {
        this(message, null);
    }

    public ArgumentException(Throwable cause) {
        this(null, cause);
    }

    public ArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
