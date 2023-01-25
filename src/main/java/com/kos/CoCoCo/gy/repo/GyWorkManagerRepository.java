package com.kos.CoCoCo.gy.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.WorkManagerMultikey;
import com.kos.CoCoCo.vo.WorkManagerVO;

public interface GyWorkManagerRepository extends CrudRepository<WorkManagerVO, WorkManagerMultikey>{
	
	@Query(value="select * from WORK_MANAGER where work_work_id =?1 ", nativeQuery = true)
	public List<WorkManagerVO> findByWork(Long work_id);
	
	@Modifying
	@Query(value="delete from WORK_MANAGER where work_work_id=?1", nativeQuery = true)
	public void workManagerDelete(Long work_id);
}
