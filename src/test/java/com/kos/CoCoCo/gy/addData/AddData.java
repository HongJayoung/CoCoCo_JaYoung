package com.kos.CoCoCo.gy.addData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kos.CoCoCo.gy.repo.GyTeamRepository;
import com.kos.CoCoCo.gy.repo.GyUserRepository;
import com.kos.CoCoCo.gy.repo.GyWorkRepository;
import com.kos.CoCoCo.vo.UserVO;

@SpringBootTest
public class AddData {
	
	@Autowired
	GyUserRepository userRepo;
	
	@Autowired
	GyWorkRepository workRepo;
	
	@Autowired
	GyTeamRepository teamRepo;

	//@Test
	public void addUser1() {
		UserVO user = UserVO.builder()
				.userId("bang@naver.com")
				.pw("1234")
				.name("방근영")
				.company("회사")
				.image("guitar-playing.png")
				.status("status")
				.build();
		userRepo.save(user);
	}
	//@Test
	public void addUser2() {
		UserVO user = UserVO.builder()
				.userId("bang2@naver.com")
				.pw("1234")
				.name("방근영2")
				.company("회사")
				.image("guitar-playing.png")
				.status("status")
				.build();
		userRepo.save(user);
	}
	//@Test
	public void addTeam1() {
	
	}
}
