package com.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author qbanxiaoli
 * @description
 * @create 2020-04-01 13:41
 */
@Slf4j
@AllArgsConstructor
@Controller
public class WebSocketController {

    //spring提供的发送消息模板
    private SimpMessagingTemplate messagingTemplate;

    private SimpUserRegistry userRegistry;

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
}