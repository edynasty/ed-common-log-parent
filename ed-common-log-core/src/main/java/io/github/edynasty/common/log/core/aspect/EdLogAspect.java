package io.github.edynasty.common.log.core.aspect;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import io.github.edynasty.common.log.core.annotation.EdLog;
import io.github.edynasty.common.log.core.context.EdLogListenerContext;
import io.github.edynasty.common.log.core.context.SysLoggerContext;
import io.github.edynasty.common.log.core.enums.LogRequestStatus;
import io.github.edynasty.common.log.core.handler.BaseLogArgsSerializableHandler;
import io.github.edynasty.common.log.core.handler.BaseLogUserHandler;
import io.github.edynasty.common.log.core.handler.impl.DefaultLogArgsSerializableHandler;
import io.github.edynasty.common.log.core.model.EdLogEvent;
import io.github.edynasty.common.log.core.model.SysLogger;
import io.github.edynasty.common.log.core.model.BaseLogUser;
import io.github.edynasty.common.log.core.properties.EdLogProperties;
import io.github.edynasty.common.log.core.service.BaseLogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 审计日志切面
 *
 * @author tangxingpeng
 * @since 2022/1/26 9:27 上午
 */
@Slf4j
@Aspect
@ConditionalOnClass({HttpServletRequest.class, RequestContextHolder.class})
public class EdLogAspect {

    private final static String DASH = "-";

    private static final String UNKNOWN = "unknown";

    @Value("${spring.application.name:unknown}")
    private String applicationName;
    /**
     * edLogProperties
     */
    private final EdLogProperties edLogProperties;
    /**
     * baseLogService
     */
    private final BaseLogService baseLogService;
    /**
     * baseLogUserService
     */
    private final BaseLogUserHandler baseLogUserService;
    /**
     * edLogListenerContext
     */
    private final EdLogListenerContext edLogListenerContext;
    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
    /**
     * 缓存处理器
     */
    private static final Map<Class<? extends BaseLogArgsSerializableHandler>, BaseLogArgsSerializableHandler> HANDLER_CACHE =
            new ConcurrentHashMap<>();

    public EdLogAspect(EdLogProperties edLogProperties,
                       BaseLogService baseLogService,
                       BaseLogUserHandler baseLogUserService,
                       EdLogListenerContext edLogListenerContext) {
        this.edLogProperties = edLogProperties;
        this.baseLogService = baseLogService;
        this.baseLogUserService = baseLogUserService;
        this.edLogListenerContext = edLogListenerContext;
    }

    @Around("@within(edLog) || @annotation(edLog)")
    public Object around(ProceedingJoinPoint joinPoint, EdLog edLog) throws Throwable {
        //判断功能是否开启
        if (!edLogProperties.getEnabled()) {
            return joinPoint.proceed();
        }

        //判断实现类存不存在
        if (baseLogService == null) {
            log.warn("EdLogAspect - logService is null");
            return joinPoint.proceed();
        }

        if (edLog == null) {
            // 获取类上的注解
            edLog = joinPoint.getTarget().getClass().getDeclaredAnnotation(EdLog.class);
        }

        //序列化处理器
        BaseLogArgsSerializableHandler serializableHandler = getSerializableHandler(edLog);

        //封装ApiLog
        SysLogger sysLogger = getSysLogger(edLog, joinPoint);
        baseLogService.set(sysLogger);
        //分装请求信息
        packageRequestMessage(sysLogger, edLog, serializableHandler, joinPoint);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = null;
        try {
            result = joinPoint.proceed();
            //成功
            if (Objects.isNull(sysLogger.getRequestStatus())) {
                sysLogger.setRequestStatus(LogRequestStatus.SUCCESS.getValue());
            }
        } catch (Exception e) {
            //运行异常
            sysLogger.setRequestStatus(LogRequestStatus.FAIL.getValue());
            if (edLog.needErrorMsg()) {
                sysLogger.setErrorMsg(serializableHandler.serializeException(e));
            }
            throw e;
        } finally {
            stopWatch.stop();
            //计算运行时间
            sysLogger.setRunTime(stopWatch.getTotalTimeSeconds());
            if (edLog.needResult()) {
                sysLogger.setRequestResult(serializableHandler.serializeResult(result));
            }
            baseLogService.save(sysLogger);
            //执行监听器
            edLogListenerContext.eventEnd(new EdLogEvent(sysLogger));
            // 移除
            baseLogService.pop();
        }
        return result;
    }

