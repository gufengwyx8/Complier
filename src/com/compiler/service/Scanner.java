package com.compiler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.compiler.model.Word;
import com.compiler.model.WordType;
import com.compiler.util.PropertiesUtil;

public class Scanner {

	public static final String PROPERTIES_PATH = "word.properties";

	public static final String SPLIT_FLAG = " ";

	public static final String INT16_REGEX = "0x[0-9a-fA-F]++";
	public static final String INT8_REGEX = "0[0-7]++";
	public static final String INT10_REGEX = "[0-9]++";

	public static final String FLAG_KEY = "flag";
	public static final String PUNCT_KEY = "punct";

	private String[] flag;
	private String[] punct;

	private static Scanner instance;

	private Scanner() {
		Map<String, String> map = PropertiesUtil
				.getPropertiesMap(PROPERTIES_PATH);
		flag = map.get(FLAG_KEY).split(SPLIT_FLAG);
		punct = map.get(PUNCT_KEY).split(SPLIT_FLAG);
	}

	public synchronized static Scanner getInstance() {
		if (instance == null) {
			instance = new Scanner();
		}
		return instance;
	}

	public List<Word> analysis(String str) {
		List<Word> wordList = new ArrayList<Word>();
		List<Word> splitList = split(str);
		for (Word w : splitList) {
			wordList.add(scan(w));
		}
		return wordList;
	}

	public Word scan(String str) {
		return scan(new Word(str, null));
	}

	public Word scan(Word word) {
		String str = word.getAttribute();
		if (isFlag(str) || isPunct(str)) {
			word.setWordType(WordType.FLAG);
			word.setAttribute(str.toUpperCase());
		} else if (str.matches(INT16_REGEX)) {
			word.setWordType(WordType.INT16);
			word.setAttribute(Integer.parseInt(str.substring(2), 16) + "");
		} else if (str.matches(INT8_REGEX)) {
			word.setWordType(WordType.INT8);
			word.setAttribute(Integer.parseInt(str, 8) + "");
		} else if (str.matches(INT10_REGEX)) {
			word.setWordType(WordType.INT10);
			word.setAttribute(str);
		} else {
			word.setWordType(WordType.IDN);
			word.setAttribute(str);
			if (!word.isIDN()) {
				word.setAttribute("error");
			}
		}
		return word;
	}

	public List<Word> split(String str) {
		List<Word> strList = new ArrayList<Word>();
		int pos = 0;
		int lineCount = 1, wordCount = 1;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == ' ' || c == '\n' || c == '\t') {
				String s = str.substring(pos, i + 1).trim();
				if (!s.equals("")) {
					Word w = new Word(s, null);
					w.setLineCount(lineCount);
					w.setWordCount(wordCount);
					w.setPosition(pos);
					strList.add(w);
				}
				pos = i + 1;
				if (c == ' ') {
					wordCount += s.length();
				} else {
					wordCount = 1;
					lineCount++;
				}
			} else if (isPunct(c + "")) {
				int end = i;
				for (; end < str.length() && isPunct(str.substring(i, end + 1)); end++)
					;
				if (end > i) {
					end--;
				}
				String s = str.substring(pos, i).trim();
				if (!s.equals("")) {
					Word w = new Word(s, null);
					w.setLineCount(lineCount);
					w.setWordCount(wordCount);
					w.setPosition(pos);
					strList.add(w);
					wordCount += s.length();
				}
				Word w2 = new Word(str.substring(i, end + 1), null);
				w2.setLineCount(lineCount);
				w2.setWordCount(wordCount);
				w2.setPosition(i);
				strList.add(w2);
				wordCount += w2.getAttribute().length();
				pos = end + 1;
				i = end;
			}
		}
		String s = str.substring(pos).trim();
		if (!s.equals("")) {
			Word w = new Word(s, null);
			w.setLineCount(lineCount);
			w.setWordCount(wordCount);
			w.setPosition(pos);
			strList.add(w);
		}
		return strList;
	}

	public boolean isPunct(String c) {
		for (String s : punct) {
			if (s.startsWith(c)) {
				return true;
			}
		}
		return false;
	}

	public boolean isFlag(String c) {
		for (String s : flag) {
			if (s.equals(c)) {
				return true;
			}
		}
		return false;
	}

	public String[] getFlag() {
		return flag;
	}

	public void setFlag(String[] flag) {
		this.flag = flag;
	}

	public String[] getPunct() {
		return punct;
	}

	public void setPunct(String[] punct) {
		this.punct = punct;
	}
}
