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
	private static ArrayList<Tree> tree =  new ArrayList<Tree>();  // �﷨��
	static List<Properties> treeNodeProperty;  // �﷨���ڵ�����

	static Stack<CharsID> IDcharstable = new Stack<>(); // ���ű�

	static List<String> three_addr = new ArrayList<String>();  // ����ַָ������
	static List<FourAddr> four_addr = new ArrayList<FourAddr>();  // ��Ԫʽָ������

	static String type;  // ����
	static int width;  // ������С
	static int offset;  // ƫ����
	static int temp_cnt = 0;  // �½���������
	static int nextInsAddress = 1;  // ָ��λ��

	static Stack<Integer> offTableNext = new Stack<Integer>();  //  ���ű�ƫ�ƴ�Сջ

	static int nodeSize;  // �﷨���ϵĽڵ���
	static int treeSize;  // �﷨����С
	static int initialPC = nextInsAddress;  // ��¼��һ��ָ���λ��

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
		treeNodeProperty = Arrays.asList(new Properties[nodeSize]);  // ��������

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
	 * ���ѱ����﷨��
	 * @param tree �﷨�����ڵ�
	 */
	public static void dfs(Tree tree)
	{
		int flag = 0;
		for(int i=0; i<tree.getChildren().size(); i++)
		{
			TreeNode tn = tree.getChildren().get(i);
			if (!util.endPoint(tn))  // �����ֵ�Ƿ������
			{
				flag = 1;
				// �ҵ��ڽӱ����һ�ڵ�
				Tree f = findTreeNode(tn.getId());
				dfs(f);  // �ݹ�������ӽڵ�
				findSemantic(f);  // ������Ӧ�����嶯������
			//	System.out.println( util.treeToProduction(f));
			}
		}
		if (flag == 0)
		{
			return;
		}

	}

	/**
	 * ����ű�������Ԫ��
	 * @param name Ԫ������
	 * @param type Ԫ������
	 * @param offset ƫ����
	 */
	private static void addChartoTable(String name, String type, int offset)
	{
		CharsID s = new CharsID(name,type,offset);
		IDcharstable.push(s);
	}

	/**
	 * ���ҷ��ű��鿴�����Ƿ����
	 * @param s ����
	 * @return �������ڷ��ű��е�λ��
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
	 * �½�һ������
	 * @return �½�������
	 */
	private static String newtemp()
	{
		return "t" + (++temp_cnt);
	}

	/**
	 * �����ַ
	 * @param list ��Ҫ�����ָ������
	 * @param huiAddress ����ĵ�ַ
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
	 * ������һ��ָ���ַ
	 * @return ��һ��ָ���ַ
	 */
	private static int nextInsAddress()
	{
		return three_addr.size() + nextInsAddress;
	}


	/**
	 * ����ĳ����id���Ҹ���
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
	 * ���嶯��
	 * @param tree
	 */
	// P ->begin D S end  {}
	public static void semantic_1(Tree tree)
	{
		System.out.println("��������ɹ�");
	}

	// S -> S1 M S2  {backpatch(S1.nextlist,M.huiAddress); S.nextlist=S2.nextlist;}
	public static void semantic_2(Tree tree)
	{
		int S = tree.getFather().getId();  // S
		int S1 = tree.getChildren().get(0).getId();  // S1
		int M = tree.getChildren().get(1).getId();  // M
		int S2 = tree.getChildren().get(2).getId();  // S2

		//��M�Ļ����ַ����S1��nextlist��
		backpatch(treeNodeProperty.get(S1).getNextList(), treeNodeProperty.get(M).getQuad());

		//��S2��nextlist����S
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
			//�ڷ��ű�����ӣ�ͬʱ���·��ű�offset
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

		//����E�µ�newtemp1���Ա�����value
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

		//E�����Ժ������E1��ͬ
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
		//�����µ�newtemp��ֵ
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

		//E�ڵ��ϣ�treeNodeproperty����id������type��ע
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

		//E�ڵ��ϣ�treeNodeproperty����num��ֵ
		Properties newproperty = new Properties();
		newproperty.setValue(num);
		newproperty.setType("int");
		treeNodeProperty.set(E,newproperty);
	}


	//  M -> ��  {M.huiAddress=nextInsAddress}
	public static void semantic_14(Tree tree)
	{
		int M = tree.getFather().getId();  // M
		//M�ڵ��ϣ�treeNodeproperty����һ��ָ���ַ��Ϊ�����ַ
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
