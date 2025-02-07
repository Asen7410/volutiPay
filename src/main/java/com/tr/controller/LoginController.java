package com.tr.controller;

import com.tr.dto.LoginDto;
import com.tr.service.LoginService;
import com.tr.util.CommonResponse;
import com.tr.util.RedisUtil;
import com.tr.vo.UserLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "登录相关接口")
@RestController
@RequestMapping("/oauth")
public class LoginController {

    @Autowired
    private LoginService adminLoginService;

    @ApiOperation(value = "登录",notes = "登录",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/login")
    public CommonResponse<UserLoginVo> appLogin(@Validated @Valid @RequestBody LoginDto loginDto){
        CommonResponse response = adminLoginService.login(loginDto);
        return response;
    }

    @GetMapping("/test")
    public CommonResponse test(@RequestParam Object data){
        RedisUtil.set("xml",data);
        return CommonResponse.success(data);
    }

}
