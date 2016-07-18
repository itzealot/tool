package com.sky.projects.tool.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GsonTest extends TestCase {
	public GsonTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(GsonTest.class);
	}

	public void testApp() {
		Gson gson = new GsonBuilder().create();
		gson.toJson("Hello", System.out);
		gson.toJson(123, System.out);
	}
}
