package com.katomegumi.zxpicture.shared.websocket.disruptor;

import com.katomegumi.zxpicture.shared.websocket.model.PictureEditRequestMessage;
import com.katomegumi.zxpicture.domain.user.entily.User;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * 队列消息事件
 */
@Data
public class PictureEditEvent {

    /**
     * 消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 当前用户的 session
     */
    private WebSocketSession session;
    
    /**
     * 当前用户
     */
    private User user;

    /**
     * 图片 id
     */
    private Long pictureId;

}
