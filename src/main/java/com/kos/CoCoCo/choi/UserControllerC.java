package com.kos.CoCoCo.choi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kos.CoCoCo.vo.UserVO;

@Controller
public class UserControllerC {

	@Autowired
	@Qualifier("userService")
	private UserServiceC userService;

	// 아이디 체크
	@PostMapping("/auth/idCheck")
	@ResponseBody
	public int idCheck(@RequestParam("userId") String userId) {
		UserVO user = userService.idCheck(userId);
		return user == null ? 0 : 1;
	}

	// 체크
	@PostMapping("/auth/pwCheck")
	@ResponseBody
	public int pwCheck(@RequestParam("userId") String userId, @RequestParam("pw") String pw) {
		boolean result = userService.pwCheck(userId, pw);
		return result ? 1 : 0;
	}

	
}