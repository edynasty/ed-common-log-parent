CREATE TABLE ed_sys_log (
                            id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                            parent_id NUMBER,
                            name VARCHAR2(255),
                            log_type VARCHAR2(50),
                            operation VARCHAR2(255),
                            user_id NUMBER,
                            user_name VARCHAR2(64),
                            tenant_id NUMBER,
                            trace_id NUMBER,
                            run_time NUMBER,
                            create_time DATE,
                            application_name VARCHAR2(64),
                            class_name VARCHAR2(255),
                            method_name VARCHAR2(255),
                            request_ip VARCHAR2(64),
                            request_status NUMBER(1),
                            request_method VARCHAR2(10),
                            request_os VARCHAR2(64),
                            request_browser VARCHAR2(64)
);
COMMENT ON TABLE ed_sys_log IS '系统日志主表';
COMMENT ON COLUMN ed_sys_log.id IS '主键ID';
COMMENT ON COLUMN ed_sys_log.parent_id IS '父日志ID';
COMMENT ON COLUMN ed_sys_log.name IS '日志名称';
COMMENT ON COLUMN ed_sys_log.log_type IS '接口操作类型';
COMMENT ON COLUMN ed_sys_log.operation IS '操作信息';
COMMENT ON COLUMN ed_sys_log.user_id IS '用户ID';
COMMENT ON COLUMN ed_sys_log.user_name IS '用户名';
COMMENT ON COLUMN ed_sys_log.tenant_id IS '租户ID';
COMMENT ON COLUMN ed_sys_log.trace_id IS '链路ID';
COMMENT ON COLUMN ed_sys_log.run_time IS '执行时间（秒）';
COMMENT ON COLUMN ed_sys_log.create_time IS '操作时间';
COMMENT ON COLUMN ed_sys_log.application_name IS '应用名';
COMMENT ON COLUMN ed_sys_log.class_name IS '类名';
COMMENT ON COLUMN ed_sys_log.method_name IS '方法名';
COMMENT ON COLUMN ed_sys_log.request_ip IS '调用者IP';
COMMENT ON COLUMN ed_sys_log.request_status IS '调用结果（0失败 1成功）';
COMMENT ON COLUMN ed_sys_log.request_method IS '请求方式（GET/POST等）';
COMMENT ON COLUMN ed_sys_log.request_os IS '操作系统';
COMMENT ON COLUMN ed_sys_log.request_browser IS '浏览器类型';

CREATE TABLE ed_sys_log_detail (
                                   id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                   log_id NUMBER,
                                   request_args CLOB,
                                   request_result CLOB,
                                   error_msg CLOB
);
COMMENT ON TABLE ed_sys_log_detail IS '系统日志详情表';
COMMENT ON COLUMN ed_sys_log_detail.id IS '主键ID';
COMMENT ON COLUMN ed_sys_log_detail.log_id IS '关联的主日志ID';
COMMENT ON COLUMN ed_sys_log_detail.request_args IS '调用参数';
COMMENT ON COLUMN ed_sys_log_detail.request_result IS '调用返回结果';
COMMENT ON COLUMN ed_sys_log_detail.error_msg IS '错误信息';