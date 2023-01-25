package com.kos.CoCoCo.ja0.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.kos.CoCoCo.ja0.awsS3.AwsS3;
import com.kos.CoCoCo.ja0.repository.BoardCategoryRepositoryH;
import com.kos.CoCoCo.ja0.repository.BoardRepositoryH;
import com.kos.CoCoCo.ja0.repository.MessageRepositoryH;
import com.kos.CoCoCo.ja0.repository.NoticeFileRepositoryH;
import com.kos.CoCoCo.ja0.repository.NoticeRepositoryH;
import com.kos.CoCoCo.ja0.repository.ReplyRepositoryH;
import com.kos.CoCoCo.ja0.repository.TeamRepository;
import com.kos.CoCoCo.ja0.repository.TeamUserRepositoryH;
import com.kos.CoCoCo.ja0.repository.UserRepositoryH;
import com.kos.CoCoCo.ja0.repository.WorkManagerRepositoryH;
import com.kos.CoCoCo.ja0.repository.WorkRepositoryH;
import com.kos.CoCoCo.vo.TeamUserMultikey;
import com.kos.CoCoCo.vo.TeamUserVO;
import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.UserVO;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

	@Autowired
	TeamUserRepositoryH tuRepo;
	
	@Autowired
	TeamRepository tRepo;
	
	@Autowired
	UserRepositoryH uRepo;
	
	@Autowired
	BoardCategoryRepositoryH bcRepo;
	
	@Autowired
	BoardRepositoryH bRepo;
	
	@Autowired
	NoticeRepositoryH nRepo;
	
	@Autowired
	NoticeFileRepositoryH nfRepo;
	
	@Autowired
	ReplyRepositoryH rRepo;
	
	@Autowired
	WorkManagerRepositoryH wmRepo;
	
	@Autowired
	WorkRepositoryH wRepo;
	
	@Autowired
	MessageRepositoryH mRepo;
	
	@Autowired
	AwsS3 awsS3;

	@GetMapping("/user")
	public String userList(HttpSession session, Model model, HttpServletRequest request) {
		model.addAttribute("msg", getRedirectMsg(request));
		
		Long teamId = (Long) session.getAttribute("teamId");
		model.addAttribute("userList", tuRepo.findByTeamId(teamId));
		
		return "admin/adminUser";
	}
	
	@GetMapping("/deleteUser/{userId}")
	public String deleteUser(@PathVariable String userId, HttpSession session, Model model, RedirectAttributes attr) {
		Long teamId = (Long) session.getAttribute("teamId");
		TeamUserMultikey teamUser = TeamUserMultikey.builder().team(tRepo.findById(teamId).get())
												.user(uRepo.findById(userId).get()).build();
		tuRepo.deleteById(teamUser);
		
		attr.addFlashAttribute("msg", "사용자가 삭제되었습니다.");
		return "redirect:/admin/user";
	}
	
	@GetMapping("/team")
	public String team(HttpSession session, Model model, HttpServletRequest request) {
		model.addAttribute("msg", getRedirectMsg(request));
		return "admin/adminTeam";
	}
	
	@GetMapping("/modify")
	public String modifyForm(HttpSession session, Model model) {
		return "admin/modifyTeam";
	}
	
	@PostMapping("/modify")
	public String modifyTeam(TeamVO team, String fileName, MultipartFile newPhoto, HttpSession session) {
		tRepo.findById(team.getTeamId()).ifPresent(i->{		
			if (!newPhoto.isEmpty() && newPhoto.getOriginalFilename().equals(fileName)) {
				//이미지 변경
				try {
					awsS3.delete(i.getTeamImg()); //s3에서도 삭제
					
					String img = awsS3.upload(newPhoto, "uploads/teamImages/");
					i.setTeamImg(img);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}else if(fileName.trim().equals("")) {
				//이미지 삭제
				awsS3.delete(i.getTeamImg()); //s3에서도 삭제
				i.setTeamImg(null);
			}
			
			if(team.getTeamInfo() != null) {
				i.setTeamInfo(team.getTeamInfo().replaceAll("\r\n", "<br>"));
			}
			
			i.setTeamName(team.getTeamName());
			
			tRepo.save(i);
			
			i.setTeamInfo(team.getTeamInfo());
			session.setAttribute("team", i);
		});
		
		UserVO user = (UserVO) session.getAttribute("user");
		session.setAttribute("teamList", tuRepo.findByUserId(user.getUserId()));
		
		return "redirect:/admin/team";
	}
	
	@Transactional
	@GetMapping("/delete")
	public String deleteTeam(HttpSession session, RedirectAttributes attr) {
		Long teamId = (Long) session.getAttribute("teamId");
		List<TeamUserVO> teamUser = tuRepo.findByTeamId(teamId);
		
		if(teamUser.size() <= 1) {
			deleteAll(teamId);
			attr.addFlashAttribute("msg", "워크스페이스가 삭제되었습니다.");			
			return "redirect:/CoCoCo";
		}
		
		attr.addFlashAttribute("msg", "워크스페이스를 삭제할 수 없습니다.");	
		return "redirect:/admin/team";
	}
	
	private void deleteAll(Long teamId) {
		mRepo.deleteByTeamId(teamId); //채팅
		
		nRepo.findByTeamId(teamId).forEach(i -> {//공지파일
			nfRepo.deleteByNoticeId(i.getNoticeId());
		});
		
		nRepo.deleteByTeamId(teamId); //공지

		wRepo.findByTeamId(teamId).forEach(i -> {
			wmRepo.deleteByWorkId(i.getWorkId()); //업무 담당자
		});;
		
		wRepo.deleteByTeamId(teamId); //업무
		
		bRepo.findByTeamId(teamId).forEach(i -> {
			rRepo.deleteByBoardId(i.getBoardId()); //댓글
		});
		
		bRepo.deleteByTeamId(teamId); //게시글
		bcRepo.deleteByTeamId(teamId); //카테고리
		tuRepo.deleteByTeamId(teamId); //teamUser
		
		TeamVO team = tRepo.findById(teamId).get();
		awsS3.delete(team.getTeamImg()); //워크스페이스 이미지 삭제
		tRepo.deleteById(teamId); //team
	}
	
	@ResponseBody
	@PostMapping("/findUser/{userId}")
	public TeamUserVO findUser(@PathVariable String userId, HttpSession session) {
		Long teamId = (Long) session.getAttribute("teamId");
		
		TeamUserMultikey id = new TeamUserMultikey(tRepo.findById(teamId).get(), uRepo.findById(userId).get());
		return tuRepo.findById(id).get();
	}
	
	@GetMapping("/updateUser/{userId}/{newRole}")
	public String updateUser(@PathVariable String userId, @PathVariable String newRole, HttpSession session) {
		Long teamId = (Long) session.getAttribute("teamId");
		
		TeamUserMultikey id = new TeamUserMultikey(tRepo.findById(teamId).get(), uRepo.findById(userId).get());
		tuRepo.findById(id).ifPresent(i->{
			i.setUserRole(newRole);
			tuRepo.save(i);
		});
		
		return "redirect:/admin/user";
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
