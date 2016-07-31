package com.sky.projects.tool.zookeeper;

import com.sky.projects.tool.zookeeper.support.ZkConnectionImpl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ZkRootTest extends TestCase {
	ZkConnection connection = new ZkConnectionImpl("zt92:2181");

	public ZkRootTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(ZkRootTest.class);
	}

	public void testConnection() throws Exception {
		System.out.println(connection);
	}

	public void testCreateRoot() {
		ZkRoot root = connection.createRoot("/test");
		root.mkdir();
		System.out.println(root.getName());
		System.out.println(root.exists());
		System.out.println(root.isRoot());

		ZkPath path = root.create("/test1");
		path.setData("test1".getBytes()).mkdir();

		System.out.println(new String(path.load()));
		System.out.println(root.children());

		System.out.println(connection.createRoot("/test1").exists());
	}
}
