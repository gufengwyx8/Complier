package com.compiler.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.compiler.model.Word;
import com.compiler.service.Scanner;
import com.compiler.util.FileUtil;

public class MainFrame extends JFrame {

	private Scanner scanner = Scanner.getInstance();

	private JButton btn_load = new JButton("读文件");
	private JButton btn_parse = new JButton("语法分析");
	private JTextArea txt_src = new JTextArea();
	private JTextArea txt_result = new JTextArea();
	private JComboBox cb_flag = new JComboBox(scanner.getFlag());
	private JComboBox cb_punct = new JComboBox(scanner.getPunct());

	public MainFrame() {
		this.setTitle("实验1：词法分析");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(200, 150, 600, 400);
		JPanel toolbar = new JPanel();
		toolbar.add(btn_load);
		toolbar.add(btn_parse);
		toolbar.add(new JLabel("关键字:", JLabel.RIGHT));
		toolbar.add(cb_flag);
		toolbar.add(new JLabel("符号:", JLabel.RIGHT));
		toolbar.add(cb_punct);
		btn_load.addActionListener(new Monitor());
		btn_parse.addActionListener(new Monitor());
		this.add(toolbar, BorderLayout.NORTH);
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 2));
		p.add(new JScrollPane(txt_src));
		p.add(new JScrollPane(txt_result));
		txt_result.setEditable(false);
		txt_src.getDocument().addDocumentListener(new DocumentMonitor());
		this.add(p, BorderLayout.CENTER);
		this.setVisible(true);
	}

	public MainFrame(String path) {
		this();
		txt_src.setText(FileUtil.readFile(path));
	}

	class Monitor implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_load) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					txt_src.setText(FileUtil.readFile(chooser.getSelectedFile()
							.getAbsolutePath()));
				}
			} else if (e.getSource() == btn_parse) {
				if (txt_src.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(MainFrame.this, "请输入代码",
							"错误", JOptionPane.ERROR_MESSAGE);
				} else {
					new ParserFrame(txt_src.getText(), scanner.analysis(txt_src
							.getText()));
				}
			}
		}
	}

	class DocumentMonitor implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			txt_result.setText("");
			for (Word word : scanner.analysis(txt_src.getText())) {
				txt_result.setText(txt_result.getText() + word + "\n");
			}
		}

		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		if (args.length >= 1) {
			new MainFrame(args[0]);
		} else {
			new MainFrame();
		}
	}
}
