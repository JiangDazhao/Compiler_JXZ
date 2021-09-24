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
	
	public static String error = "error";  // �������
	public static String acc = "acc";  // ACC�����ճɹ�����
	public AllDfaStates dfa;  // ����DFA״̬
	public int stateNum;  // DFA״̬��
	
	public int actionLength;  // Action������
	public int gotoLength;  // GoTo������
	private String[] actionColName;  // Action�����������
	private String[] gotoColName;  // GoTo�����������
	private String[][] actionTable;  // Action��String��ά����
	private int[][] gotoTable;  // GoTo��int��ά����

	//��¼���е�gramDfaת��״̬
	public ArrayList<gramDfaEdge> gramDfaEdges = new ArrayList<>();


	/**
	 * ����������������
	 */
	public TableForm()
	{
		createTableHeader();//����ͷ
		this.actionLength = actionColName.length;
		this.gotoLength = gotoColName.length;
		InitiateDfa();//����DFA
		this.stateNum = dfa.size();
		this.gotoTable = new int[stateNum][gotoLength+actionLength-1];
		this.actionTable = new String[stateNum][actionLength];
		createAnalyzeTable();//����﷨��������������
		//dfa.printAllStates();
	}
	
	/**
	 * ���������������һ��LR(1)�﷨������ı�ͷ
	 */
	private void createTableHeader()
	{
		//�����ǽ���һ�������
		this.actionColName = new String[ProcsandFirst.VT.size()+1];
		this.gotoColName = new String[ProcsandFirst.VN.size()+ProcsandFirst.VT.size()];
		Iterator<String> iter1 = ProcsandFirst.VT.iterator();
		Iterator<String> iter2 = ProcsandFirst.VN.iterator();
		int i = 0;
		while(iter1.hasNext())  // �ս������+#
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

		while(iter2.hasNext())	//���ս������
		{
			String vn = iter2.next();
			gotoColName[i] = vn;
			i++;
		}
	}
	
	//private ArrayList<DfaState> stateList = new ArrayList<DfaState>();//�������еݹ鷽����һ������������

	/**
	 * ��������ݹ鷽������һ�������﷨������DFA����ʼ����ͷ����
	 */
	private void InitiateDfa()
	{	//��ʼ������LR(1)��Ŀ����dfa
		this.dfa = new AllDfaStates();

		//��state0��ʼ����
		DfaState state0 = new DfaState(0);
		state0.DFAaddNewLrd(new ProductionState(leftvgetDerivation("S'").get(0),"#",0));  // ���ȼ���S'->��S,#

		//�ع��ķ���ֻ��S'->.S,#
		for(int i = 0;i < state0.set.size();i++)//����state0��ÿһ������ʽ״̬
		{
			//�ع��ķ���ֻ��P'->P,#
			ProductionState procstate = state0.set.get(i);
			if(procstate.dotindex < procstate.d.right.size())  // �ǹ�Լ״̬����Ҫ�����������Լ�lr������
			{
				//������lr��
				Set<String> searchfirst = new HashSet<String>();

				//����ǰ������

				// �����ڡ�A->B.C, #����״̬,��ǰ��������ԭ����
				if(procstate.dotindex==procstate.d.right.size()-1)
				{
					//b = procstate.firstsearch;
					searchfirst.add(procstate.firstsearch);
				} 
				else //��ǰ����������������first
				{
					//b = procstate.d.right.get(procstate.dotindex+1);
					//searchfirst = firstofV(procstate.d.right.get(procstate.dotindex+1));
					Boolean flag = true;
					//������һ.״̬���ÿ��������first����������first
					for(int m = procstate.dotindex+1; m < procstate.d.right.size(); m++) //��ǰdot.����
					{
						Set<String> list1 = firstofV(procstate.d.right.get(m));
						searchfirst.addAll(list1);
						if(!list1.contains("��"))//������empֱ�����������յ�lrfirst�����������ǰ��first
						{
							flag = false;
							break;
						}						
					}
					//���ÿ��first������emp��������lrfirst�������е�first����ԭ����lr
					if(flag)
					{
						searchfirst.add(procstate.firstsearch);
					}
				}

				String dotA = procstate.d.right.get(procstate.dotindex);  // ��ȡ"."����ķ���
				//�����״̬
				// ��.������ķ�����VN����Ҫ����µ���������ʽ״̬����������ÿ������ǰ����������
				if(ProcsandFirst.VN.contains(dotA))
				{
					ArrayList<Production> dA = leftvgetDerivation(dotA);
					for(int j=0,length1=dA.size();j<length1;j++)
					{
						for(String f:searchfirst) //��ÿ����ǰ���������ڲ�ͬ�Ĳ���ʽ״̬��
						{
							if(!f.equals("��"))
							{
								ProductionState procstate1;
								if(dA.get(j).right.get(0).equals("��")) //�Բ���ʽ�Ҳ����ж�
								{   //����A->emp�Ĳ���ʽ
									procstate1 = new ProductionState(dA.get(j), f, 1);
								}
								else
								{
									procstate1 = new ProductionState(dA.get(j), f, 0);
								}					
								if(!state0.lrdStatecontains(procstate1))//state0��Ӳ���ʽ״̬
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

		//state0�����ɹ���ʼ����·�������ݹ齨��������״̬
		ArrayList<String> DFADirection = state0.getCharAfterDot();//�ҵ���DFA״̬�е�����·��
		for(String direction:DFADirection)
		{
			//�ҵ���ÿ��·����صĲ���ʽ״̬
			ArrayList<ProductionState> DirectionSame = state0.getLRDmatchs(direction);
			DerivateState(0,direction,DirectionSame);//��ʼ���еݹ飬�������ڷ�����DFA
		}
	}
	
	/**
	 * ͨ������һ������һ��״̬��������LR����ʽ��list��ȡ��һ��״̬��
	 * �����״̬�Ѿ����ڣ������κβ����������ݹ飬�����״̬�����ڣ�������״̬���������еݹ�
	 * @param FromState ��һ��״̬�ı��
	 * @param direction .����ķ���
	 * @param DirectionSame ��ĳһ��direction��������в���ʽ�ļ���
	 */
	private void DerivateState(int FromState,String direction,ArrayList<ProductionState> DirectionSame)
	{
		// ��ʼ��newderiState״̬
		DfaState newderiState = new DfaState(0);
		//����ʽ״̬�е�.����,�������²���ʽ״̬���뵽newderiState��
		for(int i = 0;i < DirectionSame.size();i++)
		{
			DirectionSame.get(i).dotindex++;
			newderiState.DFAaddNewLrd(DirectionSame.get(i));
		}
		//�������еĲ���ʽ״̬
		for(int i = 0;i < newderiState.set.size();i++)
		{
			if(newderiState.set.get(i).d.right.size() != newderiState.set.get(i).dotindex)  // �ǹ�Լ
			{
				String dotA = newderiState.set.get(i).d.right.get(newderiState.set.get(i).dotindex);
						
				Set<String> searchfirst = new HashSet<String>();
				if(newderiState.set.get(i).dotindex+1 == newderiState.set.get(i).d.right.size())  // �����ڡ�A->BBB.C, #����״̬
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
						if(!list1.contains("��"))
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
						if(!f.equals("��"))
						{
							ProductionState procstate;
							if(dA.get(j).right.get(0).equals("��"))
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
		//���dfa�е�״̬������״̬���ڣ���ֱ�Ӵ洢���ת����ϵ���˳��ݹ�
		for(int i = 0;i < dfa.states.size();i++)
		{
			if(dfa.states.get(i).equalTo(newderiState))
			{
				gramDfaEdge newDfaEdge= new gramDfaEdge(FromState,i,direction);
				gramDfaEdges.add(newDfaEdge);
				return;
			}
		}
		//�������ڣ��������dfastate����ź�����,����������
		newderiState.id = dfa.states.size();
		dfa.states.add(newderiState);

		//���Ӻ���״̬�йص��±�ת����ϵ
		gramDfaEdge newDfaEdge= new gramDfaEdge(FromState,newderiState.id,direction);
		gramDfaEdges.add(newDfaEdge);

		ArrayList<String> DFADirection = newderiState.getCharAfterDot();
		for(String p:DFADirection)
		{
			ArrayList<ProductionState> l = newderiState.getLRDmatchs(p);//ֱ��ͨ��·��������һ��״̬�����
			DerivateState(newderiState.id,p,l);
		}
	}
	
	/**
	 * ��ȡ����ʽ��Ϊv�Ĳ���ʽ�б�
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
	 * ���ڻ�ȡ�ķ�����v��first���б�
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
	 * ���������������﷨��������������
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

		//�����﷨�������goto����
		int gotoCount = gramDfaEdges.size();
		for(int i = 0;i < gotoCount;i++)
		{
			int start = gramDfaEdges.get(i).Dfafrom;
			int end = gramDfaEdges.get(i).Dfato;
			String direction = gramDfaEdges.get(i).DfaDirection;
			int pathIndex = gotoIndex(direction);
			this.gotoTable[start][pathIndex] = end;
		}

		//�����﷨�������action����
		int stateCount = dfa.states.size();
		for(int i = 0;i < stateCount;i++)
		{
			DfaState state = dfa.get(i);//��ȡÿ��dfa������״̬��Ϣ
			for(ProductionState procstate:state.set)
			{//DFA״̬�Ĳ���ʽ������з���
				if(procstate.dotindex == procstate.d.right.size())
				{
					if(!procstate.d.left.equals("S'"))
					{
						int productionIndex = productionIndex(procstate.d);
						String value = "r"+productionIndex;
						actionTable[i][actionIndex(procstate.firstsearch)] = value;//��Ϊ��Լ
					} 
					else 
					{
						actionTable[i][actionIndex("#")] = TableForm.acc;//��Ϊ����
					}
				} 
				else 
				{
					String next = procstate.d.right.get(procstate.dotindex);//��ȡ��������ķ�����
					if(ProcsandFirst.VT.contains(next))
					{//������һ���ս����
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
	 *������������gotoCol�����ظ�s��gotoTable���е������
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
	 *������������actionCol�����ظ�s��actionTable���е������
	 */
	private int actionIndex(String s)
	{//����action�е�����
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
	 *���ظñ��ʽ����ţ�����rj
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
	 * ��ӡ�﷨����������д��result������,��LR_Analysis_Table.txtд��
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

	//�������ʽ�����
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
        	//������д��
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
