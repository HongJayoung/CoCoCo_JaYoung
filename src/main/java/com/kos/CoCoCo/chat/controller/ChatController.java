package com.kos.CoCoCo.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequestMapping("/chat/*")
public class ChatController {
    
    @GetMapping("/chat.do")
    public String chatGET(){

        log.info("@ChatController, chat GET()");
        
        return "chat/chat";
    }
}