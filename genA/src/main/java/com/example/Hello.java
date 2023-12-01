package com.example;

import com.example.interfaces.TestInterface;

public class Hello implements TestInterface {
	@Override
	public String print() {
		String hello = "Hello World!";
		System.out.println(hello);
		return hello;
	}

	@Override
	public String print(String s) {
		String hello = String.format("Hello %s!", s);
		System.out.println(hello);

		return hello;
	}
}
