package com.katomegumi.zxpicture.interfaces.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用于展示空间等级
 */
@Data
@AllArgsConstructor
public class SpaceLevel {
    /**
     * 中文
     */
    private String text;
    /**
     * 指
     */
    private int value;

    /**
     * 图片数量
     */
    private long MaxCount;
    /**
     * 图片大小
     */
    private long MaxSize;
}
