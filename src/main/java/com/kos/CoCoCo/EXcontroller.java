package com.kos.CoCoCo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EXcontroller {

	@GetMapping("/ex/ex")
	public void A(){
		
	}
	
	@GetMapping("/ex/ex2")
	public String B(){
		return "ex/ex2";
	}
	

	
}
