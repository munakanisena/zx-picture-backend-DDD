package com.katomegumi.zxpicture.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.katomegumi.zxpicture.domain.user.service.UserDomainService;
import com.katomegumi.zxpicture.infrastructure.common.DeleteRequest;
import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;
import com.katomegumi.zxpicture.interfaces.dto.user.UserLoginRequest;
import com.katomegumi.zxpicture.interfaces.dto.user.UserRegisterRequest;
import com.katomegumi.zxpicture.interfaces.dto.user.UserQueryRequest;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.domain.user.valueobject.UserRoleEnum;
import com.katomegumi.zxpicture.interfaces.vo.user.LoginUserVO;
import com.katomegumi.zxpicture.interfaces.vo.user.UserVO;
import com.katomegumi.zxpicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

/**
 * @author lirui
 * @description 针对表【tb_user(用户)】的数据库操作Service实现
 * @createDate 2025-02-04 18:59:15
 */
@Service
@Slf4j
public class UserApplicationServiceImpl implements UserApplicationService {

    //在应用服务层调用领域服务 应用层就是对领域服务层的编排
    @Resource
    private UserDomainService userDomainService;

    @Override
    public Long register(UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.PARAMS_ERROR);

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        //校验注册
        User.validUserRegister(userAccount, userPassword, confirmPassword);
        //注册
        return userDomainService.register(userAccount, userPassword, confirmPassword);
    }

    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        //校验
        User.validUserLogin(userAccount, userPassword);
        //用户
        return userDomainService.userLogin(userAccount, userPassword, request);
    }

    @Override
    public Long addUser(User user) {
        return userDomainService.addUser(user);
    }
    @Override
    public User getLoginUser(HttpServletRequest request) {
        return userDomainService.getLoginUser(request);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user){
        return userDomainService.getLoginUserVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        return userDomainService.userLogout(request);
    }

    @Override
    public List<User> listByIds(Set<Long> userIdSet) {
        return userDomainService.listByIds(userIdSet);
    }
    @Override
    public UserVO getUserVO(User user){
        return userDomainService.getUserVO(user);
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList){
        return userDomainService.getUserVOList(userList);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        return userDomainService.getQueryWrapper(userQueryRequest);
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        return userDomainService.getEncryptPassword(userPassword);
    }


    @Override
    public User getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userDomainService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return user;
    }

    @Override
    public UserVO getUserVOById(long id) {
        return userDomainService.getUserVO(getUserById(id));
    }

    @Override
    public boolean removeById(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        return userDomainService.removeById(id);
    }

    @Override
    public void updateById(User user) {
        boolean result = userDomainService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userDomainService.page(new Page<>(current, pageSize),
                userDomainService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userDomainService.getUserVOList(userPage.getRecords());
        return userVOPage.setRecords(userVOList);
    }
}




