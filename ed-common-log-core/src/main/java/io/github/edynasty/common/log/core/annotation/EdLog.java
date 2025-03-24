package io.github.edynasty.common.log.core.annotation;

import io.github.edynasty.common.log.core.handler.BaseLogArgsSerializableHandler;
import io.github.edynasty.common.log.core.handler.impl.DefaultLogArgsSerializableHandler;

import java.lang.annotation.*;

/**
 * 标记需要做业务日志的方法
 *
 * @author yubaoshan
 * @since 2017/3/31 12:46
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EdLog {

    /**
     * 业务的名称,例如:"修改菜单"
     */
    String title() default "";

    /**
     * 业务操作类型枚举
     */
    String type() default "OTHER";

    /**
     * 数据spEL表达式
     */
    String key() default "";

    /**
     * 是否需要参数
     */
    boolean needArgs() default false;

    /**
     * 是否需要结果
     */
    boolean needResult() default false;

    /**
     * 是否需要错误信息
     */
    boolean needErrorMsg() default false;

    /**
     * 序列化处理器
     */
    Class<? extends BaseLogArgsSerializableHandler> serializableHandler() default DefaultLogArgsSerializableHandler.class;
}
