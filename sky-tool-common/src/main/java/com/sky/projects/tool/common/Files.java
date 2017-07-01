package com.sky.projects.tool.common;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.List;

/**
 * File Util
 * 
 * @author zealot
 */
public final class Files {

	public static void copy(String src, String dir) {
		copy(new File(src), new File(dir));
	}

	public static void copy(File from, File to) {
		try {
			com.google.common.io.Files.copy(from, to);
		} catch (IOException e) {
		}
	}

	public static void append(CharSequence from, File to, Charset charset) {
		try {
			com.google.common.io.Files.append(from, to, charset);
		} catch (IOException e) {
		}
	}

	public static void append(List<CharSequence> froms, File to, Charset charset) {
		try {
			for (int i = 0, len = froms.size(); i < len; i++)
				com.google.common.io.Files.append(froms.get(i), to, charset);
		} catch (IOException e) {
		}
	}

	public static void append(CharSequence from, File to) {
		append(from, to, Charset.forName("UTF_8"));
	}

	public static void append(List<CharSequence> from, File to) {
		append(from, to, Charset.forName("UTF_8"));
	}

	public static File createTempDir() {
		return com.google.common.io.Files.createTempDir();
	}

	public static void createParentDirs(File file) {
		try {
			com.google.common.io.Files.createParentDirs(file);
		} catch (IOException e) {
		}
	}

	public static void move(File from, File to) {
		try {
			com.google.common.io.Files.move(from, to);
		} catch (IOException e) {
		}
	}

	public static void move(String src, String dir) {
		move(new File(src), new File(dir));
	}

	public static MappedByteBuffer map(File file) {
		try {
			return com.google.common.io.Files.map(file);
		} catch (IOException e) {
			return null;
		}
	}

	public static MappedByteBuffer map(File file, MapMode mode) {
		try {
			return com.google.common.io.Files.map(file, mode);
		} catch (IOException e) {
			return null;
		}
	}

	public static MappedByteBuffer map(File file, MapMode mode, long size) {
		try {
			return com.google.common.io.Files.map(file, mode, size);
		} catch (IOException e) {
			return null;
		}
	}

	private Files() {
	}
}
