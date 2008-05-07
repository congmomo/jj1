package com.googlecode.jj1.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.stringtree.json.ExceptionErrorListener;
import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONValidatingReader;
import org.stringtree.json.JSONValidatingWriter;
import org.stringtree.json.JSONWriter;


public class ServiceHandler {

	private Object root;

	public ServiceHandler(Object root) {
		this.root = root;
	}

	public String handleRequest(String in) {
		Exception error = null;
		String id = "";

		Map<String, Object> req;
		try {
			JSONReader reader = new JSONValidatingReader(new ExceptionErrorListener());
			req = (Map<String, Object>) reader.read(in);
		} catch (Exception e) {
			error = e;
			req = new HashMap<String, Object>();
			req.put("id", "");
		}

		String methodName = null;
		List<Object> args = null;
		if (error == null) {
			try {
				id = (String) req.get("id");
				methodName = (String) req.get("method");
				args = (List<Object>) req.get("params");

			} catch (Exception e) {
				error = e;
			}
		}

		Object result = null;
		if (error == null) {
			try {
				result = Introspector.call(root, methodName, args);
			} catch (Exception e) {
				error = e;
			}

		}

		Map<String, Object> errorData = null;
		if (error != null) {
			errorData = new HashMap<String, Object>();
			errorData.put("name", error.getClass().getCanonicalName());
			errorData.put("message", error.getMessage());
			result = null;
			error.printStackTrace();
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("result", result);
		data.put("id", id);
		data.put("error", errorData);
		try {
			JSONWriter writer = new JSONValidatingWriter(new ExceptionErrorListener());
			return writer.write(data);
		} catch (Exception e) {
			return "{error:{name:'JSONEncodeException', message:'Result Object Not Serializable'}, result:null, id:'"
					+ id + "'}";
		}
	}

}
