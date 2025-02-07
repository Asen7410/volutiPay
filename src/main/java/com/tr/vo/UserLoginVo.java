package com.tr.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Asen
 * @since 2025-01-02
 */
@Data
public class UserLoginVo implements Serializable {

    /** 用户编号 */
    private String account;

    /** 头像 */
    private String image;

    /** 昵称 */
    private String nickName;

    /**  */
    private String token;

}
