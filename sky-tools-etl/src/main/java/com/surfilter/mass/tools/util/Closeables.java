package com.surfilter.mass.tools.util;

public final class Closeables {

	public static void close(AutoCloseable... closeables) {
		if (closeables != null) {
			for (AutoCloseable c : closeables)
				try {
					if (c != null)
						c.close();
				} catch (Exception e) {
				} finally {
					c = null;
				}
		}
	}

	private Closeables() {
	}
}
