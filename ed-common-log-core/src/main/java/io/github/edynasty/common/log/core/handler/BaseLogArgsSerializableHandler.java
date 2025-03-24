package io.github.edynasty.common.log.core.handler;

/**
 * 基础日志参数序列化处理器
 *
 * @author tangxingpeng
 * @since 2025/3/22 00:08
 */
public interface BaseLogArgsSerializableHandler {

    /**
     * 序列化参数
     *
     * @param args 参数
     * @return 序列化后的参数
     */
    String serializeArgs(Object[] args);

    /**
     * 序列化结果
     *
     * @param result 结果
     * @return 序列化后的结果
     */
    String serializeResult(Object result);

    /**
     * 序列化异常
     *
     * @param throwable 异常
     * @return 序列化后的异常
     */
    String serializeException(Throwable throwable);

}
