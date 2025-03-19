package com.katomegumi.zxpicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.katomegumi.zxpicture.infrastructure.common.DeleteRequest;
import com.katomegumi.zxpicture.interfaces.dto.user.UserLoginRequest;
import com.katomegumi.zxpicture.interfaces.dto.user.UserQueryRequest;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.interfaces.dto.user.UserRegisterRequest;
import com.katomegumi.zxpicture.interfaces.vo.user.LoginUserVO;
import com.katomegumi.zxpicture.interfaces.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
* @author lirui
* @description 针对表【tb_user(用户)】的数据库操作Service
* @createDate 2025-02-04 18:59:15
*/
public interface UserApplicationService  {
    /**
     * 用户注册
     @param userRegisterRequest
     * @return
     */
    Long register(UserRegisterRequest userRegisterRequest);


    /**
     * 密码加密
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    Long addUser(User user);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 转换User为UserVO
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    List<User> listByIds(Set<Long> userIdSet);

    /**
     * user转换为 userVO
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 批量转换 user转换为 userVO
     * @param users
     * @return
     */
    List<UserVO> getUserVOList(List<User> users);

    /**
     * 通过传来的 直接获取queryWrapper对象 实现查询
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    User getUserById(long id);

    UserVO getUserVOById(long id);

    boolean removeById(DeleteRequest deleteRequest);

    void updateById(User user);

    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);
}
