package com.sky.projects.tool.util;

/**
 * AutoCloseable Util
 * 
 * @author zealot
 */
public final class Closeables {

	public static void close(AutoCloseable... closeables) {
		if (closeables != null) {
			for (AutoCloseable closeable : closeables)
				try {
					if (closeable != null)
						closeable.close();
				} catch (Exception e) {
				} finally {
					closeable = null;
				}
		}
	}

	private Closeables() {
	}
}
