package io.github.edynasty.common.log.core.context;

import cn.hutool.core.collection.CollectionUtil;
import io.github.edynasty.common.log.core.listener.EdLogEventListener;
import io.github.edynasty.common.log.core.model.EdLogEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author tangxingpeng
 * @since 2022/8/30 14:25
 */
@Slf4j
public class EdLogListenerContext {

    private List<EdLogEventListener> listeners;

    public EdLogListenerContext(List<EdLogEventListener> listeners) {
        // 无论传入是否为空，强制初始化为不可变空列表
        this.listeners = CollectionUtil.isEmpty(listeners)
                ? Collections.emptyList()
                : new ArrayList<>(listeners);
    }

    public void setListeners(Collection<? extends EdLogEventListener> listeners) {
        this.listeners = CollectionUtil.isEmpty(listeners)
                ? Collections.emptyList()
                : new ArrayList<>(listeners);
    }

    public void addListeners(EdLogEventListener... listeners) {
        if (listeners != null) {
            this.listeners.addAll(Arrays.asList(listeners));
        }
    }

    /**
     * 系统运行结束
     *
     * @param logger SysLogger
     */
    public void eventEnd(EdLogEvent logger) {
        if (CollectionUtil.isNotEmpty(listeners)) {
            listeners.forEach(listener -> listener.eventEnd(logger));
        }
    }

}
