package com.katomegumi.zxpicture.interfaces.controller.UserController;



import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.katomegumi.zxpicture.infrastructure.annotation.AuthCheck;
import com.katomegumi.zxpicture.infrastructure.common.BaseResponse;
import com.katomegumi.zxpicture.infrastructure.common.DeleteRequest;
import com.katomegumi.zxpicture.infrastructure.common.ResultUtils;

import com.katomegumi.zxpicture.interfaces.assembler.UserAssembler;
import com.katomegumi.zxpicture.interfaces.dto.user.*;
import com.katomegumi.zxpicture.domain.user.constant.UserConstant;
import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;

import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;

import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.interfaces.vo.user.LoginUserVO;
import com.katomegumi.zxpicture.interfaces.vo.user.UserVO;
import com.katomegumi.zxpicture.application.service.UserApplicationService;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/user")
public class UserController {

        @Resource
        private UserApplicationService userApplicationService;

        /**
         * 用户注册
         * @param userRegisterRequest
         * @return
         */
        @PostMapping("/register")
        public BaseResponse<Long> UserRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
                Long id= userApplicationService.register(userRegisterRequest);
                return ResultUtils.success(id);

        }

        /**
         * 用户登录
         * @param userLoginRequest
         * @param request
         * @return
         */
        @PostMapping("/login")
        public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
                LoginUserVO loginUserVO = userApplicationService.userLogin(userLoginRequest, request);
                return ResultUtils.success(loginUserVO);
        }

        /**
         * 获取登录用户
         * @param request
         * @return
         */
        @GetMapping("/get/login")
        public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
                User loginUser = userApplicationService.getLoginUser(request);
                return ResultUtils.success(userApplicationService.getLoginUserVO(loginUser));
        }

        /**
         * 用户注销(退出)
         * @param request
         * @return
         */
        @PostMapping("/logout")
        public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
                boolean result = userApplicationService.userLogout(request);
                return ResultUtils.success(result);
        }
        /**
         * 创建用户
         */
        @PostMapping("/add")
        @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
        public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
                ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
                User user = UserAssembler.toUserEntity(userAddRequest);
                return ResultUtils.success(userApplicationService.addUser(user));
        }

        /**
         * 根据 id 获取用户（仅管理员）
         */
        @GetMapping("/get")
        @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
        public BaseResponse<User> getUserById(long id) {
                User user = userApplicationService.getUserById(id);
                return ResultUtils.success(user);
        }

        /**
         * 根据 id 获取包装类
         */
        @GetMapping("/get/vo")
        public BaseResponse<UserVO> getUserVOById(long id) {
                return ResultUtils.success(userApplicationService.getUserVOById(id));
        }

        /**
         * 删除用户
         */
        @PostMapping("/delete")
        @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
        public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
                boolean b = userApplicationService.removeById(deleteRequest);
                return ResultUtils.success(b);
        }

        /**
         * 更新用户
         */
        @PostMapping("/update")
        @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
        public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
                ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
                User user = UserAssembler.toUserEntity(userUpdateRequest);
                userApplicationService.updateById(user);
                return ResultUtils.success(true);
        }

        /**
         * 分页获取用户封装列表（仅管理员）
         *
         * @param userQueryRequest 查询请求参数
         */
        @PostMapping("/list/page/vo")
        @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
        public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
                Page<UserVO> userVOPage = userApplicationService.listUserVOByPage(userQueryRequest);
                return ResultUtils.success(userVOPage);
        }


}
