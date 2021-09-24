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
	
	private Lexical lex;  // 词法分析器
	private ArrayList<Token> tokenList = new ArrayList<Token>();  // 从词法分析器获得的所有token
	private int length;  // tokenlist的长度
	private int analyzeindex;  // 语法分析进行到的位置
	public static int havemistake=0; //语法分析是否出错标识

	private TableForm table;  //构造的语法分析表
	private Stack<Integer> stateStack;  //用于存储相应的DFA状态号
	private Stack<String> tokenStack;  //用于存储符号栈

	private static StringBuffer result = new StringBuffer();  // 保存规约结果
	private static StringBuffer error= new StringBuffer();  // 保存错误文件

	private static List<String> result2 = new ArrayList();  // 保存规约产生式
	private static List<String> errors = new ArrayList();  // 保存规约结果

	
	/**
	 * 从词法分析入口，读入测试文件，返回字符串
	 * @param filename 文件名
	 * @return 文件内容
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
	 * 语法分析入口函数，与GUI链接
	 * @param filename
	 * @param result2 规约结果
	 * @param errors 错误结果
	 */
	public SyntaxParser(String filename, List<String> result2, List<String> errors)
	{
		this.lex = new Lexical(readfile(filename), this.tokenList);
		this.lex.GramAnalyze();  // 词法分析
		int last = tokenList.get(tokenList.size()-1).line + 1;
		this.tokenList.add(new Token(last,"#",-1));		
		this.length = this.tokenList.size();
		
		this.analyzeindex = 0;
		this.table = new TableForm();  // 生成分析表
		this.stateStack = new Stack<Integer>();  // 状态栈
		this.tokenStack = new Stack<String>(); //符号栈
		this.stateStack.push(0);  // 初始为0状态
		this.tokenStack.push("#"); //初始为#状态
		
		this.table.dfa.writefile();  // 写入文件"DFA_state_set.txt"		
		this.table.print();  // 写入文件"LR_analysis_table.txt"
		
		SyntaxParser.result2 = result2;
		SyntaxParser.errors = errors;

		//根据LR(1)分析表进行分析
		GramAnalyze();

		//将Token列表和规约过程都写入文档中
		writefile(result);
		//将错误提示文件写入文档中
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
	 * 主体部分 语法分析
	 */
	public void GramAnalyze()
	{
		while(true)
		{
			result.append("符号串: ");
			System.out.print("符号串: ");
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
			//碰到Si的处理，移入新的状态
			if(action.startsWith("s"))
			{
				int newState = Integer.parseInt(action.substring(1));
				String newInput =inputBuffer.trim().split(" ")[0];
				stateStack.push(newState);
				tokenStack.push(newInput);
				System.out.println("移入:"+" "+newInput);
				result.append("移入:"+" "+newInput+"\n");
				System.out.println("ACTION表:"+" "+action);
				result.append("ACTION表:"+" "+action+"\n");
				System.out.println("GOTO表:"+" "+"--");
				result.append("GOTO表:"+" "+"--"+"\n");
				System.out.println("状态栈:"+stateStack.toString());
				result.append("状态栈:"+stateStack.toString()+"\n");
				System.out.println("符号栈:"+tokenStack.toString());
				result.append("符号栈:"+tokenStack.toString()+"\n");
			}
			//碰到Ri的处理，根据产生式长度弹栈，并根据弹栈后的Goto表格决定入栈的状态
			else if(action.startsWith("r"))
			{
				Production derivation = ProcsandFirst.GramProcF.get(Integer.parseInt(action.substring(1)));
				result2.add(derivation.toString());
				int r = derivation.right.size();
				analyzeindex--;//输入串不变，下次还是
				//根据产生式长度，符号栈和状态栈依次弹栈
				if(!derivation.right.get(0).equals("ε"))
				{
					for(int i = 0;i < r;i++)
					{
						stateStack.pop();
						tokenStack.pop();
					}
				}
				//s是规约后入栈的状态
				int s = table.getGotoElement(stateStack.lastElement(), derivation.left);
				stateStack.push(s);
				tokenStack.push(derivation.left);
				System.out.println("规约:"+" "+derivation);
				result.append("规约:"+" "+derivation+"\n");
				System.out.println("ACTION表:"+" "+action);
				result.append("ACTION表:"+" "+action+"\n");
				System.out.println("GOTO表:"+" "+s);
				result.append("GOTO表:"+" "+s+"\n");
				System.out.println("状态栈:"+stateStack.toString());
				result.append("状态栈:"+stateStack.toString()+"\n");
				System.out.println("符号栈:"+tokenStack.toString());
				result.append("符号栈:"+tokenStack.toString()+"\n");
			} 
			else if(action.equals(TableForm.acc))
			{
				System.out.println("acc：");
				result.append("acc："+"\n");
				System.out.println("状态栈:"+stateStack.toString());
				result.append("状态栈:"+stateStack.toString()+"\n");
				System.out.println("符号栈:"+tokenStack.toString());
				result.append("符号栈:"+tokenStack.toString()+"\n");
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
			System.out.println("语法分析失败NO");
			result.append("语法分析失败NO"+"\n");
		}
		else
		{
			System.out.println("语法分析完成YES");
			result.append("语法分析完成YES"+"\n");
		}

	}
	
	

	/**
	 * 出错
	 */
	public void error()
	{
		havemistake=1;
		String s = "Error at Line[" + tokenList.get(analyzeindex-1).line + "]:  \""+
				tokenList.get(analyzeindex-1).value + "\" 单词处发现了错误。";
		result.append(s);
		error.append(s+'\n');
		errors.add(s);
		System.out.println(s);
	}
	
	/**
	 * 输出结果
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
	 * 输出结果
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
	 * 打印即将输入的Token序列，同时返回该值
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
	 * 返回种别码对应的具体类型VT
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
