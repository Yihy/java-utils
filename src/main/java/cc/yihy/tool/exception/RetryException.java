package cc.yihy.tool.exception;

/**
 * 任务执行异常调用异常
 *
 * Create at 2018/3/23
 */
public class RetryException extends RuntimeException{

    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }
}
