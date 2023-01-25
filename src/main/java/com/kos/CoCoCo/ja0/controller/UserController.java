package com.kos.CoCoCo.ja0.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kos.CoCoCo.ja0.awsS3.AwsS3;
import com.kos.CoCoCo.ja0.repository.BoardCategoryRepositoryH;
import com.kos.CoCoCo.ja0.repository.BoardRepositoryH;
import com.kos.CoCoCo.ja0.repository.KakaoUserRepository;
import com.kos.CoCoCo.ja0.repository.MessageRepositoryH;
import com.kos.CoCoCo.ja0.repository.NoticeRepositoryH;
import com.kos.CoCoCo.ja0.repository.ReplyRepositoryH;
import com.kos.CoCoCo.ja0.repository.TeamRepository;
import com.kos.CoCoCo.ja0.repository.TeamUserRepositoryH;
import com.kos.CoCoCo.ja0.repository.UserRepositoryH;
import com.kos.CoCoCo.ja0.repository.WorkManagerRepositoryH;
import com.kos.CoCoCo.ja0.repository.WorkRepositoryH;
import com.kos.CoCoCo.vo.TeamUserVO;
import com.kos.CoCoCo.vo.UserVO;
import com.kos.CoCoCo.vo.WorkManagerMultikey;
import com.kos.CoCoCo.vo.WorkManagerVO;

@Controller
public class UserController {

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
	ReplyRepositoryH rRepo;
	
	@Autowired
	WorkManagerRepositoryH wmRepo;
	
	@Autowired
	WorkRepositoryH wRepo;
	
	@Autowired
	MessageRepositoryH mRepo;
	
	@Autowired
	KakaoUserRepository kRepo;
	
	@Autowired
	AwsS3 awsS3;
	
	@GetMapping("/updateStatus/{str}/{status}")
	public String updateStatus(@PathVariable String str, @PathVariable String status, HttpSession session) {
		UserVO user = (UserVO) session.getAttribute("user");
		
		uRepo.findById(user.getUserId()).ifPresent(i->{
			i.setStatus(status);
			uRepo.save(i);
			session.setAttribute("user", i);
		});
		
		if(str.equals("t")) return "redirect:/main";
		return "redirect:/CoCoCo";
	}
	
	@PostMapping("/user/modify")
	public String modifyMyProfile(UserVO user, String fileName, String from, MultipartFile newPhoto, HttpSession session) {
		uRepo.findById(user.getUserId()).ifPresent(i->{
			if(!newPhoto.isEmpty() && newPhoto.getOriginalFilename().equals(fileName)) {
				//이미지 변경
				try {
					awsS3.delete(i.getImage()); //s3에서도 삭제
					
					String img = awsS3.upload(newPhoto, "uploads/userImages/");
					i.setImage(img);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else if(fileName.trim().equals("")) {
				//이미지 삭제
				awsS3.delete(i.getImage()); //s3에서도 삭제
				i.setImage(null);
			}
			
			//채팅 이름도 같이 바꾸기
			if(!user.getName().equals(i.getName())) {
				mRepo.findByWriter(i.getUserId()).forEach(w->{
					w.setKoreanName(user.getName());
					mRepo.save(w);
				});
			}
			
			i.setCompany(user.getCompany());
			i.setName(user.getName());
			
			uRepo.save(i);
			
			session.setAttribute("user", i);
		});
		
		if(from.equals("t")) return "redirect:/main";
		return "redirect:/CoCoCo";
	}
	
	@Transactional
	@GetMapping("/user/delete")
	public String deleteUser(HttpSession session, RedirectAttributes attr) {
		Optional<UserVO> none = uRepo.findById("XXXXX");
		if(!none.isPresent()) { //없으면 만들기
			UserVO newUser = UserVO.builder().userId("XXXXX").name("알수없음").build();
			uRepo.save(newUser);
		}
		
		UserVO user = (UserVO) session.getAttribute("user");
		if(!adminCounter(user.getUserId())) {
			attr.addFlashAttribute("msg", "관리자 권한을 모두 넘긴 후 탈퇴가 가능합니다!");
			return "redirect:/main";
		}
		
		changeAll(user.getUserId()); 
		
		tuRepo.deleteByUserId(user.getUserId());
		
		awsS3.delete(user.getImage()); //탈퇴회원 이미지 삭제
		uRepo.deleteById(user.getUserId());
		kRepo.deleteByEmail(user.getUserId());
		
		return "redirect:/logout";
	}

	private boolean adminCounter(String userId) {
		List<TeamUserVO> adminTeam = tuRepo.findAdminByUserId(userId); 
		
		if(!adminTeam.isEmpty()) {
			for(TeamUserVO t:adminTeam) {
				List<TeamUserVO> teamAdmin = tuRepo.findAdminByTeamId(t.getTeamUserId().getTeam().getTeamId());
				if(teamAdmin.size() == 1) {
					Long tid = teamAdmin.get(0).getTeamUserId().getTeam().getTeamId();
					if(tuRepo.findAdminByTeamId(tid).size() != 1) return false; //관리자가 나밖에 없으면 탈퇴 불가
					
					tuRepo.delete(teamAdmin.get(0)); //관리자가 나밖에 없는데 팀원도 나만있으면 
					tRepo.deleteById(tid);
				}
				
				for(TeamUserVO a:teamAdmin) {
					tRepo.findById(a.getTeamUserId().getTeam().getTeamId()).ifPresent(i->{
						String creator = i.getUser().getUserId();
						if(creator.equals(userId)) { //내가 만든 워크스페이스의 생성자 정보 변경
							UserVO userNone = uRepo.findById("XXXXX").get();
							i.setUser(userNone);
							tRepo.save(i);
						}
					});
				}
			}
		}
		return true;
	}
	
	private void changeAll(String userId) {
		UserVO userNone = uRepo.findById("XXXXX").get();
		
		//공지
		nRepo.findByUserId(userId).forEach(i->{
			i.setUser(userNone);
			nRepo.save(i);
		});
		
		//게시판
		bRepo.findByUserId(userId).forEach(i->{
			i.setUser(userNone);
			bRepo.save(i);
		});
		
		//게시판 댓글
		rRepo.findByUserId(userId).forEach(i->{
			i.setUser(userNone);
			rRepo.save(i);
		});
		
		//업무 담당자
		List<WorkManagerVO> wmList =  wmRepo.findByUserId(userId);
		for(WorkManagerVO wm:wmList) {
			WorkManagerMultikey wmId = new WorkManagerMultikey(wm.getWorkManagerId().getWork(), userNone);
			
			wmRepo.delete(wm); //원래있던거 지우기
			
			WorkManagerVO newWork = new WorkManagerVO(wmId);
			wmRepo.save(newWork); //담당자 새로 저장
		}
		
		//채팅
		mRepo.findByUserId(userId).forEach(i->{
			i.setWriter(userNone.getUserId());
			i.setKoreanName(userNone.getName());
			mRepo.save(i);
		});
	}
}
