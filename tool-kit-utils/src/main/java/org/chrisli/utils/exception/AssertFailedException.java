package org.chrisli.utils.exception;

/**
 * [断言验证失败异常]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class AssertFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AssertFailedException() {
        super();
    }

    public AssertFailedException(String errorMsg) {
        super(errorMsg);
    }

    public AssertFailedException(String errorMsg, Throwable t) {
        super(errorMsg, t);
    }

}
