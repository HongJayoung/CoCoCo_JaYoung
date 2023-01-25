package com.kos.CoCoCo.ja0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.WorkVO;


public interface WorkRepositoryH extends CrudRepository<WorkVO, Long>{
	
	@Query(value = "select * from works where team_team_id = ?1", nativeQuery = true)
	List<WorkVO> findByTeamId(Long teamId);
	
	@Modifying
	@Query(value = "delete from works where team_team_id = ?1", nativeQuery = true)
	void deleteByTeamId(Long teamId);
}
