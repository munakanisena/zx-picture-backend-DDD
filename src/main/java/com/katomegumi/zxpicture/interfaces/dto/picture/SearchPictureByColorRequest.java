package com.katomegumi.zxpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 相似图片搜索请求体
 */
@Data
public class SearchPictureByColorRequest implements Serializable {
    /**
     * 主色调
     */
    private String PicColor;
    /**
     *空间id
     */
    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
