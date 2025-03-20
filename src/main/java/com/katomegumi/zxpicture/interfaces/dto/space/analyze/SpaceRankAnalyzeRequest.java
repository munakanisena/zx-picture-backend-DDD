package com.katomegumi.zxpicture.interfaces.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员使用 查看空间排名
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 排名前 N 的空间
     */
    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}
