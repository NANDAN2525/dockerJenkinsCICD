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
		 logger.info("In the greetings");
		    return "Hi 😀😀😀 Welcome ";
		
	}
	
	
	@GetMapping("/demo")
	public String gregs() {
		 logger.info("In the demo");
		return "Hi in demo 😀😀";		
	}
		
	@GetMapping("/sonar")
	public String sonartest( ) {
		 logger.info("In the sonar");
		return "Hi in sonar 😀"	;	
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(SpringOAuthApplication.class, args);
	}

}



