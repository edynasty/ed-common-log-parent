package io.github.edynasty.common.log.core.enums;

import lombok.Getter;

/**
 * 日志注解操作类型枚举
 *
 * @author zyf
 * @since 2021/6/10
 */
@Getter
public enum LogTypeEnum {

    /**
     * 其它
     */
    OTHER,

    /**
     * 增加
     */
    ADD,

    /**
     * 删除
     */
    DELETE,

    /**
     * 编辑
     */
    EDIT,

    /**
     * 更新
     */
    UPDATE,

    /**
     * 查询
     */
    QUERY,

    /**
     * 详情
     */
    DETAIL,

    /**
     * 批量
     */
    BATCH,

    /**
     * 新增或更新
     */
    SAVE_OR_UPDATE,

    /**
     * 树
     */
    TREE,

    /**
     * 导入
     */
    IMPORT,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 授权
     */
    GRANT,

    /**
     * 强退
     */
    FORCE,

    /**
     * 清空
     */
    CLEAN,

    /**
     * 修改状态
     */
    CHANGE_STATUS,

    /**
     * 发送消息
     */
    SEND_MSG,

    /**
     * 生效
     */
    EFFECT,

    /**
     * 调用
     */
    REST_TEMPLATE,

    /**
     * webService
     */
    WEB_SERVICE,

    /**
     * feign
     */
    FEIGN,

    /**
     * 批准
     */
    APPROVE,
}
