package com.compiler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.compiler.model.Grammar;
import com.compiler.model.ParseResult;
import com.compiler.model.Word;
import com.compiler.model.WordType;
import com.compiler.util.FileUtil;

public class Parser {

	public static final String GRAMMAR_PATH = "grammar.dat";

	private List<Grammar> grammarList = new ArrayList<Grammar>();
	private Map<Word, List<Grammar>> grammarMap = new HashMap<Word, List<Grammar>>();
	private Scanner scanner = Scanner.getInstance();

	private static Parser instance;

	private Parser() {
		String str = FileUtil.readFileBySystemSource(GRAMMAR_PATH);
		for (String s : str.split("\n")) {
			grammarList.add(Grammar.parseGrammar(s));
		}
		for (String s : scanner.getFlag()) {
			grammarMap.put(scanner.scan(s), new ArrayList<Grammar>());
		}
		for (String s : scanner.getPunct()) {
			grammarMap.put(scanner.scan(s), new ArrayList<Grammar>());
		}
		for (WordType s : WordType.values()) {
			Word w = new Word("", s);
			grammarMap.put(w, new ArrayList<Grammar>());
		}
		grammarMap.put(scanner.scan("id"), new ArrayList<Grammar>());
		grammarMap.put(Word.END_WORD, new ArrayList<Grammar>());
		buildGrammarMap();
	}

	public synchronized static Parser getInstance() {
		if (instance == null) {
			instance = new Parser();
		}
		return instance;
	}

	private void buildGrammarMap() {
		Map<Grammar, Set<Word>> firstSet = buildFirstSet();
		Map<Word, Set<Word>> followSet = buildFollowSet(firstSet);

		for (Grammar g : firstSet.keySet()) {
			for (Word w : firstSet.get(g)) {
				if (!w.equals(Word.NULL_WORD)) {
					grammarMap.get(w).add(g);
				}
			}
			if (g.getLast().size() == 0) {
				for (Word w : followSet.get(g.getFirst())) {
					if (grammarMap.get(w) != null) {
						grammarMap.get(w).add(g);
					}
				}
			}
		}

		/*
		 * 消除if-then-else二义性
		 */
		grammarMap.get(new Word("ELSE", WordType.FLAG)).remove(
				Grammar.parseGrammar("S'->null"));
		grammarMap.get(new Word(";", WordType.FLAG)).add(
				Grammar.parseGrammar("S'->null"));
		grammarMap.get(new Word("END", WordType.FLAG)).add(
				Grammar.parseGrammar("S'->null"));
		
		System.out.println(grammarMap.toString().replaceAll("],", "]\n"));
	}

	private Map<Grammar, Set<Word>> buildFirstSet() {
		Map<Grammar, Set<Word>> firstSet = new HashMap<Grammar, Set<Word>>();
		Map<Grammar, Word> tmpMap = new HashMap<Grammar, Word>();
		for (int i = 0; i < grammarList.size(); i++) {
			for (Grammar g : grammarList) {
				if (firstSet.get(g) == null) {
					firstSet.put(g, new HashSet<Word>());
				}
				if (firstSet.get(g).size() == 0 && tmpMap.get(g) == null) {
					if (g.getLast().size() == 0) {
						firstSet.get(g).add(Word.NULL_WORD);
					} else if (g.getLast().get(0).getWordType() != null) {
						firstSet.get(g).add(g.getLast().get(0));
					} else {
						tmpMap.put(g, g.getLast().get(0));
					}
				} else {
					Word w = tmpMap.get(g);
					if (w != null) {
						for (Grammar tmpG : firstSet.keySet()) {
							if (tmpG.getFirst().equals(w)
									&& firstSet.get(tmpG) != null) {
								firstSet.get(g).addAll(firstSet.get(tmpG));
							}
						}
					}
				}
			}
		}
		return firstSet;
	}

	private Map<Word, Set<Word>> buildFollowSet(Map<Grammar, Set<Word>> firstSet) {
		Map<Word, Set<Word>> followSet = new HashMap<Word, Set<Word>>();
		Set<Word> finishSet = new HashSet<Word>();
		for (Grammar g : grammarList) {
			buildFollowSet(g.getFirst(), followSet, firstSet, finishSet);
		}
		return followSet;
	}

	private void buildFollowSet(Word word, Map<Word, Set<Word>> followSet,
			Map<Grammar, Set<Word>> firstSet, Set<Word> finishSet) {
		if (followSet.get(word) != null) {
			return;
		}
		followSet.put(word, new HashSet<Word>());
		Set<Word> curSet = followSet.get(word);
		if (word.equals(Word.START_WORD)) {
			curSet.add(Word.END_WORD);
		}
		for (Grammar g : grammarList) {
			if (g.getLast().size() == 0) {
				continue;
			}
			if (g.getLast().get(g.getLast().size() - 1).equals(word)) {
				if (!finishSet.contains(word)) {
					buildFollowSet(g.getFirst(), followSet, firstSet, finishSet);
				}
				curSet.addAll(followSet.get(g.getFirst()));
			} else if (g.getLast().indexOf(word) >= 0) {
				boolean emptyFlag = true;
				for (int i = g.getLast().indexOf(word) + 1; i < g.getLast()
						.size(); i++) {
					if (!isEmpty(g.getLast().get(i))) {
						emptyFlag = false;
						break;
					}
				}
				if (emptyFlag) {
					if (!finishSet.contains(word)) {
						buildFollowSet(g.getFirst(), followSet, firstSet,
								finishSet);
					}
					curSet.addAll(followSet.get(g.getFirst()));
				}
				if (g.getLast().indexOf(word) + 1 < g.getLast().size()) {
					Word w = g.getLast().get(g.getLast().indexOf(word) + 1);
					if (w.getWordType() != null) {
						curSet.add(w);
					} else {
						for (Grammar g2 : firstSet.keySet()) {
							if (g2.getFirst().equals(w)
									&& firstSet.get(g2) != null) {
								curSet.addAll(firstSet.get(g2));
							}
						}
					}
				}
			}
		}
		finishSet.add(word);
	}

