package com.sky.projects.tool;

import com.sky.projects.tool.util.FileUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MacTest extends TestCase {
	public MacTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(MacTest.class);
	}

	public void testApp() {
		assertTrue(true);
		
		System.out.println(FileUtil.dealMac("d0a637387cb6"));
		
		System.out.println("张琳|张林".indexOf("|"));
	}
}
