package com.compiler.view;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.compiler.model.ParseResult;
import com.compiler.service.AddressBuilder;

public class AddressCodeFrame extends JFrame {

	private JTextArea txt_src = new JTextArea();
	private JTextArea txt_result = new JTextArea();

	private AddressBuilder ab = AddressBuilder.getInstance();

	public AddressCodeFrame(String srcStr, ParseResult pr) {
		this.setTitle("实验3：语义分析");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(200, 150, 600, 400);
		this.setLayout(new GridLayout(1, 2));
		this.add(new JScrollPane(txt_src));
		txt_src.setText(srcStr);
		txt_src.setEditable(false);
		this.add(new JScrollPane(txt_result));
		txt_result.setText(ab.build(pr.getResult()));
		txt_result.setEditable(false);
		this.setVisible(true);
	}
}
