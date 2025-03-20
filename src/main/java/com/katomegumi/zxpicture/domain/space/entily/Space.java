package com.katomegumi.zxpicture.domain.space.entily;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.katomegumi.zxpicture.domain.space.valueobject.SpaceLevelEnum;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceTypeEnum;
import com.katomegumi.zxpicture.infrastructure.exception.ErrorCode;
import com.katomegumi.zxpicture.infrastructure.exception.ThrowUtils;
import lombok.Data;

/**
 * 空间
 * @TableName tb_space
 */
@TableName(value ="tb_space")
@Data
public class Space implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间类型：0-私有 1-团队
     */
    private Integer spaceType;


    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    private Long totalCount;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public void validSpace(boolean add){
        ThrowUtils.throwIf(this==null, ErrorCode.PARAMS_ERROR);
        String spaceName = this.getSpaceName();
        Integer spaceLevel = this.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getSpaceLevelEnumByValue(spaceLevel);
        Integer spaceType = this.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);
        if(add){
            ThrowUtils.throwIf(StrUtil.isBlank(spaceName), ErrorCode.PARAMS_ERROR,"空间名称不为为空");
            ThrowUtils.throwIf(spaceLevelEnum==null, ErrorCode.PARAMS_ERROR,"空间等级不能为空");
            ThrowUtils.throwIf(spaceType==null, ErrorCode.PARAMS_ERROR,"空间类型能为空");
        }
        //修改数据
        ThrowUtils.throwIf(spaceLevel!=null&&spaceLevelEnum==null, ErrorCode.PARAMS_ERROR,"空间等级不存在");
        ThrowUtils.throwIf(StrUtil.isNotBlank(spaceName)&&spaceName.length()>30,ErrorCode.PARAMS_ERROR,"名称过长");
        ThrowUtils.throwIf(spaceTypeEnum==null&&spaceType!=null,ErrorCode.PARAMS_ERROR,"空间类别不存在");
    }
}