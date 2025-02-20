package com.katomegumi.zxpicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;

import lombok.Getter;

/**
 * 图片审核状态 枚举类
 */
@Getter
public enum PictureReviewStatusEnum {
    REVIEW("待审核",0),
    PASS("通过",1),
    REJECT("拒绝",2);

    private final String text;
    private final int value;

    PictureReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 通过value获取 枚举类
     * @param value
     * @return
     */
    public static PictureReviewStatusEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureReviewStatusEnum e : PictureReviewStatusEnum.values()) {
            if (e.getValue()==value) {
                return e;
            }
        }
        return null;
    }
}
