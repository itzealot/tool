package com.sky.projects.tool.common;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RandomsTest extends TestCase {

	public RandomsTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(RandomsTest.class);
	}

	public void testApp() {
		assertTrue(true);
		System.out.println(Randoms.randomAlphanumeric(10));
		System.out.println(Randoms.randomAlphabetic(10));
	}

	public void testRandomMac() {
		System.out.println(Randoms.randomMacNoSpliter());
		System.out.println(Randoms.randomMacWithSpliter());
	}

	public void testRandomNumeric() {
		System.out.println(Randoms.randomNumeric(10));
	}

	public void testRandomMobilePhone() {
		System.out.println(Randoms.randomMobilePhone("138"));
		System.out.println(Randoms.randomMobilePhoneWithSpliter("138"));
		System.out.println(Randoms.randomMobilePhoneWithSpliter("138", ' '));
		System.out.println(Randoms.randomMobilePhone(Randoms.MobilePrefix.YD));
	}

	public void testrandomAscii() {
		System.out.println(Randoms.randomAscii(11));
	}

	public void testRandomTime() {
		System.out.println(new Date(Randoms.randomTime(0)));
		System.out.println(new Date(Randoms.randomTime(1)));
	}

	public void testRandomFloat() {
		System.out.println(Randoms.randomFloat());
		System.out.println(Randoms.randomFloat(1));
		System.out.println(Randoms.randomFloat(1.5f));
	}

	public void testRandomDouble() {
		System.out.println(Randoms.randomDouble());
		System.out.println(Randoms.randomDouble(1));
		System.out.println(Randoms.randomDouble(1.5));
	}
}
