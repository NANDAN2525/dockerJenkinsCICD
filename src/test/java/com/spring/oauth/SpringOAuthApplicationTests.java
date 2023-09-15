package com.spring.oauth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringOAuthApplicationTests {
	
	@Test
	public void sample() {
		assertEquals("Hi in sonar", "Hi in sonar");
	}
}