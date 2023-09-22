package com.spring.oauth;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
//@EnableOAuth2Sso
public class SpringOAuthApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(SpringOAuthApplication.class);

	@GetMapping("/")
	public String greeetings( ) {
		 logger.info("In the greetings module");
		    return "Hi 😀😀😀 Welcome module";
		
	}
	
	
	@GetMapping("/demo")
	public String gregs() {
		 logger.info("In the demo module");
		return "Hi in demo  module 😀😀";		
	}
		
	@GetMapping("/sonar")
	public String sonartest( ) {
		 logger.info("In the sonar module");
		return "Hi in sonar module 😀"	;	
	}
	
	@GetMapping("/check")
	public String check( ) {
		 logger.info("In the check module");
		return "Hi in check module 😀😀😀😀😀😀😀"	;	
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringOAuthApplication.class, args);
	}

}



