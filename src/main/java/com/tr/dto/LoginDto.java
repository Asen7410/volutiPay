package com.tr.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;


@Data
public class LoginDto implements Serializable {

    @ApiModelProperty(value = "账号",required = true)
    @NotEmpty
    private String account;

    @ApiModelProperty(value = "密码",required = true)
    @NotEmpty
    private String password;

    /** 客户端信息android ios pc... */
    @ApiModelProperty(value = "android,ios,pc...")
    private String userAgent;

    /** 登录时的address */
    @ApiModelProperty(value = "address")
    private String address;

}
