package com.katomegumi.zxpicture.infrastructure.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.katomegumi.zxpicture.infrastructure.exception.BusinessException;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
            String fileUrl = (String) inputSource;
            HttpUtil.downloadFile(fileUrl,file);
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        //从 URL 中提取文件名
        return FileUtil.getName(fileUrl);
    }

    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        {
            //判断是否为空
            ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR);
            //url格式是否正确
            try {
                new URL(fileUrl);
            } catch (MalformedURLException e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"地址格式不正确");
            }
            //校验http协议
            ThrowUtils.throwIf(!(fileUrl.startsWith("http://")||fileUrl.startsWith("https://")),ErrorCode.PARAMS_ERROR,"仅支持 HTTP 或 HTTPS 协议的文件地址");
            //URL内容是否正确 是图片
            HttpResponse response=null;
            try {
                response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
                // 未正常返回，无需执行其他判断
                if (response.getStatus() != HttpStatus.HTTP_OK) {
                    return;
                }
                // 校验文件类型
                String contentType = response.header("Content-Type");
                // 允许的图片类型
                if (StrUtil.isNotBlank(contentType)) {
                    final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                    ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                            ErrorCode.PARAMS_ERROR, "文件类型错误");
                }
                //判断文件大小
                String contentLength = response.header("Content-Length");
                try {
                    if (StrUtil.isNotBlank(contentLength)){
                        long contentLong = Long.parseLong(contentLength);
                        final long ONE_M=1024*1024L;
                        ThrowUtils.throwIf(contentLong>2*ONE_M,ErrorCode.PARAMS_ERROR,"文件大小不能超过2M");
                    }
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            } finally {
                //一定记得释放资源
                if (response!=null) {
                    response.close();
                }
            }
        }
    }
}
