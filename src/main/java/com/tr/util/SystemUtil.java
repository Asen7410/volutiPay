package com.tr.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;


/**
 * 系统工具类
 */
public class SystemUtil {
    /**
     * JWT-userId:
     */
    public final static String USER_ID = "traccount";

    /**
     * JWT
     */
    /** 过期时间(秒为单位) 时间内不用重新登录3600*24  */
    public final static String JWT_TOKENEXPIRETIME = "86400";
    public final static String JWT_REFRESHTOKENEXPIRETIME = "850000000";
    /**
     * JWT_SUBJECT_NAME:
     */
    public static final  String JWT_SUBJECT_NAME = "tr.com";

    public static final String TOKEN_HEADER = "token";

    /**
     * JWT-currentTimeMillis:
     */
    public final static String CURRENT_TIME_MILLIS = "timeMillis";

    /**
     * 获取操作系统的类型
     */
    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * 判断当前操作系统是Linux
     */
    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    /**
     * 判断当前操作系统是Windows
     */
    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    /**
     * 调JDK实现的UUID，生成字符串替换掉"-"
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        return s.replace("-", "");
    }
    /**
     * 功能描述: 获取request 对象
     */
    public static HttpServletRequest getRequest(){
        //获取request
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        return request;
    }

    /**
     * 功能描述: 获取response 对象
     */
    public static HttpServletResponse getResponse(){
        //获取request
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = sra.getResponse();
        return response;
    }
    /**
     * Description: Base64加密
     * @param
     * @return
     */
    public static String base64Encode(String str) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes("utf-8"));
        return new String(encodeBytes);
    }
    public static String base64Encode(byte[] utfBytes) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(utfBytes);
        return new String(encodeBytes);
    }
    /**
     * Base64解密
     */
    public static String base64Decode(String str) throws UnsupportedEncodingException {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes("utf-8"));
        return new String(decodeBytes);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(3600*24);
    }

}
