package com.katomegumi.zxpicture.application.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceAddRequest;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceQueryRequest;
import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.interfaces.vo.space.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author lirui
* @description 针对表【tb_space(空间)】的数据库操作Service
* @createDate 2025-03-04 18:25:23
*/
public interface SpaceApplicationService extends IService<Space> {


    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);


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
     * 获取单个空间封装
     * @param Space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space Space, HttpServletRequest request);

    /**
     * 获取多个空间 封装
     * @param SpacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> SpacePage, HttpServletRequest request);

    /**
     * 校验空间权限
     * @param space
     * @param loginUser
     */
    void checkSpaceAuth(Space space,User loginUser);

}
