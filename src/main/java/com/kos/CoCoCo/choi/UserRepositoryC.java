package com.kos.CoCoCo.choi;

import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.UserVO;

public interface UserRepositoryC extends CrudRepository<UserVO, String>{
	
	public UserVO findByPw(String pw);

}