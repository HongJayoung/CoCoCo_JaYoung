package com.kos.CoCoCo.ja0.repository;

import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.TeamVO;


public interface TeamRepository extends CrudRepository<TeamVO, Long>{
	
	TeamVO findByInviteCode(String inviteCode);
}
