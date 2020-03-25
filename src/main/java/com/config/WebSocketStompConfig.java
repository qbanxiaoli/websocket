package com.config;

import com.interceptor.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @author qbanxiaoli
 * @description
 * @create 2020-03-25 14:53
 */
@Configuration
//注解开启使用STOMP协议来传输基于代理(entity broker)的消息,这时控制器支持使用@MessageMapping,就像使用@RequestMapping一样
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册stomp的端点，发布或者订阅消息时需要连接此端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 允许使用socketJs方式访问，访问点为webSocketServer，允许跨域
        // 在网页上我们就可以通过这个链接
        // http://localhost:8080/websocket
        // 来和服务器的WebSocket连接
        registry.addEndpoint("/websocket")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 配置信息代理（中介转发）
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 点对点应配置一个/user消息代理，广播式应配置一个/topic消息代理
        registry.enableSimpleBroker("/queue", "/topic");
        // 全局使用的消息前缀（客户端订阅路径上会体现出来）
        registry.setApplicationDestinationPrefixes("/app");
        // 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        registry.setUserDestinationPrefix("/user/");
    }

    /**
     * 配置客户端入站通道拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(createUserInterceptor());

    }

    /**
     * 将客户端渠道拦截器加入spring ioc容器
     */
    @Bean
    public UserInterceptor createUserInterceptor() {
        return new UserInterceptor();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(500 * 1024 * 1024);
        registration.setSendBufferSizeLimit(1024 * 1024 * 1024);
        registration.setSendTimeLimit(200000);
    }

}
