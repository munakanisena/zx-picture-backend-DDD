package com.katomegumi.zxpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.katomegumi.zxpicture.domain.space.entily.SpaceUser;
import com.katomegumi.zxpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;

/**
* @author lirui
* @description 针对表【tb_space_user(团队用户关联)】的数据库操作Service
* @createDate 2025-03-12 17:58:55
*/
public interface SpaceUserDomainService  {



    /**
     * 构造查询对象
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

}
