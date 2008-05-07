package com.googlecode.jj1.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Jj1Servlet extends HttpServlet {
	private Logger logger = Logger.getLogger("jj1");
	private Map<String, ServiceHandler> contexts = new HashMap<String, ServiceHandler>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		String services = getInitParameter("services");
		if (services == null) {
			logger.warning("no services defined. set service parameter for the Jj1Servlet.");
		}

		Map<String, String> pairs = new HashMap<String, String>();
		if (services != null) {
			try {
				for (String pair : services.split(",")) {
					String[] nameValue = pair.split("=");
					String name = nameValue[0].trim();
					String value = nameValue[1].trim();
					pairs.put(name, value);
				}

			} catch (Exception e) {
				logger
						.warning("service parameter for Jj1Servlet not valid. use comma separated name value pairs where name is the context and value the a service instance.");
			}
		}

		for (String name : pairs.keySet()) {
			String value = pairs.get(name);
			try {

				Class<?> c = Class.forName(value);
				Object o = c.newInstance();

				contexts.put(name, new ServiceHandler(o));
				logger.info("added context '" + name + "' with instance '" + value + "'");

			} catch (ClassNotFoundException e) {
				logger.warning("class '" + value + "' for context '" + name + "' not found.");
			} catch (InstantiationException e) {
				logger.warning("couldn't instantiate class '" + value + "' for context '" + name + "'.");
			} catch (IllegalAccessException e) {
				logger.warning("couldn't instantiate class '" + value + "' for context '" + name + "'.");
			}
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader i = new BufferedReader(new InputStreamReader(req.getInputStream(), "utf-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = i.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}

		String context = req.getPathInfo();
		if (context == null) {
			context = "/root";
		}
		if (context.length() > 0) {
			context = context.substring(1);
		}

		ServiceHandler serviceHandler = contexts.get(context);

		if (serviceHandler != null) {
			String result = serviceHandler.handleRequest(sb.toString());
			resp.setContentType("application/json; charset=utf-8");
			resp.getWriter().print(result);
		} else {
			logger.warning("context '" + context + "' is not defined");
		}
	}

}
