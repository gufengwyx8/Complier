package com.compiler.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {
	public static String readFile(String path) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new FileReader(path));
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str + "\n");
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				br = null;
			}
		}
		return sb.toString();
	}

	public static String readFileBySystemSource(String path) {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(path)));
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str + "\n");
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				br = null;
			}
		}
		return sb.toString();
	}
}
