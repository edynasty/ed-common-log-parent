package io.github.edynasty.common.log.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.github.edynasty.common.log.core.model.SysLogger;

import java.util.Stack;

/**
 * 日志上下文
 *
 * @author tangxingpeng
 * @since 2023/8/29 16:35
 */
public class SysLoggerContext {

    /**
     * 使用栈保存日志链路（兼容线程池传递）
     */
    private static final ThreadLocal<Stack<SysLogger>> CONTEXT = new TransmittableThreadLocal<>();

    public static void clear() {
        CONTEXT.remove();
    }

    public static void push(SysLogger logger) {
        Stack<SysLogger> stack = CONTEXT.get();
        if (stack == null) {
            stack = new Stack<>();
            CONTEXT.set(stack);
        }
        stack.push(logger);
    }

    public static SysLogger getCurrent() {
        Stack<SysLogger> stack = CONTEXT.get();
        return stack != null && !stack.isEmpty() ? stack.peek() : new SysLogger();
    }

    public static void pop() {
        Stack<SysLogger> stack = CONTEXT.get();
        if (stack != null && !stack.isEmpty()) {
            stack.pop();
            if (stack.isEmpty()) {
                CONTEXT.remove();
            }
        }
    }

}