    /**
     * 缓存序列化处理器
     *
     * @param edLog edLog
     * @return BaseLogArgsSerializableHandler
     */
    private BaseLogArgsSerializableHandler getSerializableHandler(EdLog edLog) {
        Class<? extends BaseLogArgsSerializableHandler> handlerClass = edLog.serializableHandler();

        // 优先从缓存获取
        return HANDLER_CACHE.computeIfAbsent(handlerClass, clazz -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.warn("Failed to create handler: {}", clazz.getName(), e);
                // 回退到默认
                return new DefaultLogArgsSerializableHandler();
            }
        });
    }

    /**
     * 解析spEL表达式
     */
    private String getValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) {
        //获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEL);
            // spring的表达式上下文对象
            EvaluationContext context = new StandardEvaluationContext();
            // 给上下文赋值
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return expression.getValue(context) + "";
        }
        return DASH;
    }

    /**
     * 构建日志对象
     */
    private SysLogger getSysLogger(EdLog edLog, ProceedingJoinPoint joinPoint) {
        SysLogger parent = SysLoggerContext.getCurrent();
        SysLogger sysLogger = new SysLogger();
        sysLogger.setId(IdUtil.getSnowflakeNextId());
        if (Objects.isNull(parent.getId())) {
            sysLogger.setParentId(0L);
        } else {
            sysLogger.setParentId(parent.getId());
        }
        sysLogger.setCreateTime(LocalDateTime.now());
        sysLogger.setApplicationName(applicationName);

        String title = DASH;
        if (StringUtils.isEmpty(edLog.title())) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            ApiOperation annotation = methodSignature.getMethod().getAnnotation(ApiOperation.class);
            if (Objects.isNull(annotation)) {
                Operation operation = methodSignature.getMethod().getAnnotation(Operation.class);
                if (Objects.nonNull(operation) && StringUtils.isNotEmpty(edLog.title())) {
                    title = operation.description();
                }
            } else {
                if (StringUtils.isNotEmpty(edLog.title())) {
                    title = annotation.value();
                }
            }
        } else {
            title = edLog.title();
        }
        sysLogger.setName(title);
        String operation = edLog.type();
        sysLogger.setLogType(operation);

        return sysLogger;
    }

    /**
     * 封装request信息
     *
     * @param sysLogger           sysLogger
     * @param edLog               edLog
     * @param serializableHandler serializableHandler
     * @param joinPoint           joinPoint
     */
    private void packageRequestMessage(SysLogger sysLogger, EdLog edLog,
                                       BaseLogArgsSerializableHandler serializableHandler,
                                       ProceedingJoinPoint joinPoint) {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        sysLogger.setClassName(methodSignature.getDeclaringTypeName());
        sysLogger.setMethodName(methodSignature.getName());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String tenantId = request.getHeader("x-tenant-header");
            String traceId = request.getHeader("x-traceId-header");
            String method = getValue(request.getMethod());
            String browser = getBrowser(request);
            String os = getOs(request);
            String ipAddress = getIpAddress(request);
            if (baseLogUserService != null) {
                BaseLogUser currentUser = baseLogUserService.getCurrentUser();
                if (Objects.nonNull(currentUser)) {
                    sysLogger.setUserId(currentUser.getId());
                    sysLogger.setUserName(currentUser.getName());
                }
            }
            if (Objects.nonNull(tenantId)) {
                sysLogger.setTenantId(Long.valueOf(tenantId));
            }
            if (Objects.nonNull(traceId)) {
                sysLogger.setTraceId(Long.valueOf(traceId));
            }
            sysLogger.setRequestMethod(method);
            sysLogger.setRequestBrowser(browser);
            sysLogger.setRequestOs(os);
            sysLogger.setRequestIp(ipAddress);
        }

        Object[] args = joinPoint.getArgs();
        //参数
        if (edLog.needArgs()) {
            sysLogger.setRequestArgs(serializableHandler.serializeArgs(args));
        } else {
            sysLogger.setRequestArgs(DASH);
        }

        String key = edLog.key();
        if (key.contains("#")) {
            //获取方法参数值
            try {
                key = getValBySpEL(key, methodSignature, args);
            } catch (Exception e) {
                log.warn("EdLogAspect - getValBySpEL error:{}", e.getMessage(), e);
                key = DASH;
            }
            key = key.replace("\r\n", "\\r\\n")
                    .replace("\n", "\\n").
                    replace("\r", "\\r");
        }
        if (StringUtils.isEmpty(key)) {
            key = DASH;
        }
        sysLogger.setOperation(key);
    }

    /**
     * 获取客户端操作系统
     */
    private static String getOs(HttpServletRequest request) {
        UserAgent userAgent = getUserAgent(request);
        if (Objects.isNull(userAgent)) {
            return DASH;
        } else {
            String os = userAgent.getOs() + "";
            return UNKNOWN.equalsIgnoreCase(os) ? DASH : os;
        }
    }

    /**
     * 获取请求代理头
     */
    private static UserAgent getUserAgent(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return null;
        }
        String userAgentStr = ServletUtil.getHeaderIgnoreCase(request, "User-Agent");
        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
        //判空
        if (ObjectUtil.isNotEmpty(userAgentStr)) {
            //如果根本没获取到浏览器
            if (UNKNOWN.equalsIgnoreCase(userAgent.getBrowser().getName())) {
                //则将ua设置为浏览器
                userAgent.setBrowser(new Browser(userAgentStr, null, ""));
            }
        }
        return userAgent;
    }

    /**
     * 获取浏览器信息
     *
     * @param request request
     * @return 浏览器信息
     */
    private String getBrowser(HttpServletRequest request) {
        UserAgent userAgent = getUserAgent(request);
        if (Objects.isNull(userAgent)) {
            return DASH;
        } else {
            String browser = userAgent.getBrowser() + "";
            return UNKNOWN.equalsIgnoreCase(browser) ? DASH : browser;
        }
    }

    /**
     * 获取请求IP
     *
     * @return String IP
     */
    private String getIpAddress(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return "127.0.0.1";
        } else {
            String remoteHost = ServletUtil.getClientIP(request);
            return "0:0:0:0:0:0:0:1".equals(remoteHost) ? "127.0.0.1" : remoteHost;
        }
    }

    /**
     * 获取值
     *
     * @param value value
     * @return value
     */
    private String getValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return DASH;
        } else {
            return value;
        }
    }

}
