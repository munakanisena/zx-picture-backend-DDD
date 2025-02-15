package com.katomegumi.zxpicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.katomegumi.zxpicturebackend.model.entity.Picture;
import com.katomegumi.zxpicturebackend.service.PictureService;
import com.katomegumi.zxpicturebackend.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
* @author lirui
* @description 针对表【tb_picture(图片)】的数据库操作Service实现
* @createDate 2025-02-15 19:51:44
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

}




