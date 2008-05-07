package com.googlecode.jj1.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * used to call dynamically a method
 * @author creichlin
 */

public class Introspector {

	/**
	 * tries to call a method
	 * @param obj instance where method should be called
	 * @param methodName name of method
	 * @param args list of arguments
	 * @return
	 * @throws CannotCallMethodException if something goes wrong...
	 */
	public static Object call(Object obj, String methodName, List<Object> args) throws CannotCallMethodException {
		Method m = findMethod(obj, methodName, args);

		Class<?>[] dp = m.getParameterTypes();
		Object[] dargs = new Object[dp.length];

		for (int cnt = 0; cnt < dp.length; cnt++) {
			if (dp[cnt].isPrimitive()) {
				if (dp[cnt] == Long.TYPE) {
					dargs[cnt] = ((Number) args.get(cnt)).longValue();
				} else if (dp[cnt] == Byte.TYPE) {
					dargs[cnt] = ((Number) args.get(cnt)).byteValue();
				} else if (dp[cnt] == Short.TYPE) {
					dargs[cnt] = ((Number) args.get(cnt)).shortValue();
				} else if (dp[cnt] == Integer.TYPE) {
					dargs[cnt] = ((Number) args.get(cnt)).intValue();
				} else if (dp[cnt] == Float.TYPE) {
					dargs[cnt] = ((Number) args.get(cnt)).floatValue();
				} else if (dp[cnt] == Double.TYPE) {
					dargs[cnt] = ((Number) args.get(cnt)).doubleValue();
				} else if (dp[cnt] == Boolean.TYPE) {
					dargs[cnt] = args.get(cnt);
				} else {
					throw new RuntimeException("shouldn't ever happen");
				}
			} else {
				dargs[cnt] = args.get(cnt);
			}
		}

		try {
			return m.invoke(obj, dargs);
		} catch (Exception e) {
			throw new CannotCallMethodException("failed to call method named " + methodName, e);
		}
	}

	/**
	 * tries to find a matching method
	 * 
	 * @param obj
	 * @param methodName
	 * @param args
	 * @return
	 * @throws CannotCallMethodException
	 */
	public static Method findMethod(Object obj, String methodName, List<Object> args) throws CannotCallMethodException {
		List<Method> methods = findMethods(obj, methodName, args);

		if (methods.size() == 0) {
			throw new CannotCallMethodException("couldn't find signature for method " + methodName);
		}

		if (methods.size() > 1) {
			throw new CannotCallMethodException("ambiguous method " + methodName);
		}

		return methods.get(0);
	}

	/**
	 * collects all methods of obj which can be called by the given signature
	 * (methodName and arguments list)
	 * 
	 * @param obj
	 * @param methodName
	 *            name of the method, .-ending prepended strings are used to
	 *            select properties.
	 * @param args
	 *            list of all arguments
	 * @return
	 * @throws CannotCallMethodException
	 * @throws CannotCallMethodException
	 */
	public static List<Method> findMethods(Object obj, String methodName, List<Object> args)
			throws CannotCallMethodException {
		if (methodName.indexOf(".") != -1) {
			String o = methodName.substring(0, methodName.indexOf("."));
			try {
				Method m = obj.getClass().getMethod("get" + o.substring(0, 1).toUpperCase() + o.substring(1),
						new Class[] {});
				Object no = m.invoke(obj, new Object[] {});
				return findMethods(no, methodName.substring(methodName.indexOf(".") + 1), args);
			} catch (Exception e) {
				throw new CannotCallMethodException("getter for property '" + o + "' not found", e);
			}
		}

		List<Method> methods = new ArrayList<Method>();

		Method[] ms = obj.getClass().getMethods();

		for (Method m : ms) {
			boolean valid = true;
			Class<?>[] argz = m.getParameterTypes();

			if (!m.getName().equals(methodName)) {
				valid = false;
			}

			if (args.size() != argz.length) {
				valid = false;
			}

			if (valid) {
				for (int cnt = 0; cnt < argz.length; cnt++) {
					Class<?> carg = argz[cnt];
					Object oarg = null;

					if (args.size() > cnt) {
						oarg = args.get(cnt);
					}

					valid = valid && matchesParameter(carg, oarg);
				}
			}

			if (valid) {
				methods.add(m);
			}
		}

		return methods;
	}

	/**
	 * checks if the oarg parameter can match the parameter definition carg
	 * 
	 * @param carg
	 * @param oarg
	 * @return
	 */
	public static boolean matchesParameter(Class<?> carg, Object oarg) {
		if (carg.isPrimitive()) {
			if (carg == Integer.TYPE || carg == Byte.TYPE || carg == Short.TYPE || carg == Long.TYPE
					|| carg == Float.TYPE || carg == Double.TYPE) {
				// if type is a number type
				if (oarg instanceof Number) {
					// and parameter is not a number it won't match
					return true;
				}
				return false;
			}
			if (carg.isInstance(Boolean.TYPE)) {
				// if boolean... argument must be booelan... very simple
				if (oarg instanceof Boolean) {
					return true;
				}
				return false;
			}
		} else {
			// if it's an object (map, list, string, number, boolean) we just
			// check with isInstance or if it's null
			if (oarg != null && !carg.isInstance(oarg)) {
				return false;
			}
		}
		return true;
	}
}
