package com.kos.CoCoCo.gy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GyEXcontroller {

	@GetMapping("/gy/work")
	public String A(){
		return "hello";
	}
	
}
