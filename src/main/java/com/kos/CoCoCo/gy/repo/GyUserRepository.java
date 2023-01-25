package com.kos.CoCoCo.gy.repo;

import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.UserVO;

public interface GyUserRepository extends CrudRepository<UserVO, String>{

	public UserVO findByName(String name);
}
