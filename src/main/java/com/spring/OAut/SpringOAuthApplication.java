package com.spring.OAut;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableOAuth2Sso
public class SpringOAuthApplication {
	
	
	@GetMapping("/")
	public String greeetings( Principal p) {
//		return "HI hello mr/mrs" +p.getName();	
		System.out.println("in the greetings");
		return "Hi 😀😀😀 Welcome " +p.getName();	
	}
	
	
	@GetMapping("/demo")
	public String gregs( Principal p) {
//		return "HI hello mr/mrs" +p.getName();	
		System.out.println("in the greetings");
		return "Hi in demo " +p.getName();		
	}
	
	@GetMapping("/sonar")
	public String sonartest( ) {
		System.out.println("in the greetings");
		return "Hi in sonar"	;	
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringOAuthApplication.class, args);
	}

}



