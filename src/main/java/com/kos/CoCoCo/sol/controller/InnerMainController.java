package com.kos.CoCoCo.sol.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.kos.CoCoCo.ja0.repository.TeamRepository;
import com.kos.CoCoCo.sol.repository.NoticeFileRepository;
import com.kos.CoCoCo.sol.repository.NoticeRepository;
import com.kos.CoCoCo.sol.repository.WorkRepository;
import com.kos.CoCoCo.sol.vo.NoticeFile;
import com.kos.CoCoCo.sol.vo.NoticePageVO;
import com.kos.CoCoCo.vo.NoticeVO;
import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.WorkVO;

@Controller
public class InnerMainController {
	
	@Autowired
	NoticeRepository noticeRepo;
	
	@Autowired
	NoticeFileRepository noticeFRepo;
	
	@Autowired
	TeamRepository tRepo;
	
	@Autowired
	WorkRepository wRepo;
	
	@GetMapping("/main/notice")
	public String mainNotice(Model model, NoticeVO notice, HttpSession session) {
		
		Long teamId = (Long)session.getAttribute("teamId");
		TeamVO team = tRepo.findById(teamId).get();
		notice.setTeam(team);
		
		List<NoticeVO> mnlist = noticeRepo.findByTeamOrderByNoticeRegDateDesc(team);
				
		model.addAttribute("mnlist", mnlist);
		
		return "notice/main_notice";
	}
	
	
	@GetMapping("/main/notice/detail/{noticeId}")
	public String detail(NoticeVO notice, NoticePageVO page, @PathVariable Long noticeId, Model model) {
		model.addAttribute("pageVO", page);
		model.addAttribute("notice", noticeRepo.findById(noticeId).get());
		
		List<NoticeFile> nflist = noticeFRepo.findByNotice(notice);
		model.addAttribute("nflist", nflist);
				
		return "notice/noticedetail";
	}
	
	
	@GetMapping("/main/summary")
	public String summaryWorks(WorkVO work, HttpSession session, Model model) {
		
		Long teamId = (Long)session.getAttribute("teamId");
		TeamVO t = tRepo.findById(teamId).get();
		work.setTeam(t);
		
		List<WorkVO> wlist = wRepo.findByTeam(t);
		int workNum = wlist.size();

		int numOngoing = wRepo.numOngoing(t);
		int numPlan = wRepo.numPlan(t);
		int numFinish = wRepo.numFinish(t);
			
		Integer[] arr= { numPlan, numOngoing, numFinish };
		model.addAttribute("arr", arr);
		model.addAttribute("workNum", workNum);
					
		return "work/summary";
	}
	
}
