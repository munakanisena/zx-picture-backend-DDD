package com.katomegumi.zxpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.katomegumi.zxpicturebackend.model.dto.picture.PictureQueryRequest;
import com.katomegumi.zxpicturebackend.model.dto.picture.PictureUploadRequest;
import com.katomegumi.zxpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.katomegumi.zxpicturebackend.model.entity.User;
import com.katomegumi.zxpicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author lirui
* @description 针对表【tb_picture(图片)】的数据库操作Service
* @createDate 2025-02-15 19:51:44
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 封装 query对象 便于读取
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取单个图片封装
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取多个图片 封装
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片更新 修改时进行校验
     * @param picture
     */
    void validPicture(Picture picture);
}
