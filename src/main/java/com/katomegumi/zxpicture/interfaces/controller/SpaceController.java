package com.katomegumi.zxpicture.interfaces.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import com.katomegumi.zxpicture.infrastructure.annotation.AuthCheck;
import com.katomegumi.zxpicture.infrastructure.common.BaseResponse;
import com.katomegumi.zxpicture.infrastructure.common.DeleteRequest;
import com.katomegumi.zxpicture.infrastructure.common.ResultUtils;
import com.katomegumi.zxpicture.domain.user.constant.UserConstant;
import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;
import com.katomegumi.zxpicture.interfaces.assembler.SpaceAssembler;
import com.katomegumi.zxpicture.interfaces.dto.space.*;
import com.katomegumi.zxpicture.shared.auth.SpaceUserAuthManager;

import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceLevelEnum;
import com.katomegumi.zxpicture.interfaces.vo.space.SpaceVO;
import com.katomegumi.zxpicture.application.service.SpaceApplicationService;
import com.katomegumi.zxpicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Slf4j
@RequestMapping("/space")
public class SpaceController {
    @Resource
    private SpaceApplicationService spaceApplicationService;

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceUserAuthManager   spaceUserAuthManager;

    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceAddRequest==null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        long userId = spaceApplicationService.addSpace(spaceAddRequest, loginUser);
        return ResultUtils.success(userId);
    }
    /**
     * 更新空间
     * @param spaceUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Space space = SpaceAssembler.toSpaceEntity(spaceUpdateRequest);
        // 自动填充数据
        spaceApplicationService.fillSpaceBySpaceLevel(space);
        // 数据校验
        space.validSpace( false);
        // 判断是否存在
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceApplicationService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 删除空间
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userApplicationService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        spaceApplicationService.checkSpaceAuth(oldSpace, loginUser);
        // 操作数据库
        boolean result = spaceApplicationService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        //TODO 删除空间的时候 将所有关联的图片删除(对象存储和数据库)
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取空间信息（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space Space = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(Space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(Space);
    }

    /**
     * 根据 id 获取空间（封装类）
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        SpaceVO spaceVO = spaceApplicationService.getSpaceVO(space, request);
        User loginUser = userApplicationService.getLoginUser(request);
        List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
        spaceVO.setPermissionList(permissionList);
        // 获取封装类
        return ResultUtils.success(spaceVO);
    }

    /**
     * 分页获取空间列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest SpaceQueryRequest) {
        long current = SpaceQueryRequest.getCurrent();
        long size = SpaceQueryRequest.getPageSize();
        // 查询数据库
        Page<Space> SpacePage = spaceApplicationService.page(new Page<>(current, size),
                spaceApplicationService.getQueryWrapper(SpaceQueryRequest));
        return ResultUtils.success(SpacePage);
    }

    /**
     * 分页获取空间列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest SpaceQueryRequest,
                                                             HttpServletRequest request) {
        long current = SpaceQueryRequest.getCurrent();
        long size = SpaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Space> SpacePage = spaceApplicationService.page(new Page<>(current, size),
                spaceApplicationService.getQueryWrapper(SpaceQueryRequest));
        // 获取封装类
        return ResultUtils.success(spaceApplicationService.getSpaceVOPage(SpacePage, request));
    }
    /**
     * 编辑空间（给用户使用）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Space space = SpaceAssembler.toSpaceEntity(spaceEditRequest);
        //自动填充数据
        spaceApplicationService.fillSpaceBySpaceLevel(space);
        // 设置编辑时间
        space.setEditTime(new Date());
        // 数据校验
        space.validSpace(false);
        User loginUser = userApplicationService.getLoginUser(request);
        // 判断是否存在
        long id = spaceEditRequest.getId();
        Space oldSpace = spaceApplicationService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        spaceApplicationService.checkSpaceAuth(oldSpace, loginUser);
        // 操作数据库
        boolean result = spaceApplicationService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 查看当前图库 有哪些空间等级
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values()) // 获取所有枚举
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }

}
