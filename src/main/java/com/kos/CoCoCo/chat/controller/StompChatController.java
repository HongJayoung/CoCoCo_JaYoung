package com.kos.CoCoCo.chat.controller;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kos.CoCoCo.chat.dto.ChatMessageDTO;
import com.kos.CoCoCo.chat.repository.MessageRepository;
import com.kos.CoCoCo.choi.UserRepositoryC;
import com.kos.CoCoCo.ja0.repository.TeamRepository;
import com.kos.CoCoCo.ja0.repository.TeamUserRepositoryH;
import com.kos.CoCoCo.ja0.repository.UserRepositoryH;
import com.kos.CoCoCo.vo.TeamUserMultikey;
import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.UserVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    
    @Autowired
	TeamRepository tRepo;
    
    @Autowired
	TeamUserRepositoryH tuRepo;
    
    @Autowired
    private MessageRepository mRepo;
    
    @Autowired
    private UserRepositoryH uRepo;

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDTO message, String data){
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getTeamId(), message);
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDTO message){
    	//ChatMessageDTO chat = ChatMessageDTO.createChat(message.getRoomId(),message.getWriter(),message.getMessage());
    	ChatMessageDTO saveChat = mRepo.save(message);
    	String image = uRepo.findById(message.getWriter()).get().getImage();
    	saveChat.setWriterImg(image);
        template.convertAndSend("/sub/chat/room/" + message.getTeamId(), saveChat);
       // return saveChat.getId();
    }
    
    @GetMapping(value = "/chat/del")
    public String chatdel(Long chatId,Long teamId,HttpSession session, Model model) {
    	System.out.println("삭제 : " + chatId);
    	UserVO user = (UserVO)session.getAttribute("user");
    	mRepo.deleteById(chatId);
    	TeamVO team = tRepo.findById(teamId).get();
		TeamUserMultikey tuId = new TeamUserMultikey(team, user);
		
		session.setAttribute("teamUser", tuRepo.findById(tuId).get());
		model.addAttribute("team", team);
		model.addAttribute("userList", tuRepo.findByTeamId(teamId));
		
		return "main/teamMain";
    }
}