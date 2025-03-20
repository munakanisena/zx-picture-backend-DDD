package com.katomegumi.zxpicture.domain.space.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.domain.space.repository.SpaceRepository;
import com.katomegumi.zxpicture.domain.space.service.SpaceDomainService;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceLevelEnum;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceQueryRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author lirui
* @description 针对表【tb_space(空间)】的数据库操作Service实现
* @createDate 2025-03-04 18:25:23
*/
@Service
public class SpaceDomainServiceImpl implements SpaceDomainService {

//    @Resource
//    @Lazy
//    private DynamicShardingManager dynamicShardingManager;

    @Resource
    private SpaceRepository spaceRepository;

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getSpaceLevelEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        //拼接查询条件
        queryWrapper.eq(ObjectUtil.isNotEmpty(id),"id",id);
        queryWrapper.eq(ObjectUtil.isNotEmpty(userId),"userId",userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName),"spaceName",spaceName);
        queryWrapper.eq(ObjectUtil.isNotEmpty(spaceLevel),"spaceLevel",spaceLevel);
        queryWrapper.eq(ObjectUtil.isNotEmpty(spaceType),"spaceType",spaceType);

        //排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;

    }

    @Override
    public void checkSpaceAuth(Space space, User loginUser) {
        if (!space.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }
}




