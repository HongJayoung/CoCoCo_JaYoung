package com.kos.CoCoCo.gy.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.WorkVO;

public interface GyWorkRepository extends CrudRepository<WorkVO, Long>{
	
	public List<WorkVO> findByTeam(TeamVO team);
	
	public List<WorkVO> findByTeamOrderByWorkId(TeamVO team);
	
	public List<WorkVO> findByTeamOrderByWorkStart(TeamVO team);

	@Query(value="SELECT * FROM WORKS w JOIN WORK_MANAGER wm ON (wm.work_work_id = w.work_id) WHERE w.TEAM_TEAM_ID=?1 AND wm.USER_USER_ID=?2", nativeQuery = true)
	public List<WorkVO> findByUser(String team_id, String user_id);
	
}
