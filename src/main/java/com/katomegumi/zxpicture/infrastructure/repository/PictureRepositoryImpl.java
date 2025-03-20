package com.katomegumi.zxpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.katomegumi.zxpicture.domain.picture.entily.Picture;
import com.katomegumi.zxpicture.domain.picture.repository.PictureRepository;
import com.katomegumi.zxpicture.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Service;

@Service
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {
}
