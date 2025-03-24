package io.github.edynasty.common.log.core.model;

import java.util.EventObject;

/**
 * @author tangxingpeng
 * @since 2022/2/21 9:55 上午
 */
public class EdLogEvent extends EventObject {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     */
    public EdLogEvent(SysLogger source) {
        super(source);
    }

}
