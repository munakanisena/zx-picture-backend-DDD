package com.katomegumi.zxpicturebackend.manager.upload;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.katomegumi.zxpicturebackend.config.CosClientConfig;
import com.katomegumi.zxpicturebackend.exception.BusinessException;
import com.katomegumi.zxpicturebackend.exception.ErrorCode;
import com.katomegumi.zxpicturebackend.exception.ThrowUtils;
import com.katomegumi.zxpicturebackend.manager.CosManager;
import com.katomegumi.zxpicturebackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * java设计模版模式
 */
@Slf4j
public abstract class PictureUploadTemplate {
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 文件 和 URL上传
     * @param inputSource 输入源
     * @param uploadPathPrefix
     * @return
     */
    public UploadPictureResult uploadPicture(Object inputSource,String uploadPathPrefix) {
        //1.校验文件
        validPicture(inputSource);
        //2.构造上传地址
        String uuid= RandomUtil.randomString(16);

        String originFilename = getOriginFilename(inputSource);

        String uploadFilename =String.format("%s_%s.%s", DateUtil.formatDate(new Date()),uuid,FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        File file = null;

        try {
            //3.上传到临时文件
            file=File.createTempFile(uploadPath,null);
            // 处理文件来源（本地或 URL）
            processFile(inputSource, file);
            //4.上传到对象存储
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath,file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            String picFormat = imageInfo.getFormat();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            //5.封装返回类
            double picScale= NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(picFormat);

            return uploadPictureResult;

        } catch (Exception e) {
            log.error("图片上传到对象存储失败",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传失败了");
        } finally {
            //6.结束file对象
            deleteTempFile(file);
        }

    }

    protected abstract void processFile(Object inputSource, File file) throws Exception;

    protected abstract String getOriginFilename(Object inputSource);

    protected abstract void validPicture(Object inputSource);


    public static void deleteTempFile(File file) {
        if (file != null) {
            boolean result = file.delete();
            if (!result) { //获取一下这个绝对路径
                log.error("UploadPictureResult delete error, filepath = {}", file.getAbsolutePath());
            }
        }
    }
}
