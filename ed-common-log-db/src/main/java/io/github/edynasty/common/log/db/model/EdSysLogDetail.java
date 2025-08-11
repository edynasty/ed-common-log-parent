package io.github.edynasty.common.log.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.edynasty.common.log.core.model.SysLogger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统日志
 *
 * @author tangxingpeng
 * @since 2025/3/21 15:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EdSysLogDetail {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 日志ID
     */
    private Long logId;
    /**
     * 调用参数
     */
    private String requestArgs;
    /**
     * 调用返回结果
     */
    private String requestResult;
    /**
     * 错误信息
     */
    private String errorMsg;

    public EdSysLogDetail(SysLogger logger) {
        this.logId = logger.getId();
        this.requestArgs = logger.getRequestArgs();
        this.requestResult = logger.getRequestResult();
        this.errorMsg = logger.getErrorMsg();
    }

}
