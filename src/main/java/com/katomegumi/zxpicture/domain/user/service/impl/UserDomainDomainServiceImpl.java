package com.katomegumi.zxpicture.domain.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.domain.user.repository.UserRepository;
import com.katomegumi.zxpicture.domain.user.service.UserDomainService;
import com.katomegumi.zxpicture.domain.user.valueobject.UserRoleEnum;
import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;
import com.katomegumi.zxpicture.interfaces.dto.user.UserQueryRequest;
import com.katomegumi.zxpicture.interfaces.vo.user.LoginUserVO;
import com.katomegumi.zxpicture.interfaces.vo.user.UserVO;
import com.katomegumi.zxpicture.shared.auth.StpKit.StpKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.katomegumi.zxpicture.domain.user.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author lirui
 * @description 针对表【tb_user(用户)】的数据库操作Service实现
 * @createDate 2025-02-04 18:59:15
 */
@Service
@Slf4j
public class UserDomainDomainServiceImpl implements UserDomainService {

    //调用领域层的仓储接口 操作数据库
    @Resource
    private UserRepository userRepository;

    @Override
    public Long register(String userAccount, String userPassword, String confirmPassword) {
        QueryWrapper<User> query = new QueryWrapper<User>().eq("userAccount", userAccount);
        long count = userRepository.count(query);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号被占用了");
        }
        String password = getEncryptPassword(userPassword);

        User user= new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(password);
        user.setUserName("无名");

        boolean result = userRepository.save(user);

        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        //这里有主键回显
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        String encryptPassword = getEncryptPassword(userPassword);
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>()
                .eq("userAccount", userAccount)
                .eq("userPassword", encryptPassword);

        User user = userRepository.getBaseMapper().selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        //采用sa-TOKEN 这里是便于空间用户权限使用
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        final String SALT = "zx";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object objUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) objUser;
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("id", user.getId());
        user = userRepository.getBaseMapper().selectOne(queryWrapper);
        return user;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        return BeanUtil.copyProperties(user, LoginUserVO.class);

    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (userList.isEmpty()) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.getUserRole().equals(UserRoleEnum.ADMIN.getValue());
    }


    @Override
    public Long addUser(User user) {
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = this.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userRepository.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return user.getId();
    }

    @Override
    public Boolean removeById(Long id) {
        return userRepository.removeById(id);
    }

    @Override
    public boolean updateById(User user) {
        return userRepository.updateById(user);
    }

    @Override
    public User getById(long id) {
        return userRepository.getById(id);
    }

    @Override
    public Page<User> page(Page<User> userPage, QueryWrapper<User> queryWrapper) {
        return userRepository.page(userPage, queryWrapper);
    }

    @Override
    public List<User> listByIds(Set<Long> userIdSet) {
        return userRepository.listByIds(userIdSet);
    }

    @Override
    public boolean save(User user) {
        return userRepository.save(user);
    }
}



