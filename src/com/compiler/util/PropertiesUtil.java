package com.compiler.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
	public static Map<String, String> getPropertiesMap(String path) {
		Properties p = new Properties();
		Map<String, String> map = new HashMap<String, String>();
		try {
			p.load(ClassLoader.getSystemResourceAsStream(path));
			for (Object key : p.keySet()) {
				map.put(key.toString(), p.getProperty(key.toString()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
}
