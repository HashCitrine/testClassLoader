package com.example;

import com.example.interfaces.TestInterface;

public class Bye implements TestInterface {
	@Override
	public String print() {
		String bye = "Good Bye world!";
		System.out.println(bye);
		return bye;
	}

	@Override
	public String print(String s) {
		String bye = String.format("Good Bye %s!", s);
		System.out.println(bye);

		return bye;
	}
}
