package com.kos.CoCoCo.ja0.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.kos.CoCoCo.ja0.VO.PageMaker;
import com.kos.CoCoCo.ja0.VO.PageVO;
import com.kos.CoCoCo.ja0.awsS3.AwsS3;
import com.kos.CoCoCo.ja0.repository.TeamRepository;
import com.kos.CoCoCo.ja0.repository.TeamUserRepositoryH;
import com.kos.CoCoCo.ja0.repository.UserRepositoryH;
import com.kos.CoCoCo.vo.TeamUserMultikey;
import com.kos.CoCoCo.vo.TeamUserVO;
import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.UserVO;
import com.querydsl.core.types.Predicate;

@Controller
public class MainController {

	@Autowired
	TeamRepository tRepo;

	@Autowired
	TeamUserRepositoryH tuRepo;
	
	@Autowired
	UserRepositoryH uRepo;
	
	@Autowired
	AwsS3 awsS3;
	
	@GetMapping("/CoCoCo")
	public String teamList(@ModelAttribute PageVO pageVO, HttpSession session, Model model, Principal principal, HttpServletRequest request) {
		model.addAttribute("msg", getRedirectMsg(request));
		
		UserVO user = uRepo.findById(principal.getName()).get();
		List<TeamUserVO> teamList = tuRepo.findByUserId(user.getUserId());
		
		session.setAttribute("user", user);
		session.setAttribute("teamList", teamList);
		
		//teamListPaging
		Pageable page = pageVO.makePaging(1, "joinDate"); //joinDate로 asc
		Predicate predicate =  tuRepo.makePredicate(user.getUserId());
		
		Page<TeamUserVO> team = tuRepo.findAll(predicate, page);
		
		PageMaker<TeamUserVO> pgmaker = new PageMaker<>(team);
		
		for(TeamUserVO tu:pgmaker.getResult().getContent()) {
			String tInfo = tu.getTeamUserId().getTeam().getTeamInfo();
			if(tInfo == null) continue;
			
			int info = tInfo.indexOf("<br>");
			if(info >= 0)
				tu.getTeamUserId().getTeam().setTeamInfo(tInfo.substring(0,info)+" ...");
		}
		
		model.addAttribute("paging", pgmaker);
		
		return "/main/teamList";
	}
	
	@PostMapping("/addTeam")
	public String addTeam(TeamVO team, PageVO pageVO, String fileName, MultipartFile teamPhoto, HttpSession session) throws IOException{
		if (!teamPhoto.isEmpty() && teamPhoto.getOriginalFilename().equals(fileName)) {
			String img = awsS3.upload(teamPhoto, "uploads/teamImages/");
			team.setTeamImg(img);
	    }
		
		if(team.getTeamInfo() != null) {
			team.setTeamInfo(team.getTeamInfo().replaceAll("\r\n", "<br>"));
		}
		
		UserVO user = (UserVO) session.getAttribute("user");
		
		team.setUser(user); 
		team.setInviteCode(UUID.randomUUID().toString()); 
		TeamVO newTeam =  tRepo.save(team);
		TeamUserMultikey teamUserId = new TeamUserMultikey(newTeam, user);
		
		TeamUserVO teamUser = TeamUserVO.builder().teamUserId(teamUserId).userRole("ADMIN").build();
		tuRepo.save(teamUser);
		
		if(pageVO.getPage() == 0) return "redirect:/CoCoCo";
		return "redirect:/CoCoCo?page=" + pageVO.getPage() +"&size=" + pageVO.getSize();
	}
	
	@GetMapping("/deleteTeam/{teamId}")
	public String deleteTeam(@PathVariable Long teamId, HttpSession session) {
		TeamVO team = tRepo.findById(teamId).get();
		UserVO user = (UserVO) session.getAttribute("user");
		TeamUserMultikey teamUserId = new TeamUserMultikey(team, user);
		
		tuRepo.deleteById(teamUserId);
		
		return "redirect:/CoCoCo";
	}
	
	@ResponseBody
	@PostMapping("/findTeam/{code}")
	public Long findTeam(@PathVariable String code, HttpSession session, Model model) {
		TeamVO team = tRepo.findByInviteCode(code);

		if(team == null) return 0L;
		
		UserVO user = (UserVO) session.getAttribute("user");
		TeamUserMultikey id = new TeamUserMultikey(team, user);
		
		tuRepo.findById(id).ifPresentOrElse(i->{
		}, ()->{
			TeamUserVO teamUser = TeamUserVO.builder().teamUserId(new TeamUserMultikey(team, user))
					.userRole("USER").build();
			tuRepo.save(teamUser);
		});
		
		session.setAttribute("teamList", tuRepo.findByUserId(user.getUserId()));
		session.setAttribute("teamId", team.getTeamId());
		
		return team.getTeamId();
	}
	
	@ResponseBody
	@GetMapping("/setTeamId/{teamId}")
	public void setTeamId(@PathVariable Long teamId, HttpSession session) {
		session.setAttribute("teamId", teamId);
	}
	
	@GetMapping("/main")
	public String teamMain(HttpSession session, Model model, HttpServletRequest request) {
		model.addAttribute("msg", getRedirectMsg(request));
		
		UserVO user = (UserVO)session.getAttribute("user");
		Long teamId = (Long) session.getAttribute("teamId");
		
		TeamVO team = tRepo.findById(teamId).get();
		String tInfo = team.getTeamInfo();
		if(tInfo != null) {
			team.setTeamInfo(tInfo.replaceAll("<br>", "\r\n"));
		}
		
		TeamUserMultikey tuId = new TeamUserMultikey(team, user);
		
		session.setAttribute("teamUser", tuRepo.findById(tuId).get());
		session.setAttribute("userList", tuRepo.findByTeamId(teamId));
		session.setAttribute("team", team);
		
		return "main/teamMain";
	}
	
	private String getRedirectMsg(HttpServletRequest request) {
		Map<String, Object> map = (Map<String, Object>) RequestContextUtils.getInputFlashMap(request);
		
		String msg = "";
		if(map != null) {
			msg = (String) map.get("msg");
		}
		
		return msg;
	}
}
