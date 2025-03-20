package com.katomegumi.zxpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传图片
 */
@Data
public class PictureUploadRequest implements Serializable {
    /**
     * 图片 id（用于修改）
     */
    private Long id;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 空间 id
     */
    private Long spaceId;


    private static final long serialVersionUID = 1L;
}
