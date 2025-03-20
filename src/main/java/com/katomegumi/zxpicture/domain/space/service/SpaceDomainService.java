package com.katomegumi.zxpicture.domain.space.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceQueryRequest;

/**
* @author lirui
* @description 针对表【tb_space(空间)】的数据库操作Service
* @createDate 2025-03-04 18:25:23
*/
public interface SpaceDomainService  {


    /**
     * 自动填充 如果没有设置 就自行填充
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 封装 query对象 便于读取
     * @param SpaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest SpaceQueryRequest);


    /**
     * 校验空间权限
     * @param space
     * @param loginUser
     */
    void checkSpaceAuth(Space space,User loginUser);

}
