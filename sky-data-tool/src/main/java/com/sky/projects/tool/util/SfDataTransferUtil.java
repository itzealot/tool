package com.sky.projects.tool.util;

public final class SfDataTransferUtil {
	public static String dealMac(String mac) {
		if (mac == null || "MULL".equals(mac) || "".equals(mac)) {
			return mac;
		}
		final char spiliter = '-';
		return mac.length() == 12 ? mac.toUpperCase()
				: new StringBuffer().append(mac.substring(0, 2)).append(spiliter).append(mac.substring(2, 4))
						.append(spiliter).append(mac.substring(4, 6)).append(spiliter).append(mac.substring(6, 8))
						.append(spiliter).append(mac.substring(8, 10)).append(spiliter).append(mac.substring(10, 12))
						.toString().toUpperCase();
	}

	private SfDataTransferUtil() {
	}
}
