package com.sky.projects.tool.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 序列化工具类
 * 
 * @author zealot
 */
public final class Serializables {

	public static <T extends Serializable> byte[] serialize(T obj) {
		if (obj == null) {
			return null;
		}

		ObjectOutputStream oos = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			return bos.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			close(bos, oos);
		}
	}

	public static <T extends Serializable> void writeObject(T obj, File file) {
		if (obj == null || file == null) {
			return;
		}

		ObjectOutputStream oos = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
		} catch (IOException e) {
		} finally {
			close(fos, oos);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T readObject(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;

		try {
			ois = new ObjectInputStream(bis);
			return (T) ois.readObject();
		} catch (Exception e) {
			return null;
		} finally {
			close(ois, bis);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T readObject(File file) {
		if (file == null || file.isDirectory() || !file.exists()) {
			return null;
		}

		FileInputStream bis = null;
		ObjectInputStream ois = null;

		try {
			bis = new FileInputStream(file);
			ois = new ObjectInputStream(bis);
			return (T) ois.readObject();
		} catch (Exception e) {
			return null;
		} finally {
			close(ois, bis);
		}
	}

	public static void close(AutoCloseable... clos) {
		if (clos != null) {
			for (int i = 0, len = clos.length; i < len; i++) {
				if (clos[i] != null) {
					try {
						clos[i].close();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private Serializables() {
	}

}
