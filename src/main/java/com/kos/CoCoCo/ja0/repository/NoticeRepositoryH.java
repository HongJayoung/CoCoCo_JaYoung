package com.kos.CoCoCo.ja0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.NoticeVO;


public interface NoticeRepositoryH extends CrudRepository<NoticeVO, Long>{
	
	@Query(value = "select * from notice where user_user_id = ?1", nativeQuery = true)
	List<NoticeVO> findByUserId(String userId);
	
	@Query(value = "select * from notice where team_team_id = ?1", nativeQuery = true)
	List<NoticeVO> findByTeamId(Long teamId);

	@Modifying
	@Query(value = "delete from notice where team_team_id = ?1", nativeQuery = true)
	void deleteByTeamId(Long teamId);
}
