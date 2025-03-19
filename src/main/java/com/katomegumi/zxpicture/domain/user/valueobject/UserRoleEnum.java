package com.katomegumi.zxpicture.domain.user.valueobject;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * Role枚举类  什么时候设置枚举类？  当变量是固定的几个值时 设置枚举类
 */
@Getter
public enum UserRoleEnum {

    USER("用户","user"),
    ADMIN("管理员","admin");

    private final String text;
    private final String value;



    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**根据 值获取枚举类
     *
     * @param value
     * @return 枚举值
     */
    public static UserRoleEnum getUserRoleEnum(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        //如果枚举值很多 可以先将枚举封装到map然后再进行 匹配操作

        for (UserRoleEnum userRoleEnum :values()) {
            if(userRoleEnum.getValue().equals(value)){
                return userRoleEnum;
            }
        }

       return null;
    }

}
