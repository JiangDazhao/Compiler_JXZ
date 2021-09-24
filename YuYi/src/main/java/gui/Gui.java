package gui;

import javax.swing.JApplet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;

import semantic.FourAddr;
import semantic.CharsID;
import semantic.Smantic;
import semantic.util;

import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Gui extends JFrame
{
	private static final long serialVersionUID=1L;
	private static String file_name;
	
	public Gui()
	{
		getContentPane().setForeground(Color.WHITE);
		getContentPane().setFont(new Font("����", Font.BOLD, 25));
		
		setTitle(" �� �� �� �� �� ��");    //������ʾ���ڱ���
		setSize(925,508);    //���ô�����ʾ�ߴ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //�ô����Ƿ���Թر�
		getContentPane().setLayout(null);

		final JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setBounds(15, 25, 430, 365);
		scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane1);
		
		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Times New Roman", Font.BOLD, 17));
		scrollPane1.setViewportView(textArea);
		scrollPane1.setRowHeaderView(new LineNumber());
			
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane2.setToolTipText("");
		scrollPane2.setBackground(SystemColor.menu);
		scrollPane2.setBounds(460, 25, 428, 365);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane2);
		
		final String[] name1 = new String[] {"���","��Ԫʽ", "����ַ"};
        final JTable table1 = new JTable(new DefaultTableModel(new Object[][] {}, name1));
        table1.setForeground(Color.BLACK);
        table1.setFillsViewportHeight(true);
        table1.setFont(new Font("����", Font.BOLD, 15));
		table1.setBackground(new Color(255, 255, 255));
		scrollPane2.setViewportView(table1);

		
		JButton button1 = new JButton("\u6253\u5F00\u6587\u4EF6");
		button1.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser file_open_filechooser = new JFileChooser();
				file_open_filechooser.setCurrentDirectory(new File("."));
				file_open_filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = file_open_filechooser.showOpenDialog(scrollPane1);	
				
				if (result == JFileChooser.APPROVE_OPTION) // ֤����ѡ��
				{
					file_name = file_open_filechooser.getSelectedFile().getPath();
					// ��ȡ�ļ���д��JTextArea����
					try
					{
						FileReader reader = new FileReader(file_name);
			            BufferedReader br = new BufferedReader(reader);
			            String line;

			            while ((line = br.readLine()) != null) 
			            {
							textArea.append(line);
							textArea.append("\n");
			            }
			            
						/*textArea.setText("");
						InputStream in = new FileInputStream(file);
						int tempbyte;
						while ((tempbyte=in.read()) != -1) 
						{
							textArea.append(""+(char)tempbyte);
						}*/
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
		button1.setBounds(455, 400, 209, 46);
		getContentPane().add(button1);
		
		JButton button2 = new JButton("\u6E05\u7A7A\u6587\u672C");
		button2.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				textArea.setText("");
			}
		});
		button2.setFont(new Font("����", Font.BOLD, 23));
		button2.setBounds(680, 400, 206, 47);
		getContentPane().add(button2);
		
		JButton button3 = new JButton("\u8BED\u4E49\u5206\u6790");
		button3.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				Stack<CharsID> IDcharstable = new Stack<>();  // ���ű�
				List<String> three_addr = new ArrayList<String>();  // ����ַָ������
				List<FourAddr> four_addr = new ArrayList<FourAddr>();  // ����ַָ������
	    		
				Smantic se = new Smantic(file_name,IDcharstable,three_addr,four_addr);
				
				Object[][] gui_ins = util.gui_ins(three_addr,four_addr);
				
				
				DefaultTableModel model1 = new DefaultTableModel(gui_ins,name1);
				table1.setModel(model1);
				
				if (table1.getRowCount() == 0 )
				{
					JOptionPane.showMessageDialog(null, "û�пɷ����ĳ���", "Warning", JOptionPane.DEFAULT_OPTION);
				}
			
			}
		});
		button3.setFont(new Font("����", Font.BOLD, 23));
		button3.setBounds(15, 398, 430, 46);
		getContentPane().add(button3);

		setVisible(true);    //���ô����Ƿ�ɼ�
	}
	
	
    public static void main(String[] agrs)
    {    	
        new Gui();    //����һ��ʵ��������
    }
}

