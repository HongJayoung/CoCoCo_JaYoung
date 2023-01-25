package com.kos.CoCoCo.choi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kos.CoCoCo.security.MemberService;
import com.kos.CoCoCo.vo.UserVO;

@Service("userService")
public class UserServiceImpl implements UserServiceC {

	@Autowired
	private UserRepositoryC repo;
	
	@Autowired
	MemberService memberService;
	 
	@Override
    public UserVO idCheck(String id) {
       UserVO user = repo.findById(id).orElse(null);
        return user;
	}
        
	@Override
    public boolean pwCheck(String id, String pw) {
		
        UserVO user =  idCheck(id);
       
       boolean result = memberService.passwordCompare(pw, user);
       
       return result;
	}	
}
