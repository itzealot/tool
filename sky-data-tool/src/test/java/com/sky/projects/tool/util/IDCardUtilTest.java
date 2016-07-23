package com.sky.projects.tool.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class IDCardUtilTest extends TestCase {
	public IDCardUtilTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(IDCardUtilTest.class);
	}

	public void testApp() {
		assertTrue(true);
		System.out.println(IDCardUtil.from15to18(19, "522634520829128"));
		String value = IDCardUtil.from18to15("362201199208015212");
		System.out.println(value);
		System.out.println(IDCardUtil.from15to18(19, value));
	}
}
