package com.kos.CoCoCo.ja0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.WorkManagerMultikey;
import com.kos.CoCoCo.vo.WorkManagerVO;


public interface WorkManagerRepositoryH extends CrudRepository<WorkManagerVO, WorkManagerMultikey>{

	@Query(value = "select * from work_manager where user_user_id = ?1", nativeQuery = true)
	List<WorkManagerVO> findByUserId(String userId);
	
	@Modifying
	@Query(value="delete from work_manager where user_user_id=?1", nativeQuery = true)
	void deleteByUserId(String userId);
	
	@Modifying
	@Query(value="delete from work_manager where work_work_id=?1", nativeQuery = true)
	void deleteByWorkId(Long workId);
}
