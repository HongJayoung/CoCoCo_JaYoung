package com.kos.CoCoCo.sol.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.WorkVO;

public interface WorkRepository extends CrudRepository<WorkVO, Long> {
	public List<WorkVO> findByTeam(TeamVO team);
	
	public List<WorkVO> findByTeamOrderByWorkEnd(TeamVO team);
	
	
	@Query(value="SELECT count(*) FROM (SELECT * FROM WORKS w WHERE w.team_team_id=?1)"
			+ " r WHERE r.WORK_STATUS = 'ongoing'", nativeQuery=true)
	public Integer numOngoing(TeamVO team);
	
	@Query(value="SELECT count(*) FROM (SELECT * FROM WORKS w WHERE w.team_team_id=?1)"
			+ " r WHERE r.WORK_STATUS = 'plan'", nativeQuery=true)
	public Integer numPlan(TeamVO team);
	
	@Query(value="SELECT count(*) FROM (SELECT * FROM WORKS w WHERE w.team_team_id=?1)"
			+ " r WHERE r.WORK_STATUS = 'finish'", nativeQuery=true)
	public Integer numFinish(TeamVO team);
}
