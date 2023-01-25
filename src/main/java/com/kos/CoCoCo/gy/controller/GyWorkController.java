package com.kos.CoCoCo.gy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kos.CoCoCo.gy.repo.GyTeamRepository;
import com.kos.CoCoCo.gy.repo.GyTeamUserRepository;
import com.kos.CoCoCo.gy.repo.GyWorkManagerRepository;
import com.kos.CoCoCo.gy.repo.GyWorkRepository;

@Controller
@RequestMapping("/work/*")
public class GyWorkController {

	@Autowired
	GyWorkRepository workRepo;

	@Autowired
	GyTeamRepository teamRepo;

	@Autowired
	GyWorkManagerRepository workmanagerRepo;

	@Autowired
	GyTeamUserRepository teamuserRepo;

	@GetMapping("/work")
	public String workCalendar() {
		return "work/calendar";
	}

	@GetMapping("/worklist")
	public String todayWork() {
		return "work/todayWorklist";
	}
}
