package com.sky.projects.tool.zookeeper;

import com.sky.projects.tool.zookeeper.support.ZkConnectionImpl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ZkRootTest extends TestCase {

	public ZkRootTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(ZkRootTest.class);
	}

	public void testApp() throws Exception {
		assertTrue(true);

		ZkConnection connection = new ZkConnectionImpl("localhost:2181");
		ZkRoot root = connection.createRoot("/");
		ZkPath path = root.create("/test");

		path.mkdir();

		connection.close();
	}
}
