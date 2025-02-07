package com.tr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tr.dto.LoginDto;
import com.tr.entity.User;
import com.tr.service.LoginService;
import com.tr.service.UserService;
import com.tr.util.*;
import com.tr.vo.UserLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService userService;

    @Override
    public CommonResponse login(LoginDto loginDto){
        LambdaQueryWrapper<User> sysUserWrapper = Wrappers.<User>lambdaQuery()
                .eq(User::getAccount,loginDto.getAccount());
        User user = userService.getOne(sysUserWrapper);
        if(user == null){
            return CommonResponse.buildRespose4Fail(ServerCode.ERROR_CODE.getCode(),"账号未查到！");
        }
        if(!user.getPassword().equals(MD5Util.MD5Encode(loginDto.getPassword(),"UTF-8"))){
            return CommonResponse.buildRespose4Fail(ServerCode.ERROR_CODE.getCode(),"账号或密码错误！");
        }
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setImage(user.getImage());
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setAccount(user.getAccount());
        String token = JwtUtil.generateJwtToken(loginDto.getAccount(),0);
        userLoginVo.setToken(token);
        userLoginVo.setNickName(user.getNickName());
        RedisUtil.set(Constants.ACCESS_TOKEN+loginDto.getAccount(),token,86400);
        return CommonResponse.buildRespose4Success(userLoginVo,"登录成功！");
    }


    public static void main(String[] args) {
        String s = MD5Util.MD5Encode("123", "UTF-8");
        System.out.printf(s);
    }
}
