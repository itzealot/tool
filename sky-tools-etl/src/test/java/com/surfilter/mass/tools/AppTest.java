package com.surfilter.mass.tools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testApp() {
		assertTrue(true);
		System.out.println("\"\t\"".replaceAll("\"", ""));
	}

	public void testDisplay() {
		System.out.println(",asdas,|".replace(",", " "));
		System.out.println(",asdas,|".replace(",", " ").trim());
	}
}
