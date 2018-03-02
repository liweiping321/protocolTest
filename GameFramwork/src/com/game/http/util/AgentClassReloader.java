package com.game.http.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import agent.ClassAgent;

public class AgentClassReloader {

	/**
	 * 获取jar包路径
	 * 
	 * @return
	 */
	public static String getJarPath() {
		// jar文件内容
		URL url = ClassAgent.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		try {
			filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"
			// 截取路径中的jar包名
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}
		File file = new File(filePath);
		filePath = file.getAbsolutePath();// 得到windows下的正确路径
		return filePath;
	}

	/**
	 * 重加载class更新目录下的文件
	 * 
	 * @param classNames
	 * @return 返回为null,加载成功，否则失败
	 * @throws Exception
	 */
	public static String reloadClassZip(String updateZip) throws Exception {
		ZipTool tool = new ZipTool();
		String tempUpdatePath = updateZip.substring(0, updateZip.lastIndexOf("/")) + "/temp/";
		File tempUpdateFile = new File(tempUpdatePath);
		if (tempUpdateFile.exists()) {
			File[] updateFiles = tempUpdateFile.listFiles();
			for (File file : updateFiles) {
				file.delete();
			}
		}else {
			tempUpdateFile.mkdir();
		}
		tool.unZipFile(updateZip, tempUpdatePath);

		java.net.URL url = AgentClassReloader.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		String tempJarFilePath = null;
		File tempFile = null;
		try {
			filePath = java.net.URLDecoder.decode(url.getPath(), "UTF-8");
			if (filePath.endsWith(".jar")) {
				tempJarFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "/";
				tempFile = new File(tempJarFilePath);
				if (!tempFile.exists() && !tempFile.isDirectory()) {
					tool.decompress(filePath, tempJarFilePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "解压文件异常, filePath=" + filePath + ",tempJarFilePath=" + tempJarFilePath;
		}
		File[] updateFiles = tempUpdateFile.listFiles();
		boolean isClass = false;
		for (int i = 0; i < updateFiles.length; i++) {
			File file = updateFiles[i];
			String fileName = file.getName();
			if (file.isDirectory()) {
				return "ERROR!!! File is directory, fileName=" + fileName;
			} else if (fileName.endsWith(".class")) {
				// 文件
				byte[] buffer = new byte[(int) file.length()];
				InputStream bis = new BufferedInputStream(new FileInputStream(file));
				bis.mark(buffer.length);
				String classPath = null;
				try {
					bis.read(buffer);
					bis.reset();
					classPath = getClassName(bis);
				} catch (Exception e) {
					e.printStackTrace();
					return fileName + ",error:" + classPath;
				}
				bis.close();
				String className = classPath.replaceAll("/", ".");
				className = loadClass(className, buffer);
				if (className != null) {
					return className;
				}
				isClass = true;
				if (tempJarFilePath != null) {
					File newFile = new File(tempJarFilePath + classPath + ".class");
					if (newFile.exists()) {
						newFile.delete();
					}
					tool.copyFile(file, newFile);
				}
			}
		}
		if (tempJarFilePath != null && isClass) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			File jarFile = new File(filePath);
			jarFile.renameTo(new File(jarFile.getAbsolutePath() + sdf.format(Calendar.getInstance().getTime())));
			jarFile.delete();
			tool.zipFiles(tempFile.listFiles(), new File(jarFile.getAbsolutePath()));
		}
		if (isClass) {
			System.err.println("Class_reloader is over! " + Calendar.getInstance().getTime());
			return null;
		} else {
			return "更新包中没有Class文件";
		}
	}

	public static String getClassName(InputStream ins) throws IOException {
		DataInputStream din = new DataInputStream(ins);
		return ClassAgent.getClassName(din);
	}

	/**
	 * 加载class文件
	 * 
	 * @param className
	 * @param file
	 * @return 返回null为成功，否则失败信息
	 */
	public static String loadClass(String className, File file) {
		try {
			Class<?> loadClass = Class.forName(className.trim());
			ClassAgent.load(loadClass, file);
			return null;
		} catch (ClassNotFoundException e) {
			return "ClassNotFoundException [" + e.getMessage() + ",fileName：" + className + "]";
		} catch (IOException e) {
			return "IOException [" + e.getMessage() + ",fileName：" + className + "]";
		} catch (UnmodifiableClassException e) {
			return "UnmodifiableClassException [" + e.getMessage() + ",fileName：" + className + "]";
		} catch (NullPointerException e) {
			return "NullPointerException [set jvm parameter, for example： -javaagent:c:\\class-reloader.jar," + e.getMessage() + "]";
		} catch (UnsupportedOperationException e) {
			return "UnsupportedOperationException [Now we does not support to adding, deleting, global variables and methods！" + e.getMessage() + ",fileName：" + className + "]";
		} catch (Exception e) {
			return "UnknownException [" + e.getMessage() + ",fileName：" + className + "]";
		}
	}

	/**
	 * 加载class字节码文件
	 * 
	 * @param className
	 * @param theClassFile
	 * @return 返回null为成功，否则失败信息
	 */
	public static String loadClass(String className, byte[] theClassFile) {
		try {
			Class<?> reloadClass = Class.forName(className.trim());
			ClassAgent.load(reloadClass, theClassFile);
		} catch (ClassNotFoundException e) {
			return "ClassNotFoundException [" + e.getMessage() + ",fileName：" + className + "]";
		} catch (UnmodifiableClassException e) {
			return "UnmodifiableClassException [" + e.getMessage() + ",fileName：" + className + "]";
		} catch (NullPointerException e) {
			return "NullPointerException [set jvm parameter, for example： -javaagent:c:\\class-reloader.jar," + e.getMessage() + "]";
		} catch (UnsupportedOperationException e) {
			return "UnsupportedOperationException [Now we does not support to adding, deleting, global variables and methods！" + e.getMessage() + ",fileName：" + className + "]";
		} catch (Exception e) {
			return "UnknownException [" + e.getMessage() + ",fileName：" + className + "]";
		}
		return null;
	}
}
