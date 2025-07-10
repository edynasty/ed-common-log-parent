-- 主表
CREATE TABLE ed_sys_log (
                            id BIGINT PRIMARY KEY COMMENT '主键ID',
                            parent_id BIGINT COMMENT '父日志ID',
                            name VARCHAR(255) COMMENT '日志名称',
                            log_type VARCHAR(50) COMMENT '操作类型',
                            operation VARCHAR(255) COMMENT '操作描述',
                            user_id BIGINT COMMENT '用户ID',
                            user_name VARCHAR(64) COMMENT '用户名',
                            tenant_id BIGINT COMMENT '租户ID',
                            trace_id BIGINT COMMENT '链路ID',
                            run_time DOUBLE COMMENT '执行时间（秒）',
                            create_time DATETIME COMMENT '操作时间',
                            application_name VARCHAR(64) COMMENT '应用名',
                            class_name VARCHAR(255) COMMENT '类名',
                            method_name VARCHAR(255) COMMENT '方法名',
                            request_ip VARCHAR(64) COMMENT '请求IP',
                            request_status INT COMMENT '请求状态（0失败 1成功）',
                            request_method VARCHAR(10) COMMENT 'HTTP方法',
                            request_os VARCHAR(64) COMMENT '操作系统',
                            request_browser VARCHAR(64) COMMENT '浏览器类型',
                            KEY idx_trace_id (trace_id),
                            KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志主表';

-- 详情表
CREATE TABLE ed_sys_log_detail (
                                   id BIGINT PRIMARY KEY COMMENT '主键ID',
                                   log_id BIGINT COMMENT '关联主表ID',
                                   request_args LONGTEXT COMMENT '请求参数',
                                   request_result LONGTEXT COMMENT '响应结果',
                                   error_msg LONGTEXT COMMENT '错误信息',
                                   KEY idx_log_id (log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志详情表';