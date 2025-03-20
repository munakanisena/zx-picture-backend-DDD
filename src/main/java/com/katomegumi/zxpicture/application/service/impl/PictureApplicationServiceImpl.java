package com.katomegumi.zxpicture.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.katomegumi.zxpicture.domain.picture.service.PictureDomainService;
import com.katomegumi.zxpicture.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;
import com.katomegumi.zxpicture.interfaces.dto.picture.*;

import com.katomegumi.zxpicture.domain.picture.entily.Picture;
import com.katomegumi.zxpicture.domain.user.entily.User;

import com.katomegumi.zxpicture.interfaces.vo.picture.PictureVO;
import com.katomegumi.zxpicture.interfaces.vo.user.UserVO;
import com.katomegumi.zxpicture.application.service.PictureApplicationService;
import com.katomegumi.zxpicture.infrastructure.mapper.PictureMapper;
import com.katomegumi.zxpicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author lirui
* @description 针对表【tb_picture(图片)】的数据库操作Service实现
* @createDate 2025-02-15 19:51:44
*/
@Service
@Slf4j
public class PictureApplicationServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureApplicationService {


    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private PictureDomainService pictureDomainService;

    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        return pictureDomainService.uploadPicture(inputSource, pictureUploadRequest, loginUser);
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture==null,ErrorCode.PARAMS_ERROR);
        picture.validPicture();
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        return pictureDomainService.getQueryWrapper(pictureQueryRequest);
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> records = picturePage.getRecords();
        //设定
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(records)) {
            return pictureVOPage;
        }

        List<PictureVO> pictureVOList = records.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        //为了获得用户的详细信息
        Set<Long> userIds = records.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIds).stream().collect(Collectors.groupingBy(User::getId));

        for (PictureVO pictureVO : pictureVOList) {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userApplicationService.getUserVO(user));
        }
        return pictureVOPage.setRecords(pictureVOList);
    }

    @Override
    public void deletePicture(long pictureId, User loginUser) {
        pictureDomainService.deletePicture(pictureId, loginUser);
    }

    @Override
    public void editPicture(Picture picture, User loginUser) {
        pictureDomainService.editPicture(picture, loginUser);
    }


    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
         pictureDomainService.doPictureReview(pictureReviewRequest,loginUser);
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        pictureDomainService.fillReviewParams(picture,loginUser);
    }

    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        return pictureDomainService.uploadPictureByBatch(pictureUploadByBatchRequest,loginUser);
    }

    @Override
    @Async
    public void clearPictureFile(Picture picture) {
        pictureDomainService.clearPictureFile(picture);
    }
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        pictureDomainService.checkPictureAuth(loginUser, picture);
    }


    @Override
    public List<PictureVO> searchPictureByPicColor(Long spaceId,String picColor,User loginUser){
        return pictureDomainService.searchPictureByPicColor(spaceId,picColor,loginUser);
    }

    @Transactional
    @Override
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
        pictureDomainService.editPictureByBatch(pictureEditByBatchRequest,loginUser);
    }

    @Override
    public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser) {
        return pictureDomainService.createPictureOutPaintingTask(createPictureOutPaintingTaskRequest,loginUser);
    }

}




