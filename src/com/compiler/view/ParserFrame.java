package com.compiler.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.compiler.model.ParseResult;
import com.compiler.model.Word;
import com.compiler.service.Parser;

public class ParserFrame extends JFrame {
	private Parser parser = Parser.getInstance();

	private JTextPane txt_src = new JTextPane();
	private JTextArea txt_grammarList = new JTextArea();
	private JTextArea txt_result = new JTextArea();
	private JButton btn_address = new JButton("生成三地址码");
	private ParseResult pr;
	private String srcStr;

	public ParserFrame(String srcStr, List<Word> srcWordList) {
		this.srcStr = srcStr;
		this.setTitle("实验2：语法分析");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(200, 150, 600, 400);

		JPanel toolbar = new JPanel();
		btn_address.addActionListener(new Monitor());
		toolbar.add(btn_address);
		this.add(toolbar, BorderLayout.NORTH);

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 3));
		p.add(new JScrollPane(txt_src));
		p.add(new JScrollPane(txt_grammarList));
		p.add(new JScrollPane(txt_result));
		txt_src.setEditable(false);
		txt_result.setEditable(false);
		txt_grammarList.setEditable(false);
		this.add(p, BorderLayout.CENTER);

		txt_src.setText(srcStr);
		pr = parser.parse(srcWordList);
		txt_grammarList
				.setText(pr.getResult().toString().replaceAll(",", "\n"));
		txt_result.setText(pr.getFormatStr());
		if (pr.getErrorWord() != null) {
			btn_address.setEnabled(false);
			StyledDocument doc = txt_src.getStyledDocument();
			int pos;
			if (pr.getErrorWord().getWordCount() > 0) {
				pos = pr.getErrorWord().getPosition();
			} else {
				pos = srcStr.length() + 1;
				txt_src.setText(txt_src.getText() + "  ");
			}
			SimpleAttributeSet attr = new SimpleAttributeSet();
			StyleConstants.setForeground(attr, Color.RED);
			doc.setCharacterAttributes(pos, pr.getErrorWord().getAttribute()
					.length(), attr, true);
		}
		this.setVisible(true);
	}

	class Monitor implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_address) {
				new AddressCodeFrame(srcStr, pr);
			}
		}
	}
}
