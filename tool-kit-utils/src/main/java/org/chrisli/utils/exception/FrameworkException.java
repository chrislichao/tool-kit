package org.chrisli.utils.exception;

/**
 * [框架基础方法产生的异常]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class FrameworkException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FrameworkException() {
        super();
    }

    public FrameworkException(String errorMsg) {
        super(errorMsg);
    }

    public FrameworkException(String errorMsg, Throwable t) {
        super(errorMsg, t);
    }

}
