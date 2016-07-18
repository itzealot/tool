package com.sky.projects.tool.util;

public final class Closeables {

	public static void close(AutoCloseable... closeables) {
		if (closeables != null) {
			for (AutoCloseable closeable : closeables)
				try {
					if (closeable != null)
						closeable.close();
				} catch (Exception e) {
					// TODO
					e.printStackTrace();
				} finally {
					closeable = null;
				}
		}
	}

	private Closeables() {
	}
}
