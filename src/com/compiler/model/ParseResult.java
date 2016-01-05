package com.compiler.model;

import java.util.List;

public class ParseResult {
	private List<Grammar> result;
	private Word errorWord;
	private String formatStr;

	public List<Grammar> getResult() {
		return result;
	}

	public void setResult(List<Grammar> result) {
		this.result = result;
	}

	public String getFormatStr() {
		return formatStr;
	}

	public void setFormatStr(String formatStr) {
		this.formatStr = formatStr;
	}

	public Word getErrorWord() {
		return errorWord;
	}

	public void setErrorWord(Word errorWord) {
		this.errorWord = errorWord;
	}
}
