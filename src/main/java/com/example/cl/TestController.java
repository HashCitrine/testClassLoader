package com.example.cl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	private final TestFactory testFactory;

	public TestController(TestFactory testFactory) {
		this.testFactory = testFactory;
	}

	@GetMapping("/hello")
	ResponseEntity<String> hello(String message) {
		return ResponseEntity.ok(testFactory.getHello().print(message));
	}

	@GetMapping("/bye")
	ResponseEntity<String> bye(String message) {
		return ResponseEntity.ok(testFactory.getBye().print(message));
	}
}
