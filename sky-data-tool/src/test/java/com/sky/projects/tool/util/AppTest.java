package com.sky.projects.tool.util;

import java.util.ArrayList;
import java.util.List;

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
		String line = "";
		List<String> results = new ArrayList<>();
		ParseLineUtil.parseLine(line, ",", results);
		System.out.println(results);
	}
}
