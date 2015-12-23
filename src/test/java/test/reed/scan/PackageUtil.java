package test.reed.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 
 */
public class PackageUtil {
	private static String[] CLASS_PATH_PROP = { "java.class.path",
			"java.ext.dirs", "sun.boot.class.path" };

	private static List<File> CLASS_PATH_ARRAY = getClassPath();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// String path = "java.util";
		String path = "org.slf4j.impl";
		List<String> cls = getClassInPackage(path);
		for (String s : cls) {
			System.out.println(s);
			List<String> ns = getMethodInClass(s, true);
			if (ns != null) {
				for (String n : ns) {
					System.out.println("---------" + n);
				}
			}
		}
	}

	public static List<String> getClassInPackage(String pkgName) {
		List<String> ret = new ArrayList<String>();
		String rPath = pkgName.replace('.', '/') + "/";
		try {
			for (File classPath : CLASS_PATH_ARRAY) {
				if (!classPath.exists())
					continue;
				if (classPath.isDirectory()) {
					File dir = new File(classPath, rPath);
					if (!dir.exists())
						continue;
					for (File file : dir.listFiles()) {
						if (file.isFile()) {
							String clsName = file.getName();
							clsName = pkgName
									+ "."
									+ clsName
											.substring(0, clsName.length() - 6);
							ret.add(clsName);
						}
					}
				} else {
					FileInputStream fis = new FileInputStream(classPath);
					JarInputStream jis = new JarInputStream(fis, false);
					JarEntry e = null;
					while ((e = jis.getNextJarEntry()) != null) {
						String eName = e.getName();
						if (eName.startsWith(rPath) && !eName.endsWith("/")) {
							ret.add(eName.replace('/', '.').substring(0,
									eName.length() - 6));
						}
						jis.closeEntry();
					}
					jis.close();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return ret;
	}

	public static List<String> getMethodInClass(String className, boolean isFull) {
		List<String> r = null;
		try {
			Class clazz = Class.forName(className);
			if (clazz != null) {
				Method[] ms = clazz.getMethods();
				if (ms != null && ms.length > 0) {
					r = new ArrayList<String>(ms.length);
					for (Method m : ms) {
						if (m != null) {
							if (isFull) {
								String returnType = m.getReturnType().getSimpleName();
								Class[] ps = m.getParameterTypes();
								String sb = "(";
								if (ps != null & ps.length > 0) {
									for (int i = 0; i < ps.length; i++) {
										if (ps[i] != null) {
											sb += ps[i].getSimpleName();
											if (i != ps.length - 1) {
												sb += ",";
											}
										}
									}
								}
								sb += ")";
								r.add(returnType + "  " + m.getName() + sb);
							} else {
								r.add(m.getName());
							}

						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return r;
	}

	private static List<File> getClassPath() {
		List<File> ret = new ArrayList<File>();
		String delim = ":";
		if (System.getProperty("os.name").indexOf("Windows") != -1)
			delim = ";";
		for (String pro : CLASS_PATH_PROP) {
			String[] pathes = System.getProperty(pro).split(delim);
			for (String path : pathes)
				ret.add(new File(path));
		}
		return ret;
	}
}