package semantic;

import grammar.SyntaxParser;
import grammar.Tree;
import grammar.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Smantic
{
	private static ArrayList<Tree> tree =  new ArrayList<Tree>();  // 语法树
	static List<Properties> treeNodeProperty;  // 语法树节点属性

	static Stack<CharsID> IDcharstable = new Stack<>(); // 符号表

	static List<String> three_addr = new ArrayList<String>();  // 三地址指令序列
	static List<FourAddr> four_addr = new ArrayList<FourAddr>();  // 四元式指令序列

	static String type;  // 类型
	static int width;  // 变量大小
	static int offset;  // 偏移量
	static int temp_cnt = 0;  // 新建变量计数
	static int nextInsAddress = 1;  // 指令位置

	static Stack<Integer> offTableNext = new Stack<Integer>();  //  符号表偏移大小栈

	static int nodeSize;  // 语法树上的节点数
	static int treeSize;  // 语法树大小
	static int initialPC = nextInsAddress;  // 记录第一条指令的位置

	static {
		offTableNext.push(0);
		offset = 0;
	}

	public Smantic(String filename, Stack<CharsID> IDcharstable,
				   List<String> three_addr, List<FourAddr> four_addr)
	{
		SyntaxParser parser = new SyntaxParser(filename, tree);
		treeSize = tree.size();
		nodeSize = tree.get(treeSize-1).getFather().getId() + 1;
		treeNodeProperty = Arrays.asList(new Properties[nodeSize]);  // 符号属性

		Smantic.three_addr = three_addr;
		Smantic.four_addr = four_addr;
		Smantic.IDcharstable = IDcharstable;

		if (treeSize==0)
			return;
		dfs(tree.get(treeSize-1));
		//System.out.println(tree.get(treeSize-1).toString());
//		for(Tree t:tree)
//		{
//			System.out.println(t.toString());
//		}
		util.print_ins(three_addr, four_addr);
	}


	/**
	 * 深搜遍历语法树
	 * @param tree 语法树根节点
	 */
	public static void dfs(Tree tree)
	{
		int flag = 0;
		for(int i=0; i<tree.getChildren().size(); i++)
		{
			TreeNode tn = tree.getChildren().get(i);
			if (!util.endPoint(tn))  // 树结点值是否已算出
			{
				flag = 1;
				// 找到邻接表的下一节点
				Tree f = findTreeNode(tn.getId());
				dfs(f);  // 递归遍历孩子节点
				findSemantic(f);  // 查找相应的语义动作函数
			//	System.out.println( util.treeToProduction(f));
			}
		}
		if (flag == 0)
		{
			return;
		}

	}

	/**
	 * 向符号表中增加元素
	 * @param name 元素名字
	 * @param type 元素类型
	 * @param offset 偏移量
	 */
	private static void addChartoTable(String name, String type, int offset)
	{
		CharsID s = new CharsID(name,type,offset);
		IDcharstable.push(s);
	}

	/**
	 * 查找符号表，查看变量是否存在
	 * @param s 名字
	 * @return 该名字在符号表中的位置
	 */
	private static int isInTable(String s)
	{
		int a;
			for (int j=0; j<IDcharstable.size(); j++)
			{
				if(IDcharstable.get(j).getName().equals(s))
				{
					a = j;
					return a;
				}
			}
		a = -1;
		return a;
	}

	/**
	 * 新建一个变量
	 * @return 新建变量名
	 */
	private static String newtemp()
	{
		return "t" + (++temp_cnt);
	}

	/**
	 * 回填地址
	 * @param list 需要回填的指令序列
	 * @param huiAddress 回填的地址
	 */
	private static void backpatch(List<Integer> list, int huiAddress)
	{
		for(int i=0; i<list.size(); i++)
		{
			int x = list.get(i) - initialPC;
			three_addr.set(x, three_addr.get(x)+huiAddress);
			four_addr.get(x).setToaddr(String.valueOf(huiAddress));
		}
	}

	/**
	 * 返回下一条指令地址
	 * @return 下一条指令地址
	 */
	private static int nextInsAddress()
	{
		return three_addr.size() + nextInsAddress;
	}


	/**
	 * 根据某树的id查找该树
	 * @param id
	 * @return
	 */
	public static Tree findTreeNode(int id)
	{
		for(Tree t:tree)
		{
			if (t.getFather().getId() == id)
			{
				return t;
			}
		}
		return null;
	}


	/**
	 * 语义动作
	 * @param tree
	 */
	// P ->begin D S end  {}
	public static void semantic_1(Tree tree)
	{
		System.out.println("语义分析成功");
	}

	// S -> S1 M S2  {backpatch(S1.nextlist,M.huiAddress); S.nextlist=S2.nextlist;}
	public static void semantic_2(Tree tree)
	{
		int S = tree.getFather().getId();  // S
		int S1 = tree.getChildren().get(0).getId();  // S1
		int M = tree.getChildren().get(1).getId();  // M
		int S2 = tree.getChildren().get(2).getId();  // S2

		//将M的回填地址填入S1的nextlist中
		backpatch(treeNodeProperty.get(S1).getNextList(), treeNodeProperty.get(M).getQuad());

		//将S2的nextlist赋给S
		Properties newproperty = new Properties();
		newproperty.setNext(treeNodeProperty.get(S2).getNextList());
		treeNodeProperty.set(S,newproperty);
	}

	// D -> T id ; {addChartoTable(top(tblptr),id.name,T.type,top(offset));
	//              top(offset) = top(offset)+T.width}
	public static void semantic_4(Tree tree)
	{
		int T = tree.getChildren().get(0).getId();  // T
		String id = tree.getChildren().get(1).getValue();  // id

		int i = isInTable(id);
		if (i == -1)
		{
			//在符号表中添加，同时更新符号表offset
			addChartoTable(id, treeNodeProperty.get(T).getType(), offTableNext.peek());
			int s = offTableNext.pop();
			offTableNext.push(s + treeNodeProperty.get(T).getWidth());
			offset = offset + treeNodeProperty.get(T).getWidth();
		}
	}

	// T -> int  {T.type=int; T.width=4;}{t=T.type; width=T.width;}
	public static void semantic_5(Tree tree)
	{
		int T = tree.getFather().getId();  // T
		type = "int";
		width = 4;

		Properties newproperty = new Properties();
		newproperty.setType("int");
		newproperty.setWidth(4);
		treeNodeProperty.set(T,newproperty);
	}

	// T -> double  {T.type=double; T.width=8;}{t=T.type; width=T.width;}
	public static void semantic_6(Tree tree)
	{
		int T = tree.getFather().getId();  // T
		type = "double";
		width = 8;

		Properties newproperty = new Properties();
		newproperty.setType("double");
		newproperty.setWidth(8);
		treeNodeProperty.set(T,newproperty);
	}


	//  S -> id = E ;  {p=isInTable(id.lexeme); if p==nil then error;
	//                  gencode(p'='E.addr); S.nextlist=null}
	public static void semantic_7(Tree tree)
	{
		int S = tree.getFather().getId();  // S
		String id = tree.getChildren().get(0).getValue();  // id
		int E = tree.getChildren().get(2).getId();  // E

		String code = id + " = " + treeNodeProperty.get(E).getAddr();
		three_addr.add(code);
		four_addr.add(new FourAddr("=",treeNodeProperty.get(E).getAddr(),"-",id));
		//System.out.println(code);

		Properties newproperty = new Properties();
		newproperty.setNext(new ArrayList<Integer>());
		treeNodeProperty.set(S,newproperty);
	}


	//  E -> E1 + E2  {E.addr=newtemp(); gencode(E.addr'='E1.addr'+'E2.addr);}
	public static void semantic_8(Tree tree)
	{
		int E = tree.getFather().getId();  // E
		int E1 = tree.getChildren().get(0).getId();  // E1
		int E2 = tree.getChildren().get(2).getId();  // E2
		String newtemp1 = newtemp();

		//创建E新的newtemp1，以便填入value
		Properties newproperty = new Properties();
		newproperty.setValue(newtemp1);
		newproperty.setType(treeNodeProperty.get(E1).getType());
		treeNodeProperty.set(E,newproperty);

		String code = newtemp1 + " = " + treeNodeProperty.get(E1).getAddr() +
				"+" + treeNodeProperty.get(E2).getAddr();
		three_addr.add(code);
		four_addr.add(new FourAddr("+",treeNodeProperty.get(E1).getAddr(),
				treeNodeProperty.get(E2).getAddr(),newtemp1));
//		System.out.println(code);
//		System.out.println("ccc");
//		System.out.println(treeNodeProperty.get(E).getAddr());

	}


	//  E -> E1  {E.addr=E1.addr}
	public static void semantic_9_11(Tree tree)
	{
		int E = tree.getFather().getId();  // E
		int E1 = tree.getChildren().get(0).getId();  // E1

		//E的属性和类别与E1相同
		Properties newproperty = new Properties();
		newproperty.setValue(treeNodeProperty.get(E1).getAddr());
		newproperty.setType(treeNodeProperty.get(E1).getType());
		treeNodeProperty.set(E,newproperty);
	}


	//  E -> E1 * E2  {E.addr=newtemp(); gencode(E.addr'='E1.addr'*'E2.addr);}
	public static void semantic_10(Tree tree)
	{
		int E = tree.getFather().getId();  // E
		int E1 = tree.getChildren().get(0).getId();  // E1
		int E2 = tree.getChildren().get(2).getId();  // E2
		//计算新的newtemp的值
		String newtemp = newtemp();

		Properties newproperty = new Properties();
		newproperty.setValue(newtemp);
		treeNodeProperty.set(E,newproperty);

		String code = newtemp + " = " + treeNodeProperty.get(E1).getAddr() +
				"*" + treeNodeProperty.get(E2).getAddr();
		three_addr.add(code);
		four_addr.add(new FourAddr("*",treeNodeProperty.get(E1).getAddr(),
				treeNodeProperty.get(E2).getAddr(),newtemp));
	}

	//  E -> id  {E.addr=isInTable(id.lexeme); if E.addr==null then error;}
	public static void semantic_12(Tree tree)
	{
		int E = tree.getFather().getId();  // E
		String id = tree.getChildren().get(0).getValue();  // id

		//E节点上，treeNodeproperty进行id命名和type标注
		int i = isInTable(id);
		Properties newproperty = new Properties();
		newproperty.setValue(id);
		newproperty.setType(IDcharstable.get(i).getType());
		treeNodeProperty.set(E,newproperty);

	}


	//  E -> num  {E.addr=isInTable(num.lexeme); if E.addr==null then error}
	public static void semantic_13(Tree tree)
	{
		int E = tree.getFather().getId();  // E
		String num = tree.getChildren().get(0).getValue();  // num

		//E节点上，treeNodeproperty进行num赋值
		Properties newproperty = new Properties();
		newproperty.setValue(num);
		newproperty.setType("int");
		treeNodeProperty.set(E,newproperty);
	}


	//  M -> ε  {M.huiAddress=nextInsAddress}
	public static void semantic_14(Tree tree)
	{
		int M = tree.getFather().getId();  // M
		//M节点上，treeNodeproperty将下一条指令地址作为回填地址
		Properties newproperty = new Properties();
		newproperty.sethuiAddress(nextInsAddress());
		treeNodeProperty.set(M,newproperty);
	}

	public static void findSemantic(Tree tree)
	{
		String s = util.treeToProduction(tree);


		if (s.equals("P -> D S"))
		{
			semantic_1(tree);
		}
		else if (s.equals("S -> S M S"))
		{
			semantic_2(tree);
		}
		else if (s.equals("D -> T id ;"))
		{
			semantic_4(tree);
		}
		else if (s.equals("T -> int"))
		{
			semantic_5(tree);
		}
		else if (s.equals("T -> double"))
		{
			semantic_6(tree);
		}
		else if (s.equals("S -> id = E ;"))
		{
			semantic_7(tree);
		}
		else if (s.equals("E -> E + E1"))
		{
			semantic_8(tree);
		}
		else if (s.equals("E -> E1") || s.equals("E1 -> E2"))
		{
			semantic_9_11(tree);
		}
		else if (s.equals("E1 -> E1 * E2"))
		{
			semantic_10(tree);
		}
		else if (s.equals("E2 -> id"))
		{
			semantic_12(tree);
		}
		else if (s.equals("E2 -> num"))
		{
			semantic_13(tree);
		}
		else if (s.equals("M ->"))
		{
			semantic_14(tree);
		}
	}
}
