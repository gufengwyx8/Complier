package com.compiler.model;

public class Word {

	public static final Word NULL_WORD = new Word("null", null);
	public static final Word START_WORD = new Word("S", null);
	public static final Word END_WORD = new Word("#", null);

	private WordType wordType;
	private String attribute;
	private int lineCount, wordCount, position;

	public Word() {

	}

	public Word(String attribute, WordType wordType) {
		this.attribute = attribute;
		this.wordType = wordType;
	}

	public WordType getWordType() {
		return wordType;
	}

	public void setWordType(WordType wordType) {
		this.wordType = wordType;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String toString() {
		if (wordType == WordType.FLAG) {
			return attribute + "    " + "_";
		}
		return wordType + "    " + attribute;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Word)) {
			return false;
		}
		Word w = (Word) o;
		if (wordType == WordType.IDN && w.wordType == WordType.IDN) {
			return true;
		}
		if (wordType != null && w.wordType != null
				&& wordType.toString().startsWith("INT")
				&& w.wordType.toString().startsWith("INT")) {
			return true;
		}
		return wordType == w.wordType && attribute.equals(w.attribute);
	}

	public int hashCode() {
		if (wordType == null) {
			return attribute.hashCode();
		}
		if (wordType != WordType.FLAG) {
			return wordType.hashCode();
		}
		return wordType.hashCode() + attribute.hashCode();
	}

	public boolean isIDN() {
		if (attribute.startsWith("_")) {
			return true;
		}
		for (int i = 0; i < 10; i++) {
			if (attribute.startsWith(i + "")) {
				return false;
			}
		}
		return true;
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
