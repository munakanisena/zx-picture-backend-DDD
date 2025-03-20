package com.katomegumi.zxpicture.shared.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 全部 权限和角色
 */
@Data
public class SpaceUserAuthConfig implements Serializable {

    /**
     * 权限列表
     */
    private List<SpaceUserPermission> permissions;

    /**
     * 角色列表
     */
    private List<SpaceUserRole> roles;

    private static final long serialVersionUID = 1L;
}
