package com.katomegumi.zxpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.domain.space.repository.SpaceRepository;
import com.katomegumi.zxpicture.infrastructure.mapper.SpaceMapper;
import org.springframework.stereotype.Service;

@Service
public class SpaceRepositoryImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceRepository {
}
