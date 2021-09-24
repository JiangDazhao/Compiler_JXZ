package grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

public class ProcsandFirst
{
	//��̬���б�������ֱ������������
	public static String emp = "��";  // �մ�
	public static TreeSet<String> VN = new TreeSet<String>();  // ���ս����
	public static TreeSet<String> VT = new TreeSet<String>();  // �ս����
	public static ArrayList<Production> GramProcF = new ArrayList<Production>();  // ����ʽ��
	//  ÿ�����ŵ�first��
	public static HashMap<String,TreeSet<String> > firstMap = new HashMap<String,TreeSet<String>>();

	static
	{
		// ���ļ��ж�ȡ�ķ��������Ӧ�Ĳ���ʽ
		try 
		{
			read("grammar.txt");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

		// ��ӷ��ս��
		VN.add("S'");  VN.add("S");   VN.add("D");   VN.add("A");   VN.add("T");
		VN.add("E");   VN.add("E1");  VN.add("E2");
		VN.add("A1");  VN.add("A2");   VN.add("A3");  VN.add("A0");
		VN.add("JG");  VN.add("B");

		// ����ս��
		VT.add("id");    VT.add(";");    VT.add("int");
		VT.add("double");  VT.add("[");     VT.add("]");     VT.add("num");    VT.add("=");
		VT.add("+");     VT.add("*");       VT.add("(");      VT.add(")");
		VT.add("if");    VT.add("then");    VT.add("while");  VT.add("do");
		VT.add("true");   VT.add("false");  VT.add("{");   VT.add("}");
		VT.add("<");      VT.add("<=");   VT.add("==");    VT.add("!=");     VT.add(">");
		VT.add(">=");     VT.add("call");


		GenerateFirst();
	}

	
	/**
	 * ���ļ��ж�ȡ�ķ����Ҵ洢��CFG��ľ�̬�����У���ž���������index
	 * ���� | ���ϳɵ��ķ��Ҳ��ֳɶ������
	 * @param filename
	 * @throws FileNotFoundException
	 */
	private static void read(String filename) throws FileNotFoundException
	{
		File file = new File(filename);
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext())
		{
			String line = scanner.nextLine().trim();
			if(!line.equals(""))
			{
				String[] div = line.split("->");
				String right = div[1];
				Production derivation = new Production(div[0].trim()+"->"+right.trim());
				GramProcF.add(derivation);//�洢����̬��������
			}			
		}
		scanner.close();
	}
	
	/**
	 * �������з��ŵ�first����
	 */
	private static void GenerateFirst()
	{
		//�����е��ս����first����Ϊ����
		Iterator<String> iterVT = VT.iterator();
		while(iterVT.hasNext())
		{
			String vt = iterVT.next();
			firstMap.put(vt,new TreeSet<String>());
			firstMap.get(vt).add(vt);
		}
		//�������з��ս����first����
		Iterator<String> iterVN = VN.iterator();
		while(iterVN.hasNext())
		{
			String vn = iterVN.next();
			firstMap.put(vn, new TreeSet<String>());//��Ϊ��������û�н����漰firstMap�����Բ��طֳ�����whileѭ�����ϳ�һ�˼���
			firstMap.get(vn).addAll(IterFirstFind(vn));
		}
	}
	
	/**
	 * ���ڲ���VN��First��
	 * @param vn
	 * @return ����first��
	 */
	private static TreeSet<String> IterFirstFind(String vn)
	{
		TreeSet<String> firstset = new TreeSet<String>();
		int lastsize = 0;
		int rearsize = 0;
		while(true)
		{
			//ֱ��û���µ�VT��emp���뵽��vn��First������ֹͣ
			lastsize = firstset.size();
			for(Production d:GramProcF)
			{
				if(d.left.equals(vn))//first��������ʽ��
				{
					if(VT.contains(d.right.get(0)))  // �ս����ֱ�Ӽ���
					{
						firstset.add(d.right.get(0));
					} 
					else if(d.right.get(0).equals(emp))  // �շ��ţ�ֱ�Ӽ���
					{
						firstset.add(emp);
					}
					else if(VN.contains(d.right.get(0)))  // ���ս�����ݹ�
					{
						if(!vn.equals(d.right.get(0)))  // ȥ��������E->E*E��������ݹ�
						{
							TreeSet<String> set2 = IterFirstFind(d.right.get(0));
							firstset.addAll(set2);
							// �������ʽ�Ҳ��VN���Ƴ�emp������������findfirst
							if(set2.contains(emp))
							{
								for(int j=1; j<d.right.size(); j++)
								{
									TreeSet<String> set3 = IterFirstFind(d.right.get(j));
									firstset.addAll(set3);
									// �ٴεݹ�ֱ��������emp
									if(!set3.contains(emp))
									{
										break;
									}
								}
							}							
						}
					}
				}
			}
			rearsize = firstset.size();
			if(lastsize == rearsize)
				break;
		}
		return firstset;
	}


}
