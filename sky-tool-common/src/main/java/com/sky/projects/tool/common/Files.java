package com.sky.projects.tool.common;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File Util
 * 
 * @author zealot
 */
public final class Files {

	private static final Logger LOG = LoggerFactory.getLogger(Files.class);

	public static void copy(String src, String dir) {
		copy(new File(src), new File(dir));
	}

	public static void copy(File from, File to) {
		try {
			com.google.common.io.Files.copy(from, to);
		} catch (IOException e) {
			LOG.error("copy file error, from:{}, to:{}, {}", from.getAbsolutePath(), to.getAbsolutePath(), e);
		}
	}

	public static void append(CharSequence from, File to, Charset charset) {
		try {
			com.google.common.io.Files.append(from, to, charset);
		} catch (IOException e) {
			LOG.error("append line into file error, to:{}, {}", to.getAbsoluteFile(), e);
		}
	}

	public static void append(List<CharSequence> froms, File to, Charset charset) {
		try {
			for (int i = 0, len = froms.size(); i < len; i++)
				com.google.common.io.Files.append(froms.get(i), to, charset);
		} catch (IOException e) {
			LOG.error("append line into file error, to:{}, {}", to.getAbsoluteFile(), e);
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
			LOG.error("create parent dirs error. path:{}, {}", file.getAbsolutePath(), e);
		}
	}

	public static void move(File from, File to) {
		try {
			com.google.common.io.Files.move(from, to);
		} catch (IOException e) {
			LOG.error("move file error. from:{}, to:{}, {}", from.getAbsolutePath(), to.getAbsolutePath(), e);
		}
	}

	public static void move(String src, String dir) {
		move(new File(src), new File(dir));
	}

	public static MappedByteBuffer map(File file) {
		try {
			return com.google.common.io.Files.map(file);
		} catch (IOException e) {
			LOG.error("map file error, path:{}, {}", file.getAbsolutePath(), e);
			return null;
		}
	}

	public static MappedByteBuffer map(File file, MapMode mode) {
		try {
			return com.google.common.io.Files.map(file, mode);
		} catch (IOException e) {
			LOG.error("map file error, path:{}, mode:{}, {}", file.getAbsolutePath(), mode.toString(), e);
			return null;
		}
	}

	public static MappedByteBuffer map(File file, MapMode mode, long size) {
		try {
			return com.google.common.io.Files.map(file, mode, size);
		} catch (IOException e) {
			LOG.error("map error, path:{}, mode:{}, size:{}, {}", file.getAbsolutePath(), mode.toString(), size, e);
			return null;
		}
	}

	private Files() {
	}
}
