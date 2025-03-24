package io.github.edynasty.common.log.core.listener;

import io.github.edynasty.common.log.core.model.EdLogEvent;

import java.util.EventListener;

/**
 * 日志事件监听器
 *
 * @author tangxingpeng
 * @since 2022/2/21 9:48 上午
 */
@FunctionalInterface
public interface EdLogEventListener extends EventListener {

    /**
     * 系统运行结束
     *
     * @param logger SysLogger
     */
    void eventEnd(EdLogEvent logger);

}
