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
		String line = "6000076396,张利,511121197410034327,布吉街道国展社区荣超花园11栋201室,36927484,13510181975,,476695101880880596";
		List<String> results = new ArrayList<>();
		ParseLineUtil.parseLine(line, ",", results);
		System.out.println(results.size());
		System.out.println(results);
		results.clear();
		System.out.println("=================");
		ParseLineUtil.parseLine(line, ",", results);
		System.out.println(results.size());
		System.out.println(results);
	}
}