	private boolean isEmpty(Word word) {
		for (Grammar g : grammarList) {
			if (g.getFirst().equals(word)) {
				if (g.getLast().size() == 0) {
					return true;
				} else {
					boolean emptyFlag = true;
					for (Word w : g.getLast()) {
						if (!isEmpty(w)) {
							emptyFlag = false;
							break;
						}
					}
					if (emptyFlag) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public ParseResult parse(List<Word> words) {
		ParseResult pr = new ParseResult();
		Stack<Word> stack = new Stack<Word>();
		if (words.size() < 0) {
			return null;
		}
		stack.push(Word.START_WORD);
		List<Grammar> result = new ArrayList<Grammar>();
		int i = 0;
		while (!stack.isEmpty() || i < words.size()) {
			Word curWord = null;
			if (i >= words.size()) {
				curWord = Word.END_WORD;
			} else {
				curWord = words.get(i);
			}
			if (stack.isEmpty()) {
				pr.setErrorWord(curWord);
				pr.setFormatStr("错误 : 在\"" + curWord.getAttribute() + "\"上，行:"
						+ curWord.getLineCount() + " 字符:"
						+ curWord.getWordCount());
				break;
			}
			if (curWord.getWordType() == WordType.IDN && !curWord.isIDN()) {
				pr.setErrorWord(curWord);
				System.out.println(1);
				pr.setFormatStr("错误 : 在\"" + curWord.getAttribute()
						+ "\"上，变量名只能以字母下划线开头，行:" + curWord.getLineCount()
						+ " 字符:" + curWord.getWordCount());
				break;
			}
			Word topWord = stack.pop();
			if (topWord.getWordType() != null) {
				if (curWord.equals(topWord)
						|| (topWord.getWordType() == WordType.IDN && curWord
								.getWordType() == WordType.IDN)) {
					if ((topWord.getWordType() == WordType.IDN && curWord
							.getWordType() == WordType.IDN)
							|| curWord.getWordType().toString().startsWith(
									"INT")) {
						result.add(Grammar.parseGrammar(topWord.getAttribute()
								+ "->" + curWord.getAttribute()));
					}
					i++;
				} else {
					pr.setErrorWord(curWord);
					pr.setFormatStr("错误 : 在\"" + curWord.getAttribute()
							+ "\"上，应该为\"" + topWord.getAttribute() + "\"，行:"
							+ curWord.getLineCount() + " 字符:"
							+ curWord.getWordCount());
					break;
				}
			} else {
				Grammar grammar = null;
				for (Grammar g : grammarMap.get(curWord)) {
					if (g.getFirst().equals(topWord)) {
						grammar = g;
						break;
					}
				}
				if (grammar != null) {
					result.add(grammar);
					List<Word> last = grammar.getLast();
					for (int j = last.size() - 1; j >= 0; j--) {
						stack.push(last.get(j));
					}
				} else {
					pr.setErrorWord(curWord);
					pr.setFormatStr("错误 : 在\"" + curWord.getAttribute()
							+ "\"上，行:" + curWord.getLineCount() + " 字符:"
							+ curWord.getWordCount());
					break;
				}
			}
		}
		pr.setResult(result);
		if (pr.getErrorWord() == null) {
			pr.setFormatStr(format(result));
		}
		System.out.println(result.toString().replaceAll(",", "\n"));
		return pr;
	}

	public String format(List<Grammar> result) {
		StringBuilder sb = new StringBuilder();
		List<Grammar> tmpList = new ArrayList<Grammar>();
		tmpList.addAll(result);
		sb.append(tmpList.get(0).getFirst().getAttribute());
		Grammar g = tmpList.get(0);
		sb.append(format(tmpList, g.getFirst(), 1));
		return sb.toString();
	}

	private String format(List<Grammar> result, Word first, int count) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		if (result.size() <= 0) {
			return sb.toString();
		}
		if (result.get(0).getFirst().equals(first)) {
			Grammar g = result.remove(0);
			if (g.getLast().size() == 0) {
				sb.delete(0, 1);
				sb.append(" : 空\n");
			}
			for (Word w : g.getLast()) {
				for (int j = 0; j < count; j++) {
					sb.append("  ");
				}
				sb.append(w.getAttribute());
				sb.append(format(result, w, count + 1));
			}
		} else if (result.get(0).getFirst().getAttribute().equals("id")
				&& result.get(0).getFirst().getWordType() == null) {
			sb.delete(0, 1);
			sb.append(" : " + result.remove(0).getLast().get(0).getAttribute()
					+ "\n");
		} else if (first.getWordType() != null
				&& first.getWordType().toString().startsWith("INT")) {
			sb.delete(0, 1);
			sb.append(" : " + result.remove(0).getLast().get(0).getAttribute()
					+ "\n");
		}
		return sb.toString();
	}

	public List<Grammar> getGrammarList() {
		return grammarList;
	}

	public void setGrammarList(List<Grammar> grammarList) {
		this.grammarList = grammarList;
	}
}
