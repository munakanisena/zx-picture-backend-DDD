package com.katomegumi.zxpicture.infrastructure.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import java.util.Arrays;
import java.util.List;

@Service
public class FilePictureUpload extends PictureUploadTemplate {
    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        //1.校验是否为空
        ThrowUtils.throwIf(multipartFile==null, ErrorCode.PARAMS_ERROR,"文件不能为空");
        //2.文件大小 (1M)
        final long ONE_M=1024*1024L;
        ThrowUtils.throwIf(multipartFile.getSize()>5*ONE_M,ErrorCode.PARAMS_ERROR,"文件大小不能超过2M");
        //3.后缀是否符合
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix),ErrorCode.PARAMS_ERROR,"文件类型错误");
    }
}
