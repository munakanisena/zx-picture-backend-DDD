package com.katomegumi.zxpicture.domain.space.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.katomegumi.zxpicture.domain.space.entily.SpaceUser;
import com.katomegumi.zxpicture.domain.space.repository.SpaceUserRepository;
import com.katomegumi.zxpicture.domain.space.service.SpaceUserDomainService;
import com.katomegumi.zxpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author lirui
* @description 针对表【tb_space_user(团队用户关联)】的数据库操作Service实现
* @createDate 2025-03-12 17:58:55
*/
@Service
public class SpaceUserDomainServiceImpl implements SpaceUserDomainService {

    @Resource
    private SpaceUserRepository spaceUserRepository;

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "spaceRole", spaceRole);
        return queryWrapper;
    }

}




