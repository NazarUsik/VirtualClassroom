package com.infostroy.usik.controller;

import com.infostroy.usik.modal.entity.ChatMessage;
import com.infostroy.usik.modal.entity.Student;
import com.infostroy.usik.modal.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * This class is designed to handle socket requests, such as '/ sent Message', '/ addUser', '/ allUsers'.
 *
 * @author N.Usik
 */
@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private StudentService service;

    /**
     * Request to send message.
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/publicChatRoom")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        logger.info("Request to send message: {}", chatMessage);

        if (chatMessage.getType().getName().equals(ChatMessage.MessageType.HAND_UP.getName())) {
            Student student = service.findByName(chatMessage.getSender()).orElse(null);
            if (student != null && student.getHand().equals(Student.HandStatus.DOWN.getName())) {
                student.setHand(Student.HandStatus.UP);
                service.save(student);
            }
        } else if (chatMessage.getType().getName().equals(ChatMessage.MessageType.HAND_DOWN.getName())) {
            Student student = service.findByName(chatMessage.getSender()).orElse(null);
            if (student != null && student.getHand().equals(Student.HandStatus.UP.getName())) {
                student.setHand(Student.HandStatus.DOWN);
                service.save(student);
            }
        }

        return chatMessage;
    }


    /**
     * Request to add user.
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/publicChatRoom")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        logger.info("Request to add User in chat, message: {}", chatMessage);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }


    /**
     * Request to receive all users.
     */
    @MessageMapping("/chat.allUsers")
    @SendTo("/topic/publicChatRoom")
    public ChatMessage allUsers() {
        List<Student> list = service.listAll();

        StringBuilder builder = new StringBuilder();
        String delimiter = "";

        for (Student student : list) {
            builder.append(delimiter).append(student.getName()).append("&").append(student.getHand());
            delimiter = "|";
        }

        logger.info("Request to all Users in chat : {}", builder.toString());

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(" ");
        chatMessage.setType(ChatMessage.MessageType.USERS);
        chatMessage.setContent(builder.toString());
        return chatMessage;
    }

}
