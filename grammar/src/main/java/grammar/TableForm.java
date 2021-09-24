package grammar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class TableForm
{
	
	public static String error = "error";  // 错误符号
	public static String acc = "acc";  // ACC，接收成功符号
	public AllDfaStates dfa;  // 所有DFA状态
	public int stateNum;  // DFA状态数
	
	public int actionLength;  // Action表列数
	public int gotoLength;  // GoTo表列数
	private String[] actionColName;  // Action表的列名数组
	private String[] gotoColName;  // GoTo表的列名数组
	private String[][] actionTable;  // Action表，String二维数组
	private int[][] gotoTable;  // GoTo表，int二维数组

	//记录所有的gramDfa转换状态
	public ArrayList<gramDfaEdge> gramDfaEdges = new ArrayList<>();


	/**
	 * 构造分析表的主函数
	 */
	public TableForm()
	{
		createTableHeader();//建表头
		this.actionLength = actionColName.length;
		this.gotoLength = gotoColName.length;
		InitiateDfa();//创建DFA
		this.stateNum = dfa.size();
		this.gotoTable = new int[stateNum][gotoLength+actionLength-1];
		this.actionTable = new String[stateNum][actionLength];
		createAnalyzeTable();//填充语法分析表的相关内容
		//dfa.printAllStates();
	}
	
	/**
	 * 利用这个方法建立一个LR(1)语法分析表的表头
	 */
	private void createTableHeader()
	{
		//以下是建立一个表的列
		this.actionColName = new String[ProcsandFirst.VT.size()+1];
		this.gotoColName = new String[ProcsandFirst.VN.size()+ProcsandFirst.VT.size()];
		Iterator<String> iter1 = ProcsandFirst.VT.iterator();
		Iterator<String> iter2 = ProcsandFirst.VN.iterator();
		int i = 0;
		while(iter1.hasNext())  // 终结符集合+#
		{
			String vt = iter1.next();
			if(!vt.equals(ProcsandFirst.emp))
			{
				actionColName[i] = vt;
				gotoColName[i] = vt;
				i++;
			}
		}
		actionColName[i] = "#";

		while(iter2.hasNext())	//非终结符集合
		{
			String vn = iter2.next();
			gotoColName[i] = vn;
			i++;
		}
	}
	
	//private ArrayList<DfaState> stateList = new ArrayList<DfaState>();//用于下列递归方法的一个公共的容器

	/**
	 * 利用这个递归方法建立一个用于语法分析的DFA，初始化开头部分
	 */
	private void InitiateDfa()
	{	//初始化建立LR(1)项目集族dfa
		this.dfa = new AllDfaStates();

		//从state0开始建立
		DfaState state0 = new DfaState(0);
		state0.DFAaddNewLrd(new ProductionState(leftvgetDerivation("S'").get(0),"#",0));  // 首先加入S'->·S,#

		//拓广文法后只有S'->.S,#
		for(int i = 0;i < state0.set.size();i++)//对于state0的每一条产生式状态
		{
			//拓广文法后只有P'->P,#
			ProductionState procstate = state0.set.get(i);
			if(procstate.dotindex < procstate.d.right.size())  // 非规约状态才需要接下来衍生以及lr的讨论
			{
				//后面求lr用
				Set<String> searchfirst = new HashSet<String>();

				//求向前搜索符

				// 类似于“A->B.C, #”的状态,向前搜索符用原来的
				if(procstate.dotindex==procstate.d.right.size()-1)
				{
					//b = procstate.firstsearch;
					searchfirst.add(procstate.firstsearch);
				} 
				else //向前搜索符的求法类似于first
				{
					//b = procstate.d.right.get(procstate.dotindex+1);
					//searchfirst = firstofV(procstate.d.right.get(procstate.dotindex+1));
					Boolean flag = true;
					//对于下一.状态后的每个符号求first，类似于求first
					for(int m = procstate.dotindex+1; m < procstate.d.right.size(); m++) //当前dot.往后
					{
						Set<String> list1 = firstofV(procstate.d.right.get(m));
						searchfirst.addAll(list1);
						if(!list1.contains("ε"))//不包含emp直接跳出，最终的lrfirst包含这个及以前的first
						{
							flag = false;
							break;
						}						
					}
					//如果每个first都包含emp，则最终lrfirst除了所有的first还有原来的lr
					if(flag)
					{
						searchfirst.add(procstate.firstsearch);
					}
				}

				String dotA = procstate.d.right.get(procstate.dotindex);  // 获取"."后面的符号
				//添加新状态
				// “.”后面的符号是VN，需要添加新的衍生产生式状态（包括更新每个的向前搜索符集）
				if(ProcsandFirst.VN.contains(dotA))
				{
					ArrayList<Production> dA = leftvgetDerivation(dotA);
					for(int j=0,length1=dA.size();j<length1;j++)
					{
						for(String f:searchfirst) //将每个向前搜索符放在不同的产生式状态中
						{
							if(!f.equals("ε"))
							{
								ProductionState procstate1;
								if(dA.get(j).right.get(0).equals("ε")) //对产生式右部做判断
								{   //形如A->emp的产生式
									procstate1 = new ProductionState(dA.get(j), f, 1);
								}
								else
								{
									procstate1 = new ProductionState(dA.get(j), f, 0);
								}					
								if(!state0.lrdStatecontains(procstate1))//state0添加产生式状态
								{
									state0.DFAaddNewLrd(procstate1);
								}
							}
						}
					}
				}
			}
		}
		dfa.states.add(state0);

		//state0建立成功后开始根据路径名，递归建立其他的状态
		ArrayList<String> DFADirection = state0.getCharAfterDot();//找到该DFA状态中的所有路径
		for(String direction:DFADirection)
		{
			//找到与每类路径相关的产生式状态
			ArrayList<ProductionState> DirectionSame = state0.getLRDmatchs(direction);
			DerivateState(0,direction,DirectionSame);//开始进行递归，建立用于分析的DFA
		}
	}
	
	/**
	 * 通过输入一个从上一个状态传下来的LR产生式的list获取下一个状态，
	 * 如果该状态已经存在，则不作任何操作，跳出递归，如果该状态不存在，则加入该状态，继续进行递归
	 * @param FromState 上一个状态的编号
	 * @param direction .后面的符号
	 * @param DirectionSame 由某一个direction引起的所有产生式的集合
	 */
	private void DerivateState(int FromState,String direction,ArrayList<ProductionState> DirectionSame)
	{
		// 初始化newderiState状态
		DfaState newderiState = new DfaState(0);
		//产生式状态中的.后移,将所有新产生式状态加入到newderiState中
		for(int i = 0;i < DirectionSame.size();i++)
		{
			DirectionSame.get(i).dotindex++;
			newderiState.DFAaddNewLrd(DirectionSame.get(i));
		}
		//对于现有的产生式状态
		for(int i = 0;i < newderiState.set.size();i++)
		{
			if(newderiState.set.get(i).d.right.size() != newderiState.set.get(i).dotindex)  // 非规约
			{
				String dotA = newderiState.set.get(i).d.right.get(newderiState.set.get(i).dotindex);
						
				Set<String> searchfirst = new HashSet<String>();
				if(newderiState.set.get(i).dotindex+1 == newderiState.set.get(i).d.right.size())  // 类似于“A->BBB.C, #”的状态
				{
					searchfirst.add(newderiState.set.get(i).firstsearch);
				} 
				else 
				{
					Boolean flag = true;
					for(int m = newderiState.set.get(i).dotindex+1; m < newderiState.set.get(i).d.right.size(); m++)
					{
						Set<String> list1 = firstofV(newderiState.set.get(i).d.right.get(m));
						searchfirst.addAll(list1);
						if(!list1.contains("ε"))
						{
							flag = false;
							break;
						}						
					}
					if(flag)
					{
						searchfirst.add(newderiState.set.get(i).firstsearch);
					}
				}
							
				ArrayList<Production> dA = leftvgetDerivation(dotA);

				//ArrayList<String> searchfirst = firstofV(B);
				for(int j = 0;j < dA.size();j++)
				{
					for(String f:searchfirst)
					{
						if(!f.equals("ε"))
						{
							ProductionState procstate;
							if(dA.get(j).right.get(0).equals("ε"))
							{
								procstate = new ProductionState(dA.get(j), f, 1);
							}
							else
							{
								procstate = new ProductionState(dA.get(j), f, 0);
							}	
							if(!newderiState.lrdStatecontains(procstate))
							{
								newderiState.DFAaddNewLrd(procstate);
							}
						}
					}
				}
			}
		}
		//检查dfa中的状态，若该状态存在，则直接存储相关转换关系，退出递归
		for(int i = 0;i < dfa.states.size();i++)
		{
			if(dfa.states.get(i).equalTo(newderiState))
			{
				gramDfaEdge newDfaEdge= new gramDfaEdge(FromState,i,direction);
				gramDfaEdges.add(newDfaEdge);
				return;
			}
		}
		//若不存在，则添加新dfastate的序号和内容,并继续衍生
		newderiState.id = dfa.states.size();
		dfa.states.add(newderiState);

		//增加和新状态有关的新边转换关系
		gramDfaEdge newDfaEdge= new gramDfaEdge(FromState,newderiState.id,direction);
		gramDfaEdges.add(newDfaEdge);

		ArrayList<String> DFADirection = newderiState.getCharAfterDot();
		for(String p:DFADirection)
		{
			ArrayList<ProductionState> l = newderiState.getLRDmatchs(p);//直接通过路径传到下一个状态的情况
			DerivateState(newderiState.id,p,l);
		}
	}
	
	/**
	 * 获取产生式左部为v的产生式列表
	 * @param v
	 * @return
	 */
	public ArrayList<Production> leftvgetDerivation(String v)
	{
		ArrayList<Production> result = new ArrayList<Production>();
		Iterator<Production> iter = ProcsandFirst.GramProcF.iterator();
		while(iter.hasNext())
		{
			Production d = iter.next();
			if(d.left.equals(v))
			{
				result.add(d);
			}
		}
		return result;
	}
	
	/**
	 * 用于获取文法符号v的first集列表
	 * @param v
	 * @return
	 */
	private Set<String> firstofV(String v)
	{
		Set<String> result = new HashSet<String>();
		if(v.equals("#"))
		{
			result.add("#");
		} 
		else 
		{
			Iterator<String> iter = ProcsandFirst.firstMap.get(v).iterator();
			while(iter.hasNext())
			{
				result.add(iter.next());
			}
		}
		return result;
	}
	
	/**
	 * 利用这个方法填充语法分析表的相关内容
	 */
	public void createAnalyzeTable()
	{
		for(int i = 0;i < gotoTable.length; i++)
		{
			for(int j = 0;j < gotoTable[0].length;j++)
			{
				gotoTable[i][j] = -1;
			}
		}

		for(int i = 0;i < actionTable.length;i++)
		{
			for(int j = 0;j < actionTable[0].length;j++)
			{
				actionTable[i][j] = TableForm.error;
			}
		}

		//完善语法分析表的goto部分
		int gotoCount = gramDfaEdges.size();
		for(int i = 0;i < gotoCount;i++)
		{
			int start = gramDfaEdges.get(i).Dfafrom;
			int end = gramDfaEdges.get(i).Dfato;
			String direction = gramDfaEdges.get(i).DfaDirection;
			int pathIndex = gotoIndex(direction);
			this.gotoTable[start][pathIndex] = end;
		}

		//完善语法分析表的action部分
		int stateCount = dfa.states.size();
		for(int i = 0;i < stateCount;i++)
		{
			DfaState state = dfa.get(i);//获取每个dfa的整个状态信息
			for(ProductionState procstate:state.set)
			{//DFA状态的产生式逐个进行分析
				if(procstate.dotindex == procstate.d.right.size())
				{
					if(!procstate.d.left.equals("S'"))
					{
						int productionIndex = productionIndex(procstate.d);
						String value = "r"+productionIndex;
						actionTable[i][actionIndex(procstate.firstsearch)] = value;//设为规约
					} 
					else 
					{
						actionTable[i][actionIndex("#")] = TableForm.acc;//设为接受
					}
				} 
				else 
				{
					String next = procstate.d.right.get(procstate.dotindex);//获取·后面的文法符号
					if(ProcsandFirst.VT.contains(next))
					{//必须是一个终结符号
						if(gotoTable[i][gotoIndex(next)] != -1)
						{
							actionTable[i][actionIndex(next)] = "s"+gotoTable[i][gotoIndex(next)];
						}
					}
				}
			}
		}
	}

	/**
	 *根据列名数组gotoCol，返回该s在gotoTable表中的列序号
	 */
	private int gotoIndex(String s)
	{
		for(int i = 0;i < gotoLength;i++)
		{
			if(gotoColName[i].equals(s))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 *根据列名数组actionCol，返回该s在actionTable表中的列序号
	 */
	private int actionIndex(String s)
	{//返回action中的列数
		for(int i = 0;i < actionLength;i++)
		{
			if(actionColName[i].equals(s))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 *返回该表达式的序号，用于rj
	 */
	private int productionIndex(Production d)
	{
		int size = ProcsandFirst.GramProcF.size();
		for(int i = 0;i < size;i++)
		{
			if(ProcsandFirst.GramProcF.get(i).equals(d))
			{
				return i;
			}
		}
		return -1;
	}


	public String getActionElement(int stateIndex,String vt)
	{
		int dotindex = actionIndex(vt);
		return actionTable[stateIndex][dotindex];
	}
	
	public int getGotoElement(int stateIndex,String vn)
	{
		int dotindex = gotoIndex(vn);
		return gotoTable[stateIndex][dotindex];
	}
	
	/**
	 * 打印语法分析表函数，写入result缓冲区,在LR_Analysis_Table.txt写出
	 */
	public String print()
	{
		StringBuffer result = new StringBuffer();
		String colLine = form("");
		for(int i = 0;i < actionColName.length;i++)
		{
			//if(!actionColName[i].equals("integer")&&!actionColName[i].equals("record"))
			colLine += "\t";
			colLine += form(actionColName[i]);
		}
		for(int j = actionColName.length-1;j < gotoColName.length;j++)
		{
			colLine += "\t";
			colLine += form(gotoColName[j]);
		}
		result.append(colLine + "\n");
		//System.out.println(colLine);
		int tableindex = 0;
		for(int i = 0;i < dfa.states.size();i++)
		{
			String line = form(String.valueOf(i));
			while(tableindex < actionColName.length)
			{
				line += "\t";
				line += form(actionTable[i][tableindex]);
				tableindex++;
			}
			tableindex = actionColName.length-1;
			while(tableindex < gotoColName.length)
			{
				line += "\t";
				if(gotoTable[i][tableindex] == -1)
				{
					line += form("error");
				} 
				else 
				{
					line += form(String.valueOf(gotoTable[i][tableindex]));
				}
				tableindex++;
			}
			tableindex = 0;
			line += "\t";
			result.append(line + "\n");
			writefile(result);
			//System.out.println(line);
		}
		return result.toString();
	}

	//分析表格式化输出
	public String form(String str)
	{
		for(int i = 0; i < 9-str.length(); i++)
		{
			str += " ";
		}
		return str;
	}
	
	
	public void writefile(StringBuffer str)
	{		
        String direction = "LR_Analysis_Table.txt";
        try 
        {
        	//缓冲区写入
            File file = new File(direction);
			if(!file.exists()) {
				file.createNewFile();
			}
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
	
	public int getStateNum()
	{
		return dfa.states.size();
	}

}
