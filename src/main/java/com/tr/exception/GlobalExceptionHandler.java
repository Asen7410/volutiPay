package com.tr.exception;

import com.alibaba.fastjson.JSON;
import com.tr.util.CommonResponse;
import com.tr.util.ServerCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 全局异常类处理
 */
@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    /**
     * 嵌套异常处理
     *
     * @param e
     * @return ErrorInfo
     */
    @ExceptionHandler({UndeclaredThrowableException.class})
    @ResponseBody
    public Object businessExceptionHandler(HttpServletRequest request, UndeclaredThrowableException e) {
        Throwable cause = e.getCause().getCause();
        if (cause instanceof ServerException) {
            ServerException pe = (ServerException) cause;
            log.info("调用新实现接口异常.", e);
            return CommonResponse.buildRespose4Fail(pe.getCode(), pe.getMessage());
        }
        log.info("调用新实现接口异常.", e);
        return CommonResponse.buildRespose4Fail(ServerCode.ERROR_CODE.getCode(),ServerCode.ERROR_CODE.getMessage());
    }

    /**
     * 业务异常处理
     *
     * @param e
     * @return ErrorInfo
     */
    @ExceptionHandler({ServerException.class})
    @ResponseBody
    public Object businessExceptionHandler(HttpServletRequest request, ServerException e) {
        log.info("自定义异常:{}", request.getRequestURI(), e);
        return CommonResponse.buildRespose4Fail(e.getCode(), e.getMessage());
    }


    /**
     * 方法参数校验异常 Validate
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Object handleValidationException(HttpServletRequest request, ConstraintViolationException ex) {
        log.info("校验异常:{}", request.getRequestURI(), ex);
        String collect = ex.getConstraintViolations().stream().filter(Objects::nonNull)
                .map(cv -> cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));
        log.info("请求参数异常 {}", collect);
        return CommonResponse.buildRespose4Fail(ServerCode.PARAMS_ERROR_CODE.getCode(), collect);
    }

    /**
     * Bean 校验异常 Validate
     *
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class) //400
    @ResponseBody
    public Object methodArgumentValidationHandler(HttpServletRequest request, MethodArgumentNotValidException exception) {

        BindingResult bindingResult = exception.getBindingResult();
        StringBuilder sb = new StringBuilder("校验失败:");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage()).append(", ");
        }
        String error = sb.toString();
        log.info("接口路径:{},请求参数:{},报错信息:{}", request.getRequestURI(), showParams(request), JSON.toJSONString(bindingResult.getFieldErrors()));
        return CommonResponse.buildRespose4Fail(ServerCode.PARAMS_ERROR_CODE.getCode(), error);
    }

    /**
     * 绑定异常
     *
     * @param request
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Object bindException(HttpServletRequest request, BindException exception) {
        log.info("绑定异常:{}", request.getRequestURI(), exception);
        BindingResult bindingResult = exception.getBindingResult();
        StringBuilder sb = new StringBuilder("校验失败:");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(", ");
        }

        return CommonResponse.buildRespose4Fail(ServerCode.PARAMS_ERROR_CODE.getCode(), sb.toString());
    }


    /**
     * 访问接口参数不全
     *
     * @param request
     * @param pe
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Object missingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException pe) {
        log.info("异常:{}", request.getRequestURI(), pe);
//        RestResultWrapper<String> restResultWrapper = new RestResultWrapper();
//        restResultWrapper.setCode(HttpStatus.BAD_REQUEST.value());
//        restResultWrapper.setMessage("该请求路径："+request.getRequestURI()+"下的请求参数不全："+pe.getMessage());
        return CommonResponse.buildRespose4Fail(ServerCode.PARAMS_ERROR_CODE.getCode(), "该请求路径：" + request.getRequestURI() + "下的请求参数不全：" + pe.getMessage());
    }

    /**
     * HttpRequestMethodNotSupportedException
     *
     * @param request
     * @param pe
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Object httpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException pe) {
        log.info("异常:{}", request.getRequestURI(), pe);
//        RestResultWrapper<String> restResultWrapper = new RestResultWrapper();
//        restResultWrapper.setCode(HttpStatus.BAD_REQUEST.value());
//        restResultWrapper.setMessage("请求方式不正确");
        return CommonResponse.buildRespose4Fail(HttpStatus.METHOD_NOT_ALLOWED.value(), "请求方式不正确");
    }

    /**
     * 异常处理
     *
     * @param e
     * @return ErrorInfo
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        log.info(ExceptionUtil.getMessage(e));
        return CommonResponse.buildRespose4Fail(500,e.getMessage());
    }


    /**
     * 异常详情
     *
     * @param e
     * @return
     */
    private String getExceptionDetail(Exception e) {
        StringBuilder stringBuffer = new StringBuilder(e.toString() + "\n");
        StackTraceElement[] messages = e.getStackTrace();
        Arrays.stream(messages).filter(Objects::nonNull).forEach(stackTraceElement -> {
            stringBuffer.append(stackTraceElement.toString() + "\n");
        });
        return stringBuffer.toString();
    }

    /**
     * 请求参数
     *
     * @param request
     * @return
     */
    public String showParams(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder stringBuilder = new StringBuilder();
        Enumeration paramNames = request.getParameterNames();
        if (Objects.nonNull(paramNames)) {
            stringBuilder.append("----------------参数开始-------------------");
            stringBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length > 0) {
                    String paramValue = paramValues[0];
                    if (paramValue.length() != 0) {
                        stringBuilder.append("参数名:").append(paramName).append("参数值:").append(paramValue);
                    }
                }
            }
            stringBuilder.append("----------------参数结束-------------------");
        }
        return stringBuilder.toString();
    }

}