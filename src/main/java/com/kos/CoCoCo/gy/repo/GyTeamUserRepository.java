package com.kos.CoCoCo.gy.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.TeamUserMultikey;
import com.kos.CoCoCo.vo.TeamUserVO;

public interface GyTeamUserRepository extends CrudRepository<TeamUserVO, TeamUserMultikey>{
	
	@Query(value="select * from TEAM_USER where team_team_id =?1 ", nativeQuery = true)
	public List<TeamUserVO> findByTeam(Long team_id);
	
}
