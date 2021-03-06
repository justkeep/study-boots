package com.zccoder.springboot.web.controller;

import com.zccoder.springboot.domain.WiselyMessage;
import com.zccoder.springboot.domain.WiselyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * 演示控制器
 * Created by ZC on 2017/4/4.
 */
@Controller
public class WsController {

    // 当浏览器向服务端发送请求时，通过 @MessageMapping 映射 /welcome 这个地址，类似于 @RequestMapping。
    @MessageMapping("/welcome")
    // 当服务端有消息时，会对订阅了 @SendTo 中的路径的浏览器发送消息。
    @SendTo("/topic/getResponse")
    public WiselyResponse say(WiselyMessage message) throws Exception{
        Thread.sleep(3000);
        return new WiselyResponse("Welcome, " + message.getName() + "!");
    }

    @Autowired
    // 通过 SimpMessagingTemplate 向浏览器发送消息。
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    // 在 Spring MVC 中，可以直接在参数中获得 Principal，Principal 中包含了当前用户的信息。
    public void handleChat(Principal principal,String msg){
        System.out.println("name: "+principal.getName()+" ,message: "+ msg);
        // 这里是一段硬编码，如果发送人是 wyf，则发送给 wisely；如果发送人是 wisely，则发送给 wyf，
        // 可以根据项目实际需要改写此处代码。
        if ("wyf".equals(principal.getName())){
            // 通过 messagingTemplate.convertAndSendToUser 向用户发送消息，
            // 第一个参数时接收消息的用户，第二个是浏览器订阅的地址，第三个是消息本身。
            messagingTemplate.convertAndSendToUser("wisely","/queue/notifications",
                    principal.getName()+"-send:"+msg);
        } else {
            messagingTemplate.convertAndSendToUser("wyf","/queue/notifications",
                    principal.getName()+"-send:"+msg);
        }
    }

}
