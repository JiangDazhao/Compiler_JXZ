package grammar;

import lexical.Lexical;
import lexical.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;


public class SyntaxParser 
{
	
	private Lexical lex;  // �ʷ�������
	private ArrayList<Token> tokenList = new ArrayList<Token>();  // �Ӵʷ���������õ�����token
	private int length;  // tokenlist�ĳ���
	private int index;  // �﷨�������е���λ��
	public static int havemistake=0; //�﷨�����Ƿ�����ʶ

	private AnalyzeTable IDcharstable;  //������﷨������
	private Stack<Integer> stateStack;  //���ڴ洢��Ӧ��DFA״̬��
	private Stack<String> tokenStack;  //���ڴ洢����ջ
	private static StringBuffer result = new StringBuffer();  // �����Լ���

	public static ArrayList<Tree> tree = new ArrayList<Tree>();  // �����﷨����������
	private Stack<TreeNode> treeNodeID = new Stack<TreeNode>();;  // ���ڴ洢��Ӧ�����ڵ�
	private int nodecount=0;  // ��¼���нڵ��

	//private static List<String> result2 = new ArrayList();  // �����Լ���
	//private static List<String> errors = new ArrayList();  // �����Լ���

	
	/**
	 * �Ӵʷ�������ڣ���������ļ��������ַ���
	 * @param filename �ļ���
	 * @return �ļ�����
	 */
	public static String readfile(String filename)
	{
		StringBuffer result = new StringBuffer();
		File file = new File(filename);
		try
		{			
			InputStream in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte=in.read()) != -1) 
			{
				result.append(""+(char)tempbyte);
			}
			in.close();
		}
		catch(Exception event)
		{
			event.printStackTrace();
		}
		return result.toString();
	}

	/**
	 * ���������ں�������GUI����
	 * @param filename
	 * @param
	 */
	public SyntaxParser(String filename, ArrayList<Tree> tree)
	{
		this.lex = new Lexical(readfile(filename), this.tokenList);
		this.lex.analyze();  // �ʷ�����
		int last = tokenList.get(tokenList.size()-1).line + 1;
		this.tokenList.add(new Token(last,"#",-1));		
		this.length = this.tokenList.size();
		
		this.index = 0;
		this.IDcharstable = new AnalyzeTable();  // ���ɷ�����
		this.stateStack = new Stack<Integer>();  // ״̬ջ
		this.tokenStack = new Stack<String>(); //����ջ
		this.stateStack.push(0);  // ��ʼΪ0״̬
		this.tokenStack.push("#"); //��ʼΪ#״̬
		
		this.IDcharstable.dfa.writefile();  // д���ļ�"DFA_state_set.txt"
		this.IDcharstable.print();  // д���ļ�"LR_analysis_table.txt"

		this.tree= tree;
		//����LR(1)��������з���
		analyze();

		//��ӡ�﷨��
		printTree();

		//��Token�б�͹�Լ���̶�д���ĵ���
		writefile(result);
	}
	
	public Token readToken()
	{
		if(index < length)
		{
			return tokenList.get(index++);
		} 
		else 
		{
			return null;
		}
	}
	
	/**
	 * �����ֱ����Ӧ�ľ�������VT
	 * @param valueType
	 * @return
	 */
	private String getValue(Token valueType)
	{
		try
		{
			int code = valueType.code;
		if(code == 1)
			return "id";
		else if(code == 2)
			return "num";
		else if(code < 400 && code >=101)
			return valueType.value;
		else if(valueType.value.equals("#"))
			return "#";
		else
			return " ";
	}
		catch(Exception NullPointerException)
		{
			return "";	
		}
	}
	
	/**
	 * ���岿�� �﷨����
	 */
	public void analyze()
	{
		int existline=1; //���ڼ�¼�����ڵ����ڵ��к�
		while(true)
		{
			result.append("��ǰ������: ");
			System.out.print("��ǰ������: ");
			String inputBuffer=printInputandOut();
			result.append("\n");
			System.out.println();
			
			Token token = readToken();
			String value = getValue(token);
			
			if(value.equals(""))
			{
				error();
				continue;
			}
			else if(value.equals(" "))
				continue;
	
			int state = stateStack.lastElement();
			String action = IDcharstable.getActionElement(state, value);
			//����Si�Ĵ��������µ�״̬
			if(action.startsWith("s"))
			{
				int newState = Integer.parseInt(action.substring(1));
				String newInput =inputBuffer.trim().split(" ")[0];
				stateStack.push(newState);
				tokenStack.push(newInput);
				System.out.println("����:"+" "+newInput);
				result.append("����:"+" "+newInput+"\n");
				System.out.println("ACTION��:"+" "+action);
				result.append("ACTION��:"+" "+action+"\n");
				System.out.println("GOTO��:"+" "+"--");
				result.append("GOTO��:"+" "+"--"+"\n");
				System.out.println("״̬ջ:"+stateStack.toString());
				result.append("״̬ջ:"+stateStack.toString()+"\n");
				System.out.println("����ջ:"+tokenStack.toString());
				result.append("����ջ:"+tokenStack.toString()+"\n");

				//���������ֱ�ӽ���VT������Ϊ�����
				treeNodeID.push(new TreeNode(nodecount++,value,token.value,existline));
			}
			//����Ri�Ĵ������ݲ���ʽ���ȵ�ջ�������ݵ�ջ���Goto��������ջ��״̬
			else if(action.startsWith("r"))
			{
				Production derivation = ProcsandFirst.F.get(Integer.parseInt(action.substring(1)));
				int r = derivation.list.size();
				index--;//���봮���䣬�´λ���

				//�洢ĳ�����������ӽ��
				LinkedList<TreeNode> sonnode = new LinkedList<TreeNode>();
				//���ݲ���ʽ���ȣ�����ջ��״̬ջ���ε�ջ
				if(!derivation.list.get(0).equals("��"))
				{
					for(int i = 0;i < r;i++)
					{
						stateStack.pop();
						tokenStack.pop();
						sonnode.addFirst(treeNodeID.pop());//���ӽڵ����
					}
				}
				//s�ǹ�Լ����ջ��״̬
				int s = IDcharstable.getGotoElement(stateStack.lastElement(), derivation.left);
				stateStack.push(s);
				tokenStack.push(derivation.left);
				System.out.println("��Լ:"+" "+derivation);
				result.append("��Լ:"+" "+derivation+"\n");
				System.out.println("ACTION��:"+" "+action);
				result.append("ACTION��:"+" "+action+"\n");
				System.out.println("GOTO��:"+" "+s);
				result.append("GOTO��:"+" "+s+"\n");
				System.out.println("״̬ջ:"+stateStack.toString());
				result.append("״̬ջ:"+stateStack.toString()+"\n");
				System.out.println("����ջ:"+tokenStack.toString());
				result.append("����ջ:"+tokenStack.toString()+"\n");

				//��������н���Լ״̬VNҲ��Ϊ����㣬ͬʱ�����б�����������������������ڵ��б�
				treeNodeID.push(new TreeNode(nodecount++,derivation.left,"--",existline));
				ArrayList<TreeNode> sonList = new ArrayList<TreeNode>(sonnode);
				tree.add(new Tree(treeNodeID.peek(),sonList));

			}
			else if(action.equals(AnalyzeTable.acc))
			{
				System.out.println("acc��");
				result.append("acc��"+"\n");
				System.out.println("״̬ջ:"+stateStack.toString());
				result.append("״̬ջ:"+stateStack.toString()+"\n");
				System.out.println("����ջ:"+tokenStack.toString());
				result.append("����ջ:"+tokenStack.toString()+"\n");
				break;
			} 
			else 
			{
				error();
				while(action.startsWith("r"))
				{
					index = index - 1;
					Token token1 = readToken();
					tokenList.remove(token1);
					index = index - 1;

					String value1 = getValue(token1);
					stateStack.pop();

					if(value.equals(""))
					{
						error();
						continue;
					}
					if(value.equals(" "))
						continue;

					int state1 = stateStack.lastElement();
					action = IDcharstable.getActionElement(state1, value1);
					//System.out.println(action);
				}
			}
			existline=token.line;
		}
		if (havemistake==1)
		{
			System.out.println("�﷨����ʧ��NO");
			result.append("�﷨����ʧ��NO"+"\n");
		}
		else
		{
			System.out.println("�﷨�������YES");
			result.append("�﷨�������YES"+"\n");
		}

	}
	
	

	/**
	 * ����
	 */
	public void error()
	{
		havemistake=1;
		String s = "Error at Line[" + tokenList.get(index-1).line + "]:  \""+
				tokenList.get(index-1).value + "\" ���ʴ������˴���";
		result.append(s);
		//error.append(s+'\n');
		//errors.add(s);
		System.out.println(s);
	}
	
	/**
	 * ������
	 */
	private static void writefile(StringBuffer str)
	{
        String path = "LR_Analysis_Result.txt";
        try 
        {
            File file = new File(path);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(str.toString());
			bw.close();
		}
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}

	/**
	 * ������
	 */
	private static void writeerrorfile(StringBuffer str)
	{
		String path = "LR_Analysis_errors.txt";
		try
		{
			File file = new File(path);
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(str.toString());
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	
	/**
	 * ��ӡ���������Token���У�ͬʱ���ظ�ֵ
	 */
	private String printInputandOut()
	{
		String output = "";
		for(int i = index;i < tokenList.size();i++)
		{
			output += tokenList.get(i).value;
			output += " ";
		}
		System.out.println(output);
		result.append(output+"\n");
		return output;
	}

	/**
	 * ��ӡ�﷨��
	 */
	private static void printTree()
	{
		String output = "\n";
		for(int i=0; i<tree.size(); i++)
		{
			output += tree.get(i).toString();
			output += "\n";
		}
		//System.out.print(output);
		result.append(output);
	}
}
