package com.googlecode.jj1.tests;

public class TestServices {
	public String echo(String in) {
		return in;
	}

	public int echo(int in) {
		return in;
	}

	public int add(int a, int b) {
		return a + b;
	}
	
	public String substring(String in, int start, int end){
		return in.substring(start, end);
	}

}
