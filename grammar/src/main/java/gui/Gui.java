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
		getContentPane().setFont(new Font("����", Font.BOLD, 25));
		
		setTitle(" �� �� �� �� �� ��");    //������ʾ���ڱ���
		setSize(1070,508);    //���ô�����ʾ�ߴ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //�ô����Ƿ���Թر�
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
		
		final String[] name1 = new String[] {"����ʽ"};
		final JTable table1 = new JTable(new DefaultTableModel(new Object[][] {}, name1));
		table1.setForeground(Color.BLACK);
		table1.setFont(new Font("����", Font.BOLD, 15));
		table1.setFillsViewportHeight(true);
		table1.setBackground(Color.WHITE);
		scrollPane1.setViewportView(table1);

		JScrollPane scrollPane3 = new JScrollPane();
		scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane3.setBackground(SystemColor.menu);
		scrollPane3.setBounds(642, 25, 407, 376);
		getContentPane().add(scrollPane3);
		
		final String[] name3 = new String[] {"���󱨸�"};
		final JTable table3 = new JTable(new DefaultTableModel(new Object[][] {}, name3));
		table3.setForeground(Color.RED);
		table3.setFont(new Font("����", Font.BOLD, 15));
		table3.setFillsViewportHeight(true);
		table3.setBackground(Color.WHITE);
		scrollPane3.setViewportView(table3);

		//Ϊ���ļ����Ӽ����¼�
		JButton button1 = new JButton("\u6253\u5F00\u6587\u4EF6");
		button1.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser file_open_filechooser = new JFileChooser();
				file_open_filechooser.setCurrentDirectory(new File("."));
				file_open_filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = file_open_filechooser.showOpenDialog(scrollPane);	
				
				if (result == JFileChooser.APPROVE_OPTION) // ֤����ѡ��
				{
					file_name = file_open_filechooser.getSelectedFile().getPath();
					// ��ȡ�ļ���д��JTextArea����
					try
					{
						//filereader�ļ�I/O,��������ȡ
						FileReader reader = new FileReader(file_name);
			            BufferedReader br = new BufferedReader(reader);
			            String line;

			            while ((line = br.readLine()) != null) 
			            {
							textArea.append(line);//д���ı���
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
		button1.setFont(new Font("����", Font.BOLD, 23));
		button1.setBounds(15, 410, 300, 46);
		getContentPane().add(button1);

		//Ϊ����ı����Ӽ����¼�
		JButton button2 = new JButton("\u6E05\u7A7A\u6587\u672C");
		button2.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				textArea.setText("");
			}
		});
		button2.setFont(new Font("����", Font.BOLD, 23));
		button2.setBounds(335, 410, 300, 47);
		getContentPane().add(button2);

		//Ϊ�﷨�����İ�ť���Ӽ����¼�
		JButton button3 = new JButton("\u8BED\u6CD5\u5206\u6790");
		button3.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{		
				List<String> result2 = new ArrayList<String>(); 
				List<String> table2 = new ArrayList<String>(); 
				List<String> errors = new ArrayList<String>(); 

				//��ʼ���������﷨������������LR(1)����
				SyntaxParser se = new SyntaxParser(file_name,result2,errors);


				//result2������ʽ������,table1������ʾ
				DefaultTableModel model1 = new DefaultTableModel(gui_table(result2),name1);
				table1.setModel(model1);

				//erros��������Ϣ�����룬table3������ʾ
				DefaultTableModel model3 = new DefaultTableModel(gui_table(errors),name3);
				table3.setModel(model3);

				if (table1.getRowCount() == 0 && table3.getRowCount() == 0)
				{
					JOptionPane.showMessageDialog(null, "û�пɷ����ĳ���", "Warning", JOptionPane.DEFAULT_OPTION);
				}		
			}
		});
		button3.setFont(new Font("����", Font.BOLD, 23));
		button3.setBounds(672, 410, 300, 46);
		getContentPane().add(button3);

		setVisible(true);    //���ô����Ƿ�ɼ�
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
        new Gui();    //����һ��ʵ��������
    }
}


