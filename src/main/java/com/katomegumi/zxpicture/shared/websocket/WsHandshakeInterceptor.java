package com.katomegumi.zxpicture.shared.websocket;

import cn.hutool.core.util.ObjectUtil;
import com.katomegumi.zxpicture.domain.picture.entily.Picture;
import com.katomegumi.zxpicture.domain.space.entily.Space;
import com.katomegumi.zxpicture.domain.user.entily.User;
import com.katomegumi.zxpicture.domain.space.valueobject.SpaceTypeEnum;
import com.katomegumi.zxpicture.application.service.PictureApplicationService;
import com.katomegumi.zxpicture.application.service.SpaceApplicationService;
import com.katomegumi.zxpicture.application.service.UserApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * websocket 拦截器
 */
@Component
@Slf4j
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserApplicationService userApplicationService;
    @Resource
    private PictureApplicationService pictureApplicationService;
    @Resource
    private SpaceApplicationService spaceApplicationService;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String pictureId = servletRequest.getParameter("pictureId");
            if (pictureId==null){
                log.error("缺少图片参数，拒绝握手");
                return false;
            }
            Picture picture = pictureApplicationService.getById(pictureId);
            if (picture==null){
                log.error("图片不存在");
                return false;
            }

            User loginUser = userApplicationService.getLoginUser(servletRequest);
            if (ObjectUtil.isEmpty(loginUser)){
                log.error("用户未登录");
                return false;
            }

            Long spaceId = picture.getSpaceId();
            if (spaceId==null){
                log.error("空间id不存在");
                return false;
            }else {
                Space space = spaceApplicationService.getById(spaceId);
                if (ObjectUtil.isEmpty(space)){
                    log.error("空间不存在");
                    return false;
                }
                if (!space.getSpaceType().equals(SpaceTypeEnum.TEAM.getValue())){
                    log.error("不是团队空间");
                    return false;
                }
            }
            //补充 websocket会话参数
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId",Long.valueOf(pictureId)); //转化为Long类型
        }
            return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
