package com.googlecode.jj1;

public class JsonRpcException extends RuntimeException {
	public JsonRpcException(String error) {
		super(error);
	}

	public JsonRpcException(String error, Exception e) {
		super(error, e);
	}
}
