package com.katomegumi.zxpicturebackend.manager;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.katomegumi.zxpicturebackend.common.ResultUtils;
import com.katomegumi.zxpicturebackend.config.CosClientConfig;
import com.katomegumi.zxpicturebackend.exception.BusinessException;
import com.katomegumi.zxpicturebackend.exception.ErrorCode;
import com.katomegumi.zxpicturebackend.exception.ThrowUtils;
import com.katomegumi.zxpicturebackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FileManager {
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    public UploadPictureResult uploadPicture(MultipartFile multipartFile,String uploadPathPrefix) {
        //1.校验文件
        validPicture(multipartFile);
        //2.上传地址
        String uuid= RandomUtil.randomString(16);
        String originFilename = multipartFile.getOriginalFilename();
        String uploadFilename =String.format("%s_%s.%s", DateUtil.formatDate(new Date()),uuid,FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        //3.封装返回类
        File file = null;

        try {
            file=File.createTempFile(uploadPath,null);
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath,file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            String picFormat = imageInfo.getFormat();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();

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
            //4.结束file对象
            deleteTempFile(file);
        }

    }

    public static void deleteTempFile(File file) {
        if (file != null) {
            boolean result = file.delete();
            if (!result) { //获取一下这个绝对路径
                log.error("UploadPictureResult delete error, filepath = {}", file.getAbsolutePath());
            }
        }
    }

    private void validPicture(MultipartFile file) {
        //1.校验是否为空
        ThrowUtils.throwIf(file==null, ErrorCode.PARAMS_ERROR,"文件不能为空");
        //2.文件大小 (1M)
        final long ONE_M=1024*1024L;
        ThrowUtils.throwIf(file.getSize()>2*ONE_M,ErrorCode.PARAMS_ERROR,"文件大小不能超过2M");
        //3.后缀是否符合
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix),ErrorCode.PARAMS_ERROR,"文件类型错误");
    }

}
