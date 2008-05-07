package com.googlecode.jj1.server;

import com.googlecode.jj1.JsonRpcException;

public class CannotCallMethodException extends JsonRpcException{

	public CannotCallMethodException(String error, Exception e) {
		super(error, e);
	}

	public CannotCallMethodException(String error) {
		super(error);
	}

}
