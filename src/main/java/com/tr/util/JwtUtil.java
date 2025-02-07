package com.tr.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tr.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Asen
 */
@Slf4j
@Component
@DependsOn(value = {"environment"})
public class JwtUtil {

    private static AntPathMatcher matcher = new AntPathMatcher();
    /**
     * RefreshToken过期时间
     */
    private static Integer refreshTokenExpireTime;

    /**
      *  是否路径匹配
     */
    public static boolean isPathMatch(String pattern, String path) {
        return matcher.match(pattern, path);
    }
    /**
     * Description:  校验token是否正确
     */
    public static boolean verify(String token)  {
        if (StringUtils.isEmpty(token)  ){
            return false;
        }
        try {
            // 帐号加JWT私钥解密
            String currentTimeMillis = getClaim(token, SystemUtil.CURRENT_TIME_MILLIS);
            String secret = getClaim(token, SystemUtil.USER_ID) +
                    SystemUtil.JWT_SUBJECT_NAME + currentTimeMillis;
            Algorithm algorithm = Algorithm.HMAC512(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        }catch (TokenExpiredException e){
            throw new RuntimeException("401");
        } catch (JWTVerificationException e) {
            log.error("JWTToken认证解密出现异常:" ,e);
            throw new RuntimeException("403");
        }

    }

    /**
     * token是否过期
     */
    public static boolean isTokenExpired(String token) {
        Date now = Calendar.getInstance().getTime();
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(now);
    }
    /**
     *  获得Token中的信息无需secret解密也能获得
     */
    private static String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim(claim).as(String.class);
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**  =============================  以上是网关中校验使用 ======================== */

    /**
     *  获取当前用户的ID
     */
    public static String getUserNo() {
        String v = getClaim(getToken(), SystemUtil.USER_ID);
        if (!StringUtils.isEmpty(v)){
            return v;
        }
        return null;
    }

    /**
     *  获取当前用户的ID
     */
    public static String getUserNo(String token) {
        String v = getClaim(token, SystemUtil.USER_ID);
        if (!StringUtils.isEmpty(v)){
            return v;
        }
        return null;
    }
    /**
     * 获取当前用户 todo
     */
    public static String getSession() {
        String userNo = getUserNo();
        return userNo;
    }
    /**
     * Description: 生成JwtToken type 0:token 1:refreshToken
     * @param
     * @return
     */
    public static String generateJwtToken(String account,int type){
        // 获取当前最新时间戳
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        // 读取配置文件，获取refreshTokenExpireTime属性
        String token = sign(account,currentTimeMillis,type);
        return token;
    }
    /**
      * 生成签名
     */
    private static String sign(String userNo, String currentTimeMillis,int type) {
        if (StringUtils.isEmpty(userNo) || StringUtils.isEmpty(currentTimeMillis)){
            return null;
        }
        // 帐号加JWT私钥加密
        String secret = userNo  + SystemUtil.JWT_SUBJECT_NAME + currentTimeMillis;
        // 此处过期时间是以毫秒为单位，所以乘以1000
        Date date = null;
        if(type == 0){
            date = new Date(System.currentTimeMillis() + Long.valueOf(SystemUtil.JWT_TOKENEXPIRETIME) * 1000);
        }else if(type == 1){
            date = new Date(System.currentTimeMillis() + Long.valueOf(SystemUtil.JWT_REFRESHTOKENEXPIRETIME) * 1000);
        }
        Algorithm algorithm = Algorithm.HMAC512(secret);

        // 附带ACCOUNT信息
       return JWT.create()
                .withClaim(SystemUtil.USER_ID, userNo)
                .withClaim(SystemUtil.CURRENT_TIME_MILLIS, currentTimeMillis)
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .withJWTId(userNo)
                .withSubject(SystemUtil.JWT_SUBJECT_NAME)
                .sign(algorithm);
    }

    /**
     * 功能描述: 判断Token是否存在
     */
    public static boolean isToken(){
        return getToken().isEmpty() ? false : true;
    }
    /**
      * 获取token
     */
    public static String getToken(){
        //获取request
        HttpServletRequest request = SystemUtil.getRequest();
        //拿到header 中的token
        String header = request.getHeader(SystemUtil.TOKEN_HEADER);
        if(null == header){
            throw new ServerException(ServerCode.UNAUTHORIZED);
        }
        return header;
    }

    /**
      * 注销token
     */
    public static void invalidate() {
        String userNo = getUserNo();
        //todo
    }

}
