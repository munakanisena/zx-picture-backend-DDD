package com.katomegumi.zxpicture.interfaces.assembler;

import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceAddRequest;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceEditRequest;
import com.katomegumi.zxpicture.interfaces.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;

public class SpaceAssembler {

    public static Space toSpaceEntity(SpaceAddRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceUpdateRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceEditRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }
}
