package com.kos.CoCoCo.gy.controller;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kos.CoCoCo.gy.repo.GyTeamRepository;
import com.kos.CoCoCo.gy.repo.GyTeamUserRepository;
import com.kos.CoCoCo.gy.repo.GyUserRepository;
import com.kos.CoCoCo.gy.repo.GyWorkManagerRepository;
import com.kos.CoCoCo.gy.repo.GyWorkRepository;
import com.kos.CoCoCo.vo.TeamUserVO;
import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.UserVO;
import com.kos.CoCoCo.vo.WorkManagerMultikey;
import com.kos.CoCoCo.vo.WorkManagerVO;
import com.kos.CoCoCo.vo.WorkVO;

@RestController
@RequestMapping("/work/*")
public class GyWorkRestController {
	private static final Logger log = LoggerFactory.getLogger(GyWorkRestController.class);
	
	@Autowired
	GyTeamRepository teamRepo;

	@Autowired
	GyWorkRepository workRepo;
	
	@Autowired
	GyWorkManagerRepository workManagerRepo;
	
	@Autowired
	GyUserRepository userRepo;
	
	@Autowired
	GyTeamUserRepository teamUserRepo;
	
	@GetMapping("/teamWorkList/{team_id}")
	public List<WorkVO> worklist(Model model, @PathVariable Long team_id) {
		TeamVO team = teamRepo.findById(team_id).get();
		List<WorkVO> worklist = workRepo.findByTeamOrderByWorkId(team);
		
		for(WorkVO work:worklist) {
			List<WorkManagerVO> workmanagerlist = workManagerRepo.findByWork(work.getWorkId());
			UserVO[] mlist = new UserVO[workmanagerlist.size()];
			for(int i=0;i<workmanagerlist.size();i++) {
				mlist[i] = workmanagerlist.get(i).getWorkManagerId().getUser();
			}
			work.setManager(mlist);
		}
		return worklist;
	}
	
	@PostMapping(value="/addWork/{team_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public WorkVO addWork(@RequestBody WorkVO work , @PathVariable Long team_id) {
		TeamVO team = teamRepo.findById(team_id).get();
		work.setTeam(team);
		WorkVO insertwork = workRepo.save(work);
		
		for(String m:work.getManagerid()) {
			UserVO user = userRepo.findById(m).get();
			WorkManagerMultikey multikey = new WorkManagerMultikey(insertwork, user);
			WorkManagerVO workmanager = new WorkManagerVO(multikey);
			workManagerRepo.save(workmanager);
		}
		return work;
	}
	
	@GetMapping("/teamUserList/{team_id}")
	public List<UserVO> workmanagerlist(Model model, @PathVariable Long team_id) {
		List<TeamUserVO> user = teamUserRepo.findByTeam(team_id);
		List<UserVO> teamusernamelist = new ArrayList<>();
		
		for(TeamUserVO teamuser:user) {
			UserVO uservo = teamuser.getTeamUserId().getUser();
			teamusernamelist.add(uservo);
		}
		return teamusernamelist;
	}
	
	@GetMapping(value="/workDetail/{work_id}")
	public WorkVO workdetaillist(Model model, @PathVariable Long work_id) {
		WorkVO work = workRepo.findById(work_id).get();
		List<WorkManagerVO> workmanagerlist = workManagerRepo.findByWork(work_id);

		UserVO[] mlist = new UserVO[workmanagerlist.size()];
		for(int i=0;i<workmanagerlist.size();i++) {
			mlist[i] = workmanagerlist.get(i).getWorkManagerId().getUser();
		}
		work.setManager(mlist);
		return work;
	}
	
	@Transactional
	@PutMapping(value="/updateWorkStatus/{work_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public WorkVO updateStatusWork(@RequestBody String status , @PathVariable Long work_id) {
		WorkVO originalwork = workRepo.findById(work_id).get();
		originalwork.setWorkStatus(status);
		
		return workRepo.save(originalwork);
	}
	
	@Transactional
	@PutMapping(value="/updateWork/{work_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public WorkVO updateWork(@RequestBody WorkVO work , @PathVariable Long work_id) {

		WorkVO originalwork = workRepo.findById(work_id).get();
		originalwork.setWorkTitle(work.getWorkTitle());
		originalwork.setWorkText(work.getWorkText());
		originalwork.setWorkStart(work.getWorkStart());
		originalwork.setWorkEnd(work.getWorkEnd());
		originalwork.setWorkStatus(work.getWorkStatus());
		originalwork.setManagerid(work.getManagerid());
		
		WorkVO updatework = workRepo.save(originalwork);
		
		if(work.getManagerid()!=null) {
			workManagerRepo.workManagerDelete(work_id);
			for(String m:updatework.getManagerid()) {
				UserVO user = userRepo.findById(m).get();
				WorkManagerMultikey multikey = new WorkManagerMultikey(updatework, user);
				WorkManagerVO workmanager = new WorkManagerVO(multikey);
				workManagerRepo.save(workmanager);
			}
		}else {
		}
		return updatework;
	}
	
	@Transactional
	@DeleteMapping(value="/deleteWork/{work_id}")
	public void deleteWork(@PathVariable Long work_id) {
		workManagerRepo.workManagerDelete(work_id);
		workRepo.deleteById(work_id);
	}
	
	@GetMapping("/myWorkList/{team_id}/{user_id}")
	public List<WorkVO> myWork(Model model, @PathVariable String team_id, @PathVariable String user_id) {
		List<WorkVO> myworklist = workRepo.findByUser(team_id, user_id);
		return myworklist;
	}
	
	@GetMapping("/teamTodayWorkList/{team_id}")
	public List<WorkVO> todayWork(Model model, @PathVariable Long team_id) {
		TeamVO team = teamRepo.findById(team_id).get();
		List<WorkVO> worklist = workRepo.findByTeamOrderByWorkStart(team);
		
		for(WorkVO work:worklist) {
			List<WorkManagerVO> workmanagerlist = workManagerRepo.findByWork(work.getWorkId());
			String[] mlist = new String[workmanagerlist.size()];
			for(int i=0;i<workmanagerlist.size();i++) {
				mlist[i] = workmanagerlist.get(i).getWorkManagerId().getUser().getUserId();
			}
			work.setManagerid(mlist);
		}
		return worklist;
	}
}
