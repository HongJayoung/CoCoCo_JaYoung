package com.kos.CoCoCo.choi;

import com.kos.CoCoCo.vo.UserVO;

public interface UserServiceC {
	// 아이디 체크
	public UserVO idCheck(String id);
	
	// 아이디,비밀번호 일치여부 체크
	public boolean pwCheck(String id, String pw);

}