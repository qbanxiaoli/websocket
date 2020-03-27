package com.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.OnMessage;
import java.security.Principal;
import java.util.Map;

/**
 * @author qbanxiaoli
 * @description
 * @create 2019-05-11 13:55
 */
@Slf4j
@Controller
public class WebSocketController {

    //spring提供的发送消息模板
    private final SimpMessagingTemplate messagingTemplate;

    private final SimpUserRegistry userRegistry;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate, SimpUserRegistry userRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.userRegistry = userRegistry;
    }

    @GetMapping("/get")
    public String index() {
        return "/index";
    }

    //广播推送消息
    @Scheduled(fixedRate = 10000)
    public void sendTopicMessage() {
        log.info("当前在线人数:" + userRegistry.getUserCount());
        int i = 1;
        for (SimpUser user : userRegistry.getUsers()) {
            log.info("用户" + i++ + "---" + user);
            messagingTemplate.convertAndSendToUser(user.getName(), "/queue/message", "服务器主动推的数据");

        }
    }

    /**
     * 接收用户信息
     */
    @MessageMapping(value = "/principal")
    public void test() {
        log.info("当前在线人数:" + userRegistry.getUserCount());
        int i = 1;
        for (SimpUser user : userRegistry.getUsers()) {
            log.info("用户" + i++ + "---" + user);
            messagingTemplate.convertAndSendToUser(user.getName(), "/queue/entity", "服务器主动推的数据");

        }
    }

    /**
     * 接收数据体
     */
    @MessageMapping(value = "/P2P")
    public void templateTest(Principal principal, Map<String, String> data) {
        log.info("当前在线人数:" + userRegistry.getUserCount());
        int i = 1;
        for (SimpUser user : userRegistry.getUsers()) {
            log.info("用户" + i++ + "---" + user);
        }
        //发送消息给指定用户
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/entity", "服务器主动推的数据");
    }


    /**
     * 接收路径参数
     */
    @MessageMapping(value = "/path/{name}/{company}")
    public void pathTest(Principal principal, @DestinationVariable String name, @DestinationVariable String company) {
        log.info("当前在线人数:" + userRegistry.getUserCount());
        int i = 1;
        for (SimpUser user : userRegistry.getUsers()) {
            log.info("用户" + i++ + "---" + user);
        }
        //发送消息给指定用户
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/entity", "服务器主动推的数据");
    }

    /**
     * 接收header参数
     */
    @MessageMapping(value = "/header")
    public void headerTest(Principal principal, @Header String one, @Header String two) {
        log.info("当前在线人数:" + userRegistry.getUserCount());
        int i = 1;
        for (SimpUser user : userRegistry.getUsers()) {
            log.info("用户" + i++ + "---" + user);
        }
        //发送消息给指定用户
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/entity", "服务器主动推的数据");
    }

    /**
     * 接收HttpSession数据
     */
    @MessageMapping(value = "/httpsession")
    public void httpsession(StompHeaderAccessor accessor) {
        String name = (String) accessor.getSessionAttributes().get("name");
        System.out.println(1111);
    }

    /**
     * 接收param数据
     */
    @MessageMapping(value = "/param")
    public void param(String name) {
        System.out.println(1111);
    }

    /**
     * 广播
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/getResponse")
    public ResponseEntity topic() {
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

}
