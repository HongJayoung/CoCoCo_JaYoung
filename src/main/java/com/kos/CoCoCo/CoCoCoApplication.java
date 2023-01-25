package com.kos.CoCoCo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class CoCoCoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoCoCoApplication.class, args);
	}

}
