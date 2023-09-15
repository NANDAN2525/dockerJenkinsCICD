package com.spring.OAut;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringOAuthApplicationTests {
	
	@Autowired
	SpringOAuthApplication app;
	

	@Test
	public void contextLoads() {
		assertEquals(app.sonartest(), "Hi in sonar");
	}
	@Test
	public void sample() {
		assertEquals("Hi in sonar", "Hi in sonar");
	}
}