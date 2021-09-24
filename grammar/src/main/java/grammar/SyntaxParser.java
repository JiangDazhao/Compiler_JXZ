package grammar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lexical.Lexical;
import lexical.Token;


public class SyntaxParser 
{
	
	private Lexical lex;  // �ʷ�������
	private ArrayList<Token> tokenList = new ArrayList<Token>();  // �Ӵʷ���������õ�����token
	private int length;  // tokenlist�ĳ���
	private int analyzeindex;  // �﷨�������е���λ��
	public static int havemistake=0; //�﷨�����Ƿ�����ʶ

	private TableForm table;  //������﷨������
	private Stack<Integer> stateStack;  //���ڴ洢��Ӧ��DFA״̬��
	private Stack<String> tokenStack;  //���ڴ洢����ջ

	private static StringBuffer result = new StringBuffer();  // �����Լ���
	private static StringBuffer error= new StringBuffer();  // ��������ļ�

	private static List<String> result2 = new ArrayList();  // �����Լ����ʽ
	private static List<String> errors = new ArrayList();  // �����Լ���

	
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
	 * �﷨������ں�������GUI����
	 * @param filename
	 * @param result2 ��Լ���
	 * @param errors ������
	 */
	public SyntaxParser(String filename, List<String> result2, List<String> errors)
	{
		this.lex = new Lexical(readfile(filename), this.tokenList);
		this.lex.GramAnalyze();  // �ʷ�����
		int last = tokenList.get(tokenList.size()-1).line + 1;
		this.tokenList.add(new Token(last,"#",-1));		
		this.length = this.tokenList.size();
		
		this.analyzeindex = 0;
		this.table = new TableForm();  // ���ɷ�����
		this.stateStack = new Stack<Integer>();  // ״̬ջ
		this.tokenStack = new Stack<String>(); //����ջ
		this.stateStack.push(0);  // ��ʼΪ0״̬
		this.tokenStack.push("#"); //��ʼΪ#״̬
		
		this.table.dfa.writefile();  // д���ļ�"DFA_state_set.txt"		
		this.table.print();  // д���ļ�"LR_analysis_table.txt"
		
		SyntaxParser.result2 = result2;
		SyntaxParser.errors = errors;

		//����LR(1)��������з���
		GramAnalyze();

		//��Token�б�͹�Լ���̶�д���ĵ���
		writefile(result);
		//��������ʾ�ļ�д���ĵ���
		writeerrorfile(error);
	}
	
	public Token readToken()
	{
		if(analyzeindex < length)
		{
			return tokenList.get(analyzeindex++);
		} 
		else 
		{
			return null;
		}
	}


	/**
	 * ���岿�� �﷨����
	 */
	public void GramAnalyze()
	{
		while(true)
		{
			result.append("���Ŵ�: ");
			System.out.print("���Ŵ�: ");
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
			String action = table.getActionElement(state, value);
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
			}
			//����Ri�Ĵ������ݲ���ʽ���ȵ�ջ�������ݵ�ջ���Goto��������ջ��״̬
			else if(action.startsWith("r"))
			{
				Production derivation = ProcsandFirst.GramProcF.get(Integer.parseInt(action.substring(1)));
				result2.add(derivation.toString());
				int r = derivation.right.size();
				analyzeindex--;//���봮���䣬�´λ���
				//���ݲ���ʽ���ȣ�����ջ��״̬ջ���ε�ջ
				if(!derivation.right.get(0).equals("��"))
				{
					for(int i = 0;i < r;i++)
					{
						stateStack.pop();
						tokenStack.pop();
					}
				}
				//s�ǹ�Լ����ջ��״̬
				int s = table.getGotoElement(stateStack.lastElement(), derivation.left);
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
			} 
			else if(action.equals(TableForm.acc))
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
				break;
			}	
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
		String s = "Error at Line[" + tokenList.get(analyzeindex-1).line + "]:  \""+
				tokenList.get(analyzeindex-1).value + "\" ���ʴ������˴���";
		result.append(s);
		error.append(s+'\n');
		errors.add(s);
		System.out.println(s);
	}
	
	/**
	 * ������
	 */
	private static void writefile(StringBuffer str)
	{
        String direction = "LR_Analysis_Result.txt";
        try 
        {
            File file = new File(direction);
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
		String direction = "LR_Analysis_errors.txt";
		try
		{
			File file = new File(direction);
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
		for(int i = analyzeindex;i < tokenList.size();i++)
		{
			output += tokenList.get(i).value;
			output += " ";
		}
		System.out.println(output);
		result.append(output+"\n");
		return output;
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

}
