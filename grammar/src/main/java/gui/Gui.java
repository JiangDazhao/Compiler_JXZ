package gui;


import grammar.SyntaxParser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Gui extends JFrame
{
	private static final long serialVersionUID=1L;
	private static String file_name;
	private JTable table;
	
	public Gui()
	{
		getContentPane().setForeground(Color.WHITE);
		getContentPane().setFont(new Font("宋体", Font.BOLD, 25));
		
		setTitle(" 语 法 分 析 程 序");    //设置显示窗口标题
		setSize(1070,508);    //设置窗口显示尺寸
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
		getContentPane().setLayout(null);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(15, 25, 307, 376);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane);
		
		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Times New Roman", Font.BOLD, 17));
		scrollPane.setViewportView(textArea);
		scrollPane.setRowHeaderView(new LineNumber());
			
		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane1.setToolTipText("");
		scrollPane1.setBackground(SystemColor.menu);
		scrollPane1.setBounds(344, 25, 283, 376);
		scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane1);
		
		final String[] name1 = new String[] {"产生式"};
		final JTable table1 = new JTable(new DefaultTableModel(new Object[][] {}, name1));
		table1.setForeground(Color.BLACK);
		table1.setFont(new Font("宋体", Font.BOLD, 15));
		table1.setFillsViewportHeight(true);
		table1.setBackground(Color.WHITE);
		scrollPane1.setViewportView(table1);

		JScrollPane scrollPane3 = new JScrollPane();
		scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane3.setBackground(SystemColor.menu);
		scrollPane3.setBounds(642, 25, 407, 376);
		getContentPane().add(scrollPane3);
		
		final String[] name3 = new String[] {"错误报告"};
		final JTable table3 = new JTable(new DefaultTableModel(new Object[][] {}, name3));
		table3.setForeground(Color.RED);
		table3.setFont(new Font("楷体", Font.BOLD, 15));
		table3.setFillsViewportHeight(true);
		table3.setBackground(Color.WHITE);
		scrollPane3.setViewportView(table3);

		//为打开文件增加监听事件
		JButton button1 = new JButton("\u6253\u5F00\u6587\u4EF6");
		button1.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser file_open_filechooser = new JFileChooser();
				file_open_filechooser.setCurrentDirectory(new File("."));
				file_open_filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = file_open_filechooser.showOpenDialog(scrollPane);	
				
				if (result == JFileChooser.APPROVE_OPTION) // 证明有选择
				{
					file_name = file_open_filechooser.getSelectedFile().getPath();
					// 读取文件，写到JTextArea里面
					try
					{
						//filereader文件I/O,缓冲区读取
						FileReader reader = new FileReader(file_name);
			            BufferedReader br = new BufferedReader(reader);
			            String line;

			            while ((line = br.readLine()) != null) 
			            {
							textArea.append(line);//写入文本区
							textArea.append("\n");
			            }
						reader.close();
					}
					catch(Exception event)
					{
						event.printStackTrace();
					}
				}
			}
		});
		button1.setFont(new Font("宋体", Font.BOLD, 23));
		button1.setBounds(15, 410, 300, 46);
		getContentPane().add(button1);

		//为清空文本增加监听事件
		JButton button2 = new JButton("\u6E05\u7A7A\u6587\u672C");
		button2.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				textArea.setText("");
			}
		});
		button2.setFont(new Font("宋体", Font.BOLD, 23));
		button2.setBounds(335, 410, 300, 47);
		getContentPane().add(button2);

		//为语法分析的按钮增加监听事件
		JButton button3 = new JButton("\u8BED\u6CD5\u5206\u6790");
		button3.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{		
				List<String> result2 = new ArrayList<String>(); 
				List<String> table2 = new ArrayList<String>(); 
				List<String> errors = new ArrayList<String>(); 

				//初始化，调用语法分析函数进行LR(1)分析
				SyntaxParser se = new SyntaxParser(file_name,result2,errors);


				//result2将产生式都存入,table1用于显示
				DefaultTableModel model1 = new DefaultTableModel(gui_table(result2),name1);
				table1.setModel(model1);

				//erros将错误信息都存入，table3用于显示
				DefaultTableModel model3 = new DefaultTableModel(gui_table(errors),name3);
				table3.setModel(model3);

				if (table1.getRowCount() == 0 && table3.getRowCount() == 0)
				{
					JOptionPane.showMessageDialog(null, "没有可分析的程序", "Warning", JOptionPane.DEFAULT_OPTION);
				}		
			}
		});
		button3.setFont(new Font("宋体", Font.BOLD, 23));
		button3.setBounds(672, 410, 300, 46);
		getContentPane().add(button3);

		setVisible(true);    //设置窗口是否可见
	}
	
	public static Object[][] gui_table(List<String> e)
	{
		int le = e.size();
		Object[][] t = new Object[le][1];
		for (int i=0; i<le; i++)
		{
			t[i][0] = e.get(i);
		}
		return t;
	}
	
    public static void main(String[] agrs)
    {    	
        new Gui();    //创建一个实例化对象
    }
}


