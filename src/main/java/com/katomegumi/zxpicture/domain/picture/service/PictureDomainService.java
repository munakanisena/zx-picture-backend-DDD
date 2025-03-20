package com.katomegumi.zxpicture.domain.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.katomegumi.zxpicture.domain.picture.entily.Picture;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.katomegumi.zxpicture.interfaces.dto.picture.*;
import com.katomegumi.zxpicture.interfaces.vo.picture.PictureVO;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author lirui
* @description 针对表【tb_picture(图片)】的数据库操作Service
* @createDate 2025-02-15 19:51:44
*/
public interface PictureDomainService  {
    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 封装 query对象 便于读取
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);


    void deletePicture(long pictureId, User loginUser);

    void editPicture(Picture picture, User loginUser);


    /**
     * 审核请求
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest,User loginUser);

    /**
     * 更新或者修改 都需要进行审核
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取图片
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    @Async
    void clearPictureFile(Picture picture);

    void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 根据主色调查找相似图片
     * @param spaceId
     * @param picColor
     * @param loginUser
     * @return
     */
    List<PictureVO> searchPictureByPicColor(Long spaceId, String picColor, User loginUser);

    /**
     * 批处理图片
     * @param pictureEditByBatchRequest
     * @param loginUser
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    /**
     * 创建扩图任务
     * @param createPictureOutPaintingTaskRequest
     * @param loginUser
     * @return 任务ID
     */
    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
}
