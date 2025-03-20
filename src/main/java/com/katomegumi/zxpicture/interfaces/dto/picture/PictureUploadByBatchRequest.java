package com.katomegumi.zxpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 通过接口 批量获取图片
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 获取数量
     */
    private Integer count;

    /**
     * 图片前缀
     */
    private String NamePrefix;

    private static final long serialVersionUID = 1L;
}
