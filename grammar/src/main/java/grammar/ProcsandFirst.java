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
	//静态公有变量可以直接用类名访问
	public static String emp = "ε";  // 空串
	public static TreeSet<String> VN = new TreeSet<String>();  // 非终结符集
	public static TreeSet<String> VT = new TreeSet<String>();  // 终结符集
	public static ArrayList<Production> GramProcF = new ArrayList<Production>();  // 产生式集
	//  每个符号的first集
	public static HashMap<String,TreeSet<String> > firstMap = new HashMap<String,TreeSet<String>>();

	static
	{
		// 从文件中读取文法，添加相应的产生式
		try 
		{
			read("grammar.txt");
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

		// 添加非终结符
		VN.add("S'");  VN.add("S");   VN.add("D");   VN.add("A");   VN.add("T");
		VN.add("E");   VN.add("E1");  VN.add("E2");
		VN.add("A1");  VN.add("A2");   VN.add("A3");  VN.add("A0");
		VN.add("JG");  VN.add("B");

		// 添加终结符
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
	 * 从文件中读取文法并且存储到CFG类的静态容器中，编号就是容器的index
	 * 根据 | 将合成的文法右部分成多个部分
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
				GramProcF.add(derivation);//存储到静态的容器中
			}			
		}
		scanner.close();
	}
	
	/**
	 * 计算所有符号的first集合
	 */
	private static void GenerateFirst()
	{
		//将所有的终结符的first都设为本身
		Iterator<String> iterVT = VT.iterator();
		while(iterVT.hasNext())
		{
			String vt = iterVT.next();
			firstMap.put(vt,new TreeSet<String>());
			firstMap.get(vt).add(vt);
		}
		//计算所有非终结符的first集合
		Iterator<String> iterVN = VN.iterator();
		while(iterVN.hasNext())
		{
			String vn = iterVN.next();
			firstMap.put(vn, new TreeSet<String>());//因为后续操作没有交叉涉及firstMap，所以不必分成两个while循环，合成一趟即可
			firstMap.get(vn).addAll(IterFirstFind(vn));
		}
	}
	
	/**
	 * 用于查找VN的First集
	 * @param vn
	 * @return 返回first集
	 */
	private static TreeSet<String> IterFirstFind(String vn)
	{
		TreeSet<String> firstset = new TreeSet<String>();
		int lastsize = 0;
		int rearsize = 0;
		while(true)
		{
			//直到没有新的VT或emp加入到该vn的First集合中停止
			lastsize = firstset.size();
			for(Production d:GramProcF)
			{
				if(d.left.equals(vn))//first集看产生式左部
				{
					if(VT.contains(d.right.get(0)))  // 终结符，直接加入
					{
						firstset.add(d.right.get(0));
					} 
					else if(d.right.get(0).equals(emp))  // 空符号，直接加入
					{
						firstset.add(emp);
					}
					else if(VN.contains(d.right.get(0)))  // 非终结符，递归
					{
						if(!vn.equals(d.right.get(0)))  // 去除类似于E->E*E这样的左递归
						{
							TreeSet<String> set2 = IterFirstFind(d.right.get(0));
							firstset.addAll(set2);
							// 如果产生式右侧的VN能推出emp，接下来继续findfirst
							if(set2.contains(emp))
							{
								for(int j=1; j<d.right.size(); j++)
								{
									TreeSet<String> set3 = IterFirstFind(d.right.get(j));
									firstset.addAll(set3);
									// 再次递归直到不包含emp
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
