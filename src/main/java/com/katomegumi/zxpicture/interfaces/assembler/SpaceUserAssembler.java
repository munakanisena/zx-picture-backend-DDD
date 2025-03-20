package com.katomegumi.zxpicture.interfaces.assembler;

import com.katomegumi.zxpicture.domain.space.entily.SpaceUser;
import com.katomegumi.zxpicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.katomegumi.zxpicture.interfaces.dto.spaceuser.SpaceUserEditRequest;
import org.springframework.beans.BeanUtils;

public class SpaceUserAssembler {

    public static SpaceUser toSpaceUserEntity(SpaceUserAddRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }

    public static SpaceUser toSpaceUserEntity(SpaceUserEditRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }
}
