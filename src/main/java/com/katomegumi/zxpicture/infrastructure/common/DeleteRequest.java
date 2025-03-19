package com.katomegumi.zxpicture.infrastructure.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {

    private static  final long serialVersionUID = 1L;

    /**
     * 要删除的id
     */
    private Long id;

}
