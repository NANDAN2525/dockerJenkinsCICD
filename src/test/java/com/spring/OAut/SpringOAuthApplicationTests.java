package com.spring.OAut;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringSecurityApplicationTests {
	
	@Autowired
	SpringOAuthApplication app;
	

	@Test
	void contextLoads() {
		assertEquals(app.sonartest(), "Hi in sonar");
	}

}