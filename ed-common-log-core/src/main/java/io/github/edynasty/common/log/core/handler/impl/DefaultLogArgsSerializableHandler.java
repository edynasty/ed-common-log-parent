package io.github.edynasty.common.log.core.handler.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.edynasty.common.log.core.handler.BaseLogArgsSerializableHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 默认的日志参数序列化处理器
 *
 * @author tangxingpeng
 * @since 2025/3/22 22:49
 */
@Slf4j
public class DefaultLogArgsSerializableHandler implements BaseLogArgsSerializableHandler {

    protected final static String DASH = "-";

    protected final static Set<Class<?>> IGNORE_CLASSES = new HashSet<>(Arrays.asList(
            ServletRequest.class, ServletResponse.class, MultipartFile.class,
            HttpServletRequest.class, HttpServletResponse.class, HttpSession.class,
            WebRequest.class, NativeWebRequest.class, BindingResult.class,
            InputStream.class, OutputStream.class, Reader.class, Writer.class,
            Thread.class, Future.class, CompletableFuture.class,
            Proxy.class, Class.class, Method.class, Constructor.class
    ));

    protected final static JsonMapper MAPPER;

    static {
        MAPPER = JsonMapper.builder()
                .enable(MapperFeature.USE_STD_BEAN_NAMING)
                // 忽略在json字符串中存在，但是在java对象中不存在对应属性的情况
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 忽略空Bean转json的错误
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                // 允许不带引号的字段名称
                .configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true)
                // 允许单引号
                .configure(JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(), true)
                // allow int startWith 0
                .configure(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature(), true)
                // 允许字符串存在转义字符：\r \n \t
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
                // 排除空值字段
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                // 使用驼峰式
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                // 使用bean名称
                .enable(MapperFeature.USE_STD_BEAN_NAMING)
                // 所有日期格式都统一为固定格式
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                // 设置时区
                .defaultTimeZone(TimeZone.getTimeZone("GMT+8")).build();
    }

    protected static boolean shouldIgnore(Object obj) {
        return obj == null || IGNORE_CLASSES.stream().anyMatch(cls -> cls.isAssignableFrom(obj.getClass()));
    }

    @Override
    public String serializeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return DASH;
        }

        List<Object> params = Arrays.stream(args)
                .filter(arg -> !shouldIgnore(arg))
                .collect(Collectors.toList());

        try {
            return toJsonString(params);
        } catch (Exception e) {
            log.warn("序列化参数失败:{}", e.getMessage());
            return "serialize args error";
        }
    }

    @Override
    public String serializeResult(Object result) {
        if (result == null) {
            return DASH;
        }

        if (shouldIgnore(result)) {
            return DASH;
        }

        try {
            return toJsonString(result);
        } catch (Exception e) {
            log.warn("序列化结果失败:{}", e.getMessage());
            return "serialize result error";
        }
    }

    @Override
    public String serializeException(Throwable throwable) {
        return ExceptionUtil.stacktraceToString(throwable, -1)
                .replace("\r\n", "\\r\\n")
                .replace("\n", "\\n").
                replace("\r", "\\r");
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param object 待转换对象
     * @return JsonString
     */
    protected static String toJsonString(Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }
}
