package io.github.edynasty.common.log.core.service;

import io.github.edynasty.common.log.core.context.SysLoggerContext;
import io.github.edynasty.common.log.core.model.SysLogger;

/**
 * 审计日志接口
 *
 * @author tangxingpeng
 * @since 2020/12/26 22:47
 */
public interface BaseLogService {

    /**
     * 保存审计
     *
     * @param logger logger
     */
    void save(SysLogger logger);

    /**
     * 设置审计
     *
     * @param logger logger
     */
    default void set(SysLogger logger) {
        SysLoggerContext.push(logger);
    }

    /**
     * 获取当前审计
     */
    default SysLogger getCurrent() {
        return SysLoggerContext.getCurrent();
    }

    /**
     * 弹出审计
     */
    default void pop() {
        SysLoggerContext.pop();
    }

    /**
     * 清除审计
     */
    default void clear() {
        SysLoggerContext.clear();
    }

}
