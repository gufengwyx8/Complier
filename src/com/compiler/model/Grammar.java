package com.compiler.model;

import java.util.ArrayList;
import java.util.List;

import com.compiler.service.Scanner;

public class Grammar {

	public static final Grammar IF_GRAMMAR = parseGrammar("S->if C then S S'");
	public static final Grammar WHILE_GRAMMAR = parseGrammar("S->while C do S");
	public static final Grammar ELSE_GRAMMAR = parseGrammar("S'->else S");
	public static final Grammar EXPRESSION_GRAMMAR = parseGrammar("S->id:=E");
	public static final Grammar ADD_START_GRAMMAR = parseGrammar("E->T E'");
	public static final Grammar ADD_EXPRESSION_GRAMMAR = parseGrammar("E'->+T E'");
	public static final Grammar MUL_START_GRAMMAR = parseGrammar("T->F T'");
	public static final Grammar MUL_EXPRESSION_GRAMMAR = parseGrammar("T'->*F T'");
	public static final Grammar SUB_EXPRESSION_GRAMMAR = parseGrammar("E'->-T E'");
	public static final Grammar DIV_EXPRESSION_GRAMMAR = parseGrammar("T'->/F T'");
	public static final Grammar COMBINE_GRAMMAR = parseGrammar("F->( E )");
	public static final Grammar MUTILINE_GRAMMAR = parseGrammar("S->begin SList end");

	public static final String ARROW_FLAG = "->";

	private Word first;
	private List<Word> last;

	private Scanner scanner = Scanner.getInstance();

	public Grammar() {
		last = new ArrayList<Word>();
	}

	public Word getFirst() {
		return first;
	}

	public void setFirst(Word first) {
		this.first = first;
	}

	public List<Word> getLast() {
		return last;
	}

	public void setLast(List<Word> last) {
		this.last = last;
	}

	public boolean isExpression() {
		// return last.size() > 0
		// && (last.get(0).getAttribute().equals("+")
		// || last.get(0).getAttribute().equals("-")
		// || last.get(0).getAttribute().equals("*") || last
		// .get(0).getAttribute().equals("/"));
		return (first.getAttribute().equals("E'") || first.getAttribute()
				.equals("T'"))
				&& last.size() > 0;
	}

	public boolean isStart() {
		return this.equals(ADD_START_GRAMMAR) || this.equals(MUL_START_GRAMMAR);
	}

	public boolean isNull() {
		return last.size() == 0;
	}

	public static Grammar parseGrammar(String str) {
		Grammar g = new Grammar();
		g.first = new Word();
		g.first.setWordType(null);
		g.first.setAttribute(str.substring(0, str.indexOf(ARROW_FLAG)));

		String lastStr = str.substring(str.indexOf(ARROW_FLAG)
				+ ARROW_FLAG.length());
		if (!lastStr.trim().equals("null")) {
			g.last = g.scanner.analysis(lastStr);
			for (Word w : g.last) {
				if (w.getAttribute().equals("int8")
						|| w.getAttribute().equals("int10")
						|| w.getAttribute().equals("int16")) {
					w.setWordType(WordType.valueOf(w.getAttribute()
							.toUpperCase()));
				} else if (w.getAttribute().equals("id")) {
					w.setWordType(WordType.IDN);
				} else if (w.getWordType() == WordType.IDN) {
					w.setWordType(null);
				}
			}
		}
		return g;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(first.getAttribute() + " " + ARROW_FLAG + " ");
		if (last.size() == 0) {
			sb.append("Пе");
		} else {
			for (Word w : last) {
				sb.append(w.getAttribute() + " ");
			}
		}
		return sb.toString();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Grammar)) {
			return false;
		}
		Grammar g = (Grammar) o;
		return first.equals(g.first) && last.equals(g.last);
	}

	public int hashCode() {
		return first.hashCode() + last.hashCode();
	}
}
