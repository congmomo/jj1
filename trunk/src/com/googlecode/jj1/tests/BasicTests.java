package com.googlecode.jj1.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.jj1.JsonRpcException;
import com.googlecode.jj1.ServiceProxy;

public class BasicTests {
	
	private ServiceProxy proxy;
	
	@Before
	public void setUp() throws Exception {
		proxy = ServiceProvider.getProxy("root");
	}

	@Test
	public void string() {
		String p1 = "little red riding hood";
		String result = (String)proxy.call("echo", p1);
		assertEquals(p1, result);
	}

	@Test
	public void stringWithFunnyChars() {
		String p1 = "ädsf$öü$\t\t\n\ndefe&&&5''''";
		String result = (String)proxy.call("echo", p1);
		assertEquals(p1, result);
	}
	
	@Test
	public void intEcho(){
		int i = ((Number)proxy.call("echo", 100)).intValue();
		assertEquals(100, i);
	}
	
	@Test
	public void addInteger(){
		int result = ((Number)proxy.call("add", 128, 12)).intValue();
		assertEquals(140, result);
	}

	@Test
	public void addDouble(){
		/**
		 * because service method uses int as parameters and return the doubles get truncated
		 */
		double result = ((Number)proxy.call("add", 128.23d, 12.01d)).doubleValue();
		assertEquals(140, result);
	}
	
	@Test
	public void remoteSubstring(){
		String param = "The earliest known printed version was known as Le Petit Chaperon Rouge and had its origins in 17th century French folklore. It was included in the collection Tales and Stories of the Past with Morals. Tales of Mother Goose (Histoires et contes du temps passé, avec des moralités. Contes de ma mère l'Oye), in 1697, by Charles Perrault. As the title implies, this version[8] is both more sinister and more overtly moralized than the later ones. The redness of the hood, which has been given symbolic significance in many interpretations of the tale, was a detail introduced by Perrault.";
		String result = (String)proxy.call("substring", param, 100, 150);
		assertEquals(param.substring(100, 150), result);
	}
	
	@Test(expected = JsonRpcException.class)
	public void callUnknownMethod(){
		proxy.execute("noFunction");
	}
}
