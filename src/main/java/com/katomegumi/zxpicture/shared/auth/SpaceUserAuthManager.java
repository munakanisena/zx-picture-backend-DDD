package com.katomegumi.zxpicture.shared.auth;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.katomegumi.zxpicture.shared.auth.model.SpaceUserAuthConfig;
import com.katomegumi.zxpicture.shared.auth.model.SpaceUserRole;
import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.domain.space.entily.SpaceUser;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceRoleEnum;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceTypeEnum;
import com.katomegumi.zxpicture.application.service.SpaceUserApplicationService;
import com.katomegumi.zxpicture.application.service.UserApplicationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 空间成员管理权限
 */
@Component
public class SpaceUserAuthManager {
    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;
    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG=JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     * @param role
     * @return
     */
    public List<String> getSpaceUserPermissionsByRole(String role) {
        if (role == null) {
            return Collections.emptyList();
        }
        SpaceUserRole spaceUserRole = SPACE_USER_AUTH_CONFIG.getRoles()
                .stream()
                .filter(role1 -> role1.getKey().equals(role))
                .findFirst()
                .orElse(null); //没有就设置为null
        if (spaceUserRole == null) {
            return Collections.emptyList();
        }
        return spaceUserRole.getPermissions();
    }
    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getSpaceUserPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (loginUser.isAdmin()) {
                return ADMIN_PERMISSIONS;
            }
            return new ArrayList<>();
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        // 根据空间获取对应的权限
        switch (spaceTypeEnum) {
            case PRIVATE:
                // 私有空间，仅本人或管理员有所有权限
                if (space.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间，查询 SpaceUser 并获取角色和权限
                SpaceUser spaceUser = spaceUserApplicationService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getSpaceUserPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }

}
