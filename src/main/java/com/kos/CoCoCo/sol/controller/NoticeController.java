package com.kos.CoCoCo.sol.controller;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kos.CoCoCo.ja0.awsS3.AwsS3;
import com.kos.CoCoCo.ja0.repository.TeamRepository;
import com.kos.CoCoCo.ja0.repository.TeamUserRepositoryH;
import com.kos.CoCoCo.ja0.repository.UserRepositoryH;
import com.kos.CoCoCo.sol.repository.NoticeFileRepository;
import com.kos.CoCoCo.sol.repository.NoticeRepository;
import com.kos.CoCoCo.sol.service.NoticeService;
import com.kos.CoCoCo.sol.vo.NoticeFile;
import com.kos.CoCoCo.sol.vo.NoticePageVO;
import com.kos.CoCoCo.sol.vo.PageMaker;
import com.kos.CoCoCo.vo.NoticeVO;
import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.UserVO;
import com.querydsl.core.types.Predicate;

import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@Autowired
	TeamRepository tRepo;

	@Autowired
	TeamUserRepositoryH tuRepo;
	
	@Autowired
	UserRepositoryH uRepo;
	
	@Autowired
	AwsS3 aws;

	@Autowired
	NoticeService noticeService;
	
	@Autowired
	NoticeRepository noticeRepo;
	
	@Autowired
	NoticeFileRepository noticeFRepo;

	@GetMapping("")
	public String noticelist(NoticePageVO pageVO, NoticeVO notice, Model model, HttpSession session) {
				
	    Long teamId = (Long)session.getAttribute("teamId");
		TeamVO team = tRepo.findById(teamId).get();
		notice.setTeam(team);
		
		if(pageVO == null) {
			pageVO = new NoticePageVO();
		}
				
		Pageable page = pageVO.makePaging(0, new String[]{"fixedYN","noticeId"});
		Predicate prediacte = noticeRepo.makePredicate2(pageVO.getType(), pageVO.getKeyword(), pageVO.getKeyword2(), team);
		
		Page<NoticeVO> noticelist = noticeRepo.findAll(prediacte, page);
		PageMaker<NoticeVO> pgmaker = new PageMaker<>(noticelist);
		
		model.addAttribute("noticelist", pgmaker);
		model.addAttribute("pageVO", pageVO);
		notice.getFile();
		
		
		return "notice/noticelist";
	}
	
	
	@GetMapping("/detail")
	public String detail(NoticePageVO pageVO, NoticeVO notice, Long noticeId, Model model) {
		
		model.addAttribute("pageVO", pageVO);
		model.addAttribute("notice", noticeRepo.findById(noticeId).get());
				
		List<NoticeFile> nflist = noticeFRepo.findByNotice(notice);
		model.addAttribute("nflist", nflist);
		
		return "notice/noticedetail";
	}
	
		
	@GetMapping("/insert")
	public String insert() {
				
		return "notice/insertnotice";
	}
	
	@PostMapping("/insert")
	public String insertPost(HttpSession session, Model model, NoticeVO notice, MultipartFile[] files) throws Exception{
		System.out.println("공지vo"+notice);
				
		Long teamId = (Long) session.getAttribute("teamId");
		TeamVO team = tRepo.findById(teamId).get();
		notice.setTeam(team);
		
		UserVO writer = (UserVO) session.getAttribute("user");
		notice.setUser(writer);
				
		noticeService.insert(notice, files, null);
		
		return "redirect:/notice";
	}
	
	
	@GetMapping("/update")
	public String updateGet(NoticeFile noticefile, NoticeVO notice, Long noticeId,
			HttpSession session, Model model ) {
		model.addAttribute("notice", noticeRepo.findById(noticeId).get());
		model.addAttribute("noticefile", noticeFRepo.findByNotice(notice));
		List<NoticeFile> nflist = noticeFRepo.findByNotice(notice);
		model.addAttribute("nflist", nflist);
		return "notice/updatenotice";
	}
	@Transactional
	@PostMapping("/update")
	public String updatePost(NoticeVO notice, MultipartFile[] files, String fileIds, Model model) {
		noticeRepo.findById(notice.getNoticeId()).ifPresentOrElse(original->{
			
			original.setNoticeText(notice.getNoticeText());
			original.setNoticeTitle(notice.getNoticeTitle());
			original.setFixedYN(notice.getFixedYN());
			
			try {
				noticeService.insert(original, files, fileIds);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}, ()->{System.out.println("수정할 데이터 없음");});
		
				
		return "redirect:/notice";
	}
		
	@Transactional
	@GetMapping("/delete")
	public String delete(NoticePageVO pageVO, NoticeVO notice, NoticeFile file, RedirectAttributes attr) {
		
		System.out.println("NOTICE "+ notice);
		
		List<NoticeFile> files = noticeFRepo.findByNotice(notice);
		for(NoticeFile f:files) {
			aws.delete(f.getFilename());
		}
		noticeFRepo.deleteByNoticeId(notice.getNoticeId());
		noticeRepo.deleteById(notice.getNoticeId());
		
		return "redirect:/notice";
	}
	
	
	@GetMapping("/download/{fileId}")
	public ResponseEntity<byte[]> download(@PathVariable Long fileId) throws IOException {
		String dir = "uploads/noticefiles/";
		NoticeFile file = noticeFRepo.findById(fileId).get();
		
		aws.copy(file.getFilename(), dir, file.getOriginFname());

		return aws.download(dir, file.getOriginFname());
	}
		

}

