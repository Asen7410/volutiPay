package com.tr.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tr.entity.User;
import com.tr.exception.ServerException;
import com.tr.service.UserService;
import com.tr.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Asen
 */
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String token = httpServletRequest.getHeader(SystemUtil.TOKEN_HEADER);//从http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if(!(object instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)object;
        Method method=handlerMethod.getMethod();
        // 执行认证
        if (StringUtils.isEmpty(token)) {
            throw new ServerException(ServerCode.UNAUTHORIZED.getCode(),ServerCode.UNAUTHORIZED.getMessage());
        }
        // 获取 token 中的 user id
        String account = JwtUtil.getUserNo();
        if(StringUtils.isEmpty(account)){
            throw new ServerException(ServerCode.UNAUTHORIZED.getCode(),ServerCode.UNAUTHORIZED.getMessage());
        }
        //查询token
        String sysAccessToken = RedisUtil.get(Constants.ACCESS_TOKEN+account);
        if(sysAccessToken == null){
            throw new ServerException(ServerCode.UNAUTHORIZED.getCode(),ServerCode.UNAUTHORIZED.getMessage());
        }
        LambdaQueryWrapper<User> appUserInfoWrapper = Wrappers.<User>lambdaQuery().eq(User::getAccount,account);
        User user = userService.getOne(appUserInfoWrapper);
        if (user == null) {
            throw new ServerException(ServerCode.UNAUTHORIZED.getCode(),ServerCode.UNAUTHORIZED.getMessage());
        }
        // 验证 token
        if(!JwtUtil.verify(token)){
            throw new ServerException(ServerCode.UNAUTHORIZED.getCode(),ServerCode.UNAUTHORIZED.getMessage());
        }
        //token失效
        boolean tokenFlag = JwtUtil.isTokenExpired(token);
        if(tokenFlag){
            throw new ServerException(ServerCode.TOKEN_EXPIRED.getCode(),ServerCode.TOKEN_EXPIRED.getMessage());
        }
        user.setPassword(null);
        DataContextHolder.getUserIdLocal().set(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }

}
