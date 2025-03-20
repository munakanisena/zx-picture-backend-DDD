package com.katomegumi.zxpicture.application.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.katomegumi.zxpicture.application.service.SpaceApplicationService;
import com.katomegumi.zxpicture.application.service.SpaceUserApplicationService;
import com.katomegumi.zxpicture.domain.space.service.SpaceDomainService;
import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceAddRequest;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceQueryRequest;
import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.domain.space.entily.SpaceUser;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceLevelEnum;

import com.katomegumi.zxpicture.infrastructure.mapper.SpaceMapper;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceRoleEnum;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceTypeEnum;
import com.katomegumi.zxpicture.interfaces.vo.space.SpaceVO;
import com.katomegumi.zxpicture.interfaces.vo.user.UserVO;
import com.katomegumi.zxpicture.application.service.UserApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author lirui
* @description 针对表【tb_space(空间)】的数据库操作Service实现
* @createDate 2025-03-04 18:25:23
*/
@Service
public class SpaceApplicationServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceApplicationService {

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private TransactionTemplate transactionTemplate; //自定义事务

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    @Resource
    private SpaceDomainService spaceDomainService;

//    @Resource
//    @Lazy
//    private DynamicShardingManager dynamicShardingManager;

    /**
     * 添加 用户添加空间
     *
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        //1.转换space
        ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);
        Space space = BeanUtil.copyProperties(spaceAddRequest, Space.class);
        if (StrUtil.isBlank(space.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (space.getSpaceLevel() == null) {
            //设置
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        //设置默认值
        if (space.getSpaceType() == null) {
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        //1.填充参数
        fillSpaceBySpaceLevel(space);
        //2.校验参数
        space.validSpace( true);
        Long userId = loginUser.getId();
        //补充参数
        space.setUserId(userId);
        //3.检验权限 判断是否为管理员
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        //针对用户加锁 避免多线程情况下 用户创建多个空间
        //TODO 后续考虑使用redis 的redisson完成
        String lock = String.valueOf(userId).intern();


        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery()
                        .eq(Space::getUserId, userId)
                        .eq(Space::getSpaceType, space.getSpaceType())
                        .exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户只能有一个私有空间或者团队空间");
                boolean result = save(space);
                ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "保存到数据库失败，请重试");
                //在创建空间的时候 如果是创建的团队空间 自动创建空间用户表(设置为管理员)
                if (spaceAddRequest.getSpaceType() == SpaceTypeEnum.TEAM.getValue()) {
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setUserId(userId);
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    boolean save = spaceUserApplicationService.save(spaceUser);
                    ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "创建团队成员记录失败");
                }
                //添加分表
                //dynamicShardingManager.createTable(space); 关闭分表
                //4.添加数据库
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }


    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        spaceDomainService.fillSpaceBySpaceLevel(space);
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        return spaceDomainService.getQueryWrapper(spaceQueryRequest);
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> records = spacePage.getRecords();
        //设定
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(records)) {
            return spaceVOPage;
        }

        List<SpaceVO> spaceVOList = records.stream().map(SpaceVO::objToVo).collect(Collectors.toList());
        //为了获得用户的详细信息
        Set<Long> userIds = records.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIds).stream().collect(Collectors.groupingBy(User::getId));

        for (SpaceVO spaceVO : spaceVOList) {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userApplicationService.getUserVO(user));
        }
        return spaceVOPage.setRecords(spaceVOList);
    }

    @Override
    public void checkSpaceAuth(Space space, User loginUser) {
        spaceDomainService.checkSpaceAuth(space, loginUser);
    }

}


