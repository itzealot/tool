package com.sky.projects.tool.common;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Path Util
 * 
 * @author zt
 */
public final class Paths {

	public static final char SEPARATOR = '/';
	public static final String ROOT_PATH = "/";

	/**
	 * 获取路径的子路径
	 * 
	 * @param path
	 * @return
	 */
	public static String subPath(final String path) {
		checkNotNull(path, "path can not be null");

		String source = path.charAt(0) == SEPARATOR ? path.substring(1) : path;

		int index = source.indexOf(SEPARATOR);

		// don't have sub path
		return index == -1 ? "" : source.substring(index);
	}

	public static String getParentPath(final String path) {
		if (path == null || path.equals("") || ROOT_PATH.equals(path)) {
			return "";
		}

		int len = path.length();

		String source = path.charAt(len - 1) == SEPARATOR ? source = path.substring(0, len - 1) : path;

		int index = source.lastIndexOf(ROOT_PATH);

		return index == 0 ? ROOT_PATH : source.substring(0, index);
	}

	/**
	 * 获取路径中目录名称
	 * 
	 * @param path
	 * @return
	 */
	public static String dirName(final String path) {
		if (path == null || path.equals("") || ROOT_PATH.equals(path)) {
			return "";
		}

		int len = path.length();

		String source = path.charAt(len - 1) == SEPARATOR ? path.substring(0, len - 1) : path;

		return source.substring(source.lastIndexOf(ROOT_PATH) + 1);
	}

	/**
	 * 获取路径中第一个目录名称
	 * 
	 * @param path
	 * @return
	 */
	public static String firstDirName(String path) {
		if (path == null || path.equals("") || ROOT_PATH.equals(path)) {
			return "";
		}

		String source = path.charAt(0) == SEPARATOR ? source = path.substring(1) : path;

		int index = source.indexOf(SEPARATOR);

		return index == -1 ? path : source.substring(0, index);
	}

	/**
	 * 获取路径中第一个路径名称
	 * 
	 * @param path
	 * @return
	 */
	public static String firstPath(final String path) {
		if (path == null || path.equals("") || ROOT_PATH.equals(path)) {
			return "";
		}

		String source = firstDirName(path);

		return source.charAt(0) == SEPARATOR ? source : ROOT_PATH + source;
	}

	/**
	 * 是否包含多个路径
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isMulityPath(String path) {
		String source = subPath(path);
		return !("".equals(source) || "/".equals(source));
	}

	private Paths() {
	}
}
