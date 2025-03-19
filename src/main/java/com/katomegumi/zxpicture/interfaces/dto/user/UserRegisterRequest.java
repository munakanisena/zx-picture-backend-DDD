package com.katomegumi.zxpicture.interfaces.dto.user;


import lombok.Data;

import java.io.Serializable;

/**
 * 注册用户 dto
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1195110832618584472L;

    /**
     * 用户名
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String confirmPassword;

}
