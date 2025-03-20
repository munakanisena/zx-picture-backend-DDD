package com.katomegumi.zxpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.katomegumi.zxpicture.domain.space.entily.SpaceUser;
import com.katomegumi.zxpicture.domain.space.repository.SpaceUserRepository;
import com.katomegumi.zxpicture.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Service;

@Service
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}
