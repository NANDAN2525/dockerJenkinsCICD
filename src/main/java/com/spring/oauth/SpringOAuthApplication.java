package com.spring.oauth;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@RestController
//@EnableOAuth2Sso
public class SpringOAuthApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(SpringOAuthApplication.class);

	@GetMapping("/")
	public String greeetings( Principal p) {
		 logger.info("In the greetings");
		    return "Hi 😀😀😀 Welcome " + p.getName();
		
	}
	
	
	@GetMapping("/demo")
	public String gregs( Principal p) {
		 logger.info("In the demo");
		return "Hi in demo " +p.getName();		
	}
		
	@GetMapping("/sonar")
	public String sonartest( ) {
		 logger.info("In the sonar");
		return "Hi in sonar"	;	
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(SpringOAuthApplication.class, args);
	}

}



