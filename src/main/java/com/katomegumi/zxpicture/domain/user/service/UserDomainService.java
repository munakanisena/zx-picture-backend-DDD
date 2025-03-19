package com.katomegumi.zxpicture.domain.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.interfaces.dto.user.UserQueryRequest;
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
public interface UserDomainService  {
    /**
     * 用户注册
     * @param username
     * @param password
     * @param confirmPassword
     * @return
     */
    Long register(String username, String password, String confirmPassword);


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 密码加密
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

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

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    Long addUser(User user);

    Boolean removeById(Long id);

    boolean updateById(User user);

    User getById(long id);

    Page<User> page(Page<User> userPage, QueryWrapper<User> queryWrapper);

    List<User> listByIds(Set<Long> userIdSet);

    boolean save(User user);
}
