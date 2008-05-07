package com.googlecode.jj1.tests;

import com.googlecode.jj1.ServiceProxy;

public class ServiceProvider {

	public static ServiceProxy getProxy(String context) {
		return new ServiceProxy("http://127.0.0.1:8080/jj1/jj1/" + context);
	}
	
}
