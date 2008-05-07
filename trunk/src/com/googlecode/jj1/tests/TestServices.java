package com.googlecode.jj1.tests;

import com.googlecode.jj1.server.JsonRpc;

public class TestServices {
	
	/**
	 * this method cannot be called as a json service
	 */
	public void doSomethingDangerous(){
		//...
	}
	
	@JsonRpc
	public String echo(String in) {
		return in;
	}

	@JsonRpc
	public int echo(int in) {
		return in;
	}

	@JsonRpc
	public int add(int a, int b) {
		return a + b;
	}
	
	@JsonRpc
	public String substring(String in, int start, int end){
		return in.substring(start, end);
	}

}
