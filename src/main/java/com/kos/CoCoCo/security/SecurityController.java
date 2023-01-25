package com.kos.CoCoCo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kos.CoCoCo.vo.UserVO;

@Controller
public class SecurityController {

	@Autowired
	MemberService mservice;

	//필수 
	@GetMapping("/auth/login")
	public void login() {
	}

	@GetMapping("/logout")
	public void logout() {		
	}
	
	@RequestMapping("/accessDenied")
	public String accessDenied() {
		return "auth/accessDenied";
	}
	
	@GetMapping("/auth/join")
	public String join() {
	  return "auth/joinForm";	
	}
	
	@PostMapping("/auth/joinProc")
	public String register(@ModelAttribute UserVO member) {
		mservice.joinUser(member);
	  return "redirect:/auth/login";	
	}
	

}
