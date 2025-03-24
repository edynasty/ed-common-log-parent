package io.github.edynasty.common.log.core.handler;

import io.github.edynasty.common.log.core.model.BaseLogUser;

/**
 * @author tangxingpeng
 * @since 2025/3/21 16:49
 */
public interface BaseLogUserHandler {

    /**
     * 获取当前用户
     *
     * @return SysUser
     */
    BaseLogUser getCurrentUser();

}
