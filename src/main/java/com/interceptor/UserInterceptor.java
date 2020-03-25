package com.interceptor;

import com.alibaba.fastjson.JSON;
import com.entity.User;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author qbanxiaoli
 * @description
 * @create 2020-03-25 16:22
 */
public class UserInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                //这里就是token
                Object name = ((Map) raw).get(HttpHeaders.AUTHORIZATION);
                if (name instanceof LinkedList) {
                    // 设置当前访问器的认证用户
                    String token = ((LinkedList) name).get(0).toString();
                    String username = null;
                    try {
                        Jwt jwt = JwtHelper.decode(token);
                        Map map = JSON.parseObject(jwt.getClaims(), Map.class);
                        username = map.get("username").toString();
                        if (username == null) {
                            throw new RuntimeException("websocket认证失败");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("websocket认证失败", e);
                    }
                    User user = new User();
                    user.setUsername(username);
                    accessor.setUser(user);
                }
            }
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {

    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        return false;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        return null;
    }

    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {

    }

}
