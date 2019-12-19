package cc.yihy.tool.util;

import cc.yihy.tool.exception.RetryException;

/**
 * 重试工具类
 * <p>
 * Create at 2018/1/9
 */
public class RetryUtils {

    public static final int DEFAULT_RETRIES = 3;

    /**
     * 多次重试
     *
     * @param supplier 执行的方法
     * @param <T>      返回值
     * @return 执行结果
     */
    public static <T> T retry(Supplier<T> supplier, String message) {
        return retry(DEFAULT_RETRIES, supplier, message);
    }

    /**
     * 多次重试
     *
     * @param consumer 执行的方法
     * @return 执行结果
     */
    public static void retry(Consumer consumer, String message) {
        retry(DEFAULT_RETRIES, consumer, message);
    }

    /**
     * 多次重试
     *
     * @param times    重试次数
     * @param consumer 执行的方法
     * @return 执行结果
     */
    public static void retry(int times, Consumer consumer, String message) {
        RuntimeException exception = null;
        for (int i = 0; i < times; i++) {
            try {
                consumer.accept();
                return;
            } catch (RuntimeException e) {
                exception = e;
            }
        }
        throw new RetryException(message, exception);
    }

    /**
     * 多次重试
     *
     * @param times    重试次数
     * @param supplier 执行的方法
     * @param <T>      返回值
     * @return 执行结果
     */
    public static <T> T retry(int times, Supplier<T> supplier, String message) {
        RuntimeException exception = null;
        for (int i = 0; i < times; i++) {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                exception = e;
            }
        }
        throw exception;
    }

    /**
     * 配合{@link #retry(int, Supplier, String)}使用的方法接口
     */
    @FunctionalInterface
    public interface Supplier<T> {

        T get() throws RuntimeException;
    }

    /**
     * 配合{@link #retry(int, Supplier, String)}使用的方法接口
     */
    @FunctionalInterface
    public interface Consumer {

        void accept() throws RuntimeException;
    }
}
