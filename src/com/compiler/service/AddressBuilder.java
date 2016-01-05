package com.compiler.service;

import java.util.List;

import com.compiler.model.AddressCode;
import com.compiler.model.Grammar;
import com.compiler.model.Word;

public class AddressBuilder {

	private static AddressBuilder instance;
	private List<Grammar> grammarList;
	private int temp, line, index;

	public synchronized static AddressBuilder getInstance() {
		if (instance == null) {
			instance = new AddressBuilder();
		}
		return instance;
	}

	private AddressBuilder() {

	}

	public void reset() {
		temp = 1;
		line = 1;
		index = 0;
	}

	public String build(List<Grammar> grammarList) {
		reset();
		this.grammarList = grammarList;
		return buildExpression("S").getCode() + "\nL" + line + ":";
	}

	public AddressCode buildExpression(String parentCode) {
		AddressCode ac = new AddressCode();
		AddressCode ac1, ac2;
		for (; index < grammarList.size(); index++) {
			Grammar g = grammarList.get(index);
			if (g.getFirst().getAttribute().equals(parentCode)) {
				if (g.getFirst().equals(Word.START_WORD)
						&& !g.equals(Grammar.MUTILINE_GRAMMAR)) {
					ac.setLine("L" + (line++));
				}
				if (g.equals(Grammar.EXPRESSION_GRAMMAR)) { // 赋值
					ac1 = buildExpression("id");
					ac2 = buildExpression("E");
					ac.setCode(ac.getLine() + ":\n\t"
							+ (ac2.getCode() == null ? "" : ac2.getCode())
							+ ac1.getPlace() + ":=" + ac2.getPlace());
				} else if (g.getFirst().getAttribute().equals("C")) { // 布尔表达式起始
					ac1 = buildExpression("E");
					ac2 = buildExpression("C'");
					ac.setCode((ac1.getCode() == null ? "" : ac1.getCode())
							+ ac2.getCode());
					ac.setPlace(ac1.getPlace() + ac2.getPlace());
				} else if (g.getFirst().getAttribute().equals("C'")) { // 布尔表达式结束
					ac1 = buildExpression("E");
					ac.setPlace(g.getLast().get(0).getAttribute()
							+ ac1.getPlace());
					ac.setCode(ac1.getCode());
				} else if (g.isStart()) { // 算数表达式起始
					ac1 = buildExpression(g.getLast().get(0).getAttribute());
					ac2 = buildExpression(g.getLast().get(1).getAttribute());
					if (ac2.getCode() != null) {
						ac.setPlace("T" + temp++);
						ac.setCode((ac1.getCode() == null ? "" : ac1.getCode())
								+ ac2.getCode() + ac.getPlace() + ":="
								+ ac1.getPlace() + ac2.getPlace() + "\n\t");
					} else {
						ac.setPlace(ac1.getPlace());
						ac.setCode((ac1.getCode() == null ? "" : ac1.getCode())
								+ (ac2.getCode() == null ? "" : ac2.getCode()));
					}
				} else if (g.isExpression()) { // 算数表达式结束
					ac1 = buildExpression(g.getLast().get(1).getAttribute());
					ac2 = buildExpression(g.getLast().get(2).getAttribute());
					if (ac2.getCode() == null) {
						ac
								.setCode((ac1.getCode() == null ? "" : ac1
										.getCode()));
						ac.setPlace(g.getLast().get(0).getAttribute()
								+ ac1.getPlace());
					} else {
						ac.setPlace(g.getLast().get(0).getAttribute() + "T"
								+ (temp++));
						ac.setCode((ac1.getCode() == null ? "" : ac1.getCode())
								+ ac2.getCode() + ac.getPlace().substring(1)
								+ ":=" + ac1.getPlace() + ac2.getPlace()
								+ "\n\t");
					}
				} else if (g.equals(Grammar.COMBINE_GRAMMAR)) { // 括号表达式
					ac1 = buildExpression("E");
					ac.setCode(ac1.getCode());
					ac.setPlace(ac1.getPlace());
				} else if (g.getFirst().getAttribute().equals("F")) { // 终结符
					Grammar g2 = grammarList.get(++index);
					ac.setPlace(g2.getLast().get(0).getAttribute());
				} else if (g.getFirst().getAttribute().equals("id")) { // 终结符
					ac.setPlace(g.getLast().get(0).getAttribute());
				} else if (g.equals(Grammar.MUTILINE_GRAMMAR)) { // begin/end标识
					ac1 = buildExpression("SList");
					ac.setCode(ac1.getCode());
					ac.setLine(ac1.getLine());
				} else if (g.getFirst().getAttribute().equals("SList")) { // begin/end起始
					ac1 = buildExpression("S");
					ac2 = buildExpression("SList'");
					ac.setCode(ac1.getCode()
							+ (ac2.getCode() == null ? "" : "\n"
									+ ac2.getCode()));
					ac.setLine(ac1.getLine());
				} else if (g.getFirst().getAttribute().equals("SList'") // begin/end结束
						&& g.getLast().size() > 0) {
					ac1 = buildExpression("SList");
					ac.setCode(ac1.getCode());
				} else if (g.equals(Grammar.IF_GRAMMAR)) {
					ac1 = buildExpression("C");
					ac2 = buildExpression("S");
					AddressCode ac3 = buildExpression("S'");
					ac.setCode(ac.getLine() + ":\n\t" + ac1.getCode() + "if "
							+ ac1.getPlace() + " goto " + ac2.getLine()
							+ "\n\t");
					if (ac3.getCode() == null) {
						ac.setCode(ac.getCode() + "goto L" + line + "\n"
								+ ac2.getCode());
					} else {
						ac.setCode(ac.getCode() + "goto " + ac3.getLine()
								+ "\n" + ac2.getCode() + "\n" + ac3.getCode());
					}
				} else if (g.getFirst().getAttribute().equals("S'")
						&& g.getLast().size() > 0) {
					ac = buildExpression("S");
				} else if (g.equals(Grammar.WHILE_GRAMMAR)) {
					ac1 = buildExpression("C");
					ac2 = buildExpression("S");
					ac.setCode(ac.getLine() + ":\n\t" + ac1.getCode() + "if "
							+ ac1.getPlace() + " goto " + ac2.getLine()
							+ "\n\tgoto L" + (line + 1) + "\n" + ac2.getCode()
							+ "\nL" + (line++) + ":\n\tgoto " + ac.getLine());
				}
				System.out.println(g + "    " + ac.getCode() + "    "
						+ ac.getPlace());
				break;
			}
		}
		return ac;
	}
}
