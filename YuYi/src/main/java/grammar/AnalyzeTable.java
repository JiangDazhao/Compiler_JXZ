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

public class AnalyzeTable 
{
	
	public static String error = "error";  // �������
	public static String acc = "acc";  // ACC�����ճɹ�����
	public DFAStateSet dfa;  // ����DFA״̬
	public int stateNum;  // DFA״̬��
	
	public int actionLength;  // Action������
	public int gotoLength;  // GoTo������
	private String[] actionColName;  // Action�����������
	private String[] gotoColName;  // GoTo�����������
	private String[][] actionTable;  // Action��String��ά����
	private int[][] gotoTable;  // GoTo��int��ά����
	
	//  ����x��DFA״̬,����S����ʱ,ת�Ƶ���y��DFA״̬,��:
	private ArrayList<Integer> gotoStart = new ArrayList<Integer>();  // �洢��x��DFA״̬
	private ArrayList<Integer> gotoEnd = new ArrayList<Integer>();  // �洢��y��DFA״̬
	private ArrayList<String> gotoPath = new ArrayList<String>();  // �洢S����


	/**
	 * ����������������
	 */
	public AnalyzeTable()
	{
		createTableHeader();//����ͷ
		this.actionLength = actionColName.length;
		this.gotoLength = gotoColName.length;
		createDFA();//����DFA
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
	
	//private ArrayList<DFAState> stateList = new ArrayList<DFAState>();//�������еݹ鷽����һ������������

	/**
	 * ��������ݹ鷽������һ�������﷨������DFA
	 * ��������ɲ�����ж���׼Ϊ:
	 */
	private void createDFA()
	{	//��ʼ������LR(1)��Ŀ����dfa
		this.dfa = new DFAStateSet();

		//��state0��ʼ����
		DFAState state0 = new DFAState(0);
		state0.DFAaddNewLrd(new ProductionState(leftvgetDerivation("P'").get(0),"#",0));  // ���ȼ���S'->��S,#
		//�ع��ķ���ֻ��P'->P,#
		for(int i = 0;i < state0.set.size();i++)//����state0��ÿһ������ʽ״̬
		{
			//�ع��ķ���ֻ��P'->P,#
			ProductionState lrd = state0.set.get(i);
			if(lrd.index < lrd.d.list.size())  // �ǹ�Լ״̬����Ҫ�����������Լ�lr������
			{
				String dotA = lrd.d.list.get(lrd.index);  // ��ȡ"."����ķ���
				//������lr��
				Set<String> lrfirst = new HashSet<String>();
				// �����ڡ�A->BBB.C, #����״̬,��ǰ�����������Ķ�
				if(lrd.index==lrd.d.list.size()-1)
				{
					//b = lrd.lr;
					lrfirst.add(lrd.lr);
				} 
				else //��closure(I),��ǰ����������������first
				{
					//b = lrd.d.list.get(lrd.index+1);
					//lrfirst = firstofV(lrd.d.list.get(lrd.index+1));
					Boolean flag = true;
					//������һ.״̬��Ĳ�����first����������first
					for(int m = lrd.index+1; m < lrd.d.list.size(); m++) //��ǰdot.����
					{
						Set<String> list1 = firstofV(lrd.d.list.get(m));
						lrfirst.addAll(list1);
						if(!list1.contains("��"))//������empֱ�����������յ�lrfirst�����������ǰ��first
						{
							flag = false;
							break;
						}						
					}
					//���ÿ��first������emp��������lrfirst�������е�first����ԭ����lr
					if(flag)
					{
						lrfirst.add(lrd.lr);
					}
				}
				// ��.������ķ�����VN����Ҫ����µ���������ʽ״̬����������ÿ������ǰ����������
				if(ProcsandFirst.VN.contains(dotA))
				{
					ArrayList<Production> dA = leftvgetDerivation(dotA);
					for(int j=0,length1=dA.size();j<length1;j++)
					{
						for(String f:lrfirst) //��ÿ����ǰ���������ڲ�ͬ�Ĳ���ʽ״̬��
						{
							if(!f.equals("��"))
							{
								ProductionState lrd1;
								if(dA.get(j).list.get(0).equals("��")) //�Բ���ʽ�Ҳ����ж�
								{   //����A->emp�Ĳ���ʽ
									lrd1 = new ProductionState(dA.get(j), f, 1);		
								}
								else
								{
									lrd1 = new ProductionState(dA.get(j), f, 0);		
								}					
								if(!state0.lrdStatecontains(lrd1))//state0��Ӳ���ʽ״̬
								{
									state0.DFAaddNewLrd(lrd1);
								}
							}
						}
					}
				}
			}
		}
		dfa.states.add(state0);

		//state0�����ɹ���ʼ����·�������ݹ齨��������״̬
		ArrayList<String> gotoPath = state0.getCharAfterDot();//�ҵ���DFA״̬�е�����·��
		for(String path:gotoPath)
		{
			//�ҵ���ÿ��·����صĲ���ʽ״̬
			ArrayList<ProductionState> list = state0.getLRDmatchs(path);
			addState(0,path,list);//��ʼ���еݹ飬�������ڷ�����DFA
		}
	}
	
	/**
	 * ͨ������һ������һ��״̬��������LR����ʽ��list��ȡ��һ��״̬��
	 * �����״̬�Ѿ����ڣ������κβ����������ݹ飬�����״̬�����ڣ�������״̬���������еݹ�
	 * @param list
	 * @param lastState ��һ��״̬�ı��
	 */
	private void addState(int lastState,String path,ArrayList<ProductionState> list)
	{
		//��ʱ����״̬temp����ʼΪ0
		DFAState temp = new DFAState(0);
		//����ʽ״̬�е�.����
		for(int i = 0;i < list.size();i++)
		{
			list.get(i).index++;
			temp.DFAaddNewLrd(list.get(i));
		}
		//�������еĲ���ʽ״̬
		for(int i = 0;i < temp.set.size();i++)
		{
			if(temp.set.get(i).d.list.size() != temp.set.get(i).index)  // �ǹ�Լ
			{
				String dotA = temp.set.get(i).d.list.get(temp.set.get(i).index);
						
				Set<String> lrfirst = new HashSet<String>();
				if(temp.set.get(i).index+1 == temp.set.get(i).d.list.size())  // �����ڡ�A->BBB.C, #����״̬
				{
					lrfirst.add(temp.set.get(i).lr);
				} 
				else 
				{
					Boolean flag = true;
					for(int m = temp.set.get(i).index+1; m < temp.set.get(i).d.list.size(); m++)
					{
						Set<String> list1 = firstofV(temp.set.get(i).d.list.get(m));
						lrfirst.addAll(list1);
						if(!list1.contains("��"))
						{
							flag = false;
							break;
						}						
					}
					if(flag)
					{
						lrfirst.add(temp.set.get(i).lr);
					}
				}
							
				ArrayList<Production> dA = leftvgetDerivation(dotA);

				//ArrayList<String> lrfirst = firstofV(B);
				for(int j = 0;j < dA.size();j++)
				{
					for(String f:lrfirst)
					{
						if(!f.equals("��"))
						{
							ProductionState lrd;
							if(dA.get(j).list.get(0).equals("��"))
							{
								lrd = new ProductionState(dA.get(j), f, 1);		
							}
							else
							{
								lrd = new ProductionState(dA.get(j), f, 0);	
							}	
							if(!temp.lrdStatecontains(lrd))
							{
								temp.DFAaddNewLrd(lrd);
							}
						}
					}
				}
			}
		}
		//���dfa�е�״̬������״̬���ڣ���ֱ�Ӵ洢���ת����ϵ���˳��ݹ�
		for(int i = 0;i < dfa.states.size();i++)
		{
			if(dfa.states.get(i).equalTo(temp))
			{
				gotoStart.add(lastState);
				gotoEnd.add(i);
				gotoPath.add(path);
				return;
			}
		}
		//�������ڣ��������dfastate����ź�����
		temp.id = dfa.states.size();
		dfa.states.add(temp);
		//ͬʱ����
		gotoStart.add(lastState);
		gotoEnd.add(temp.id);
		gotoPath.add(path);
		ArrayList<String> gotoPath = temp.getCharAfterDot();
		for(String p:gotoPath)
		{
			ArrayList<ProductionState> l = temp.getLRDmatchs(p);//ֱ��ͨ��·��������һ��״̬�����
			addState(temp.id,p,l);
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
		Iterator<Production> iter = ProcsandFirst.F.iterator();
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
	private void createAnalyzeTable()
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
				actionTable[i][j] = AnalyzeTable.error;
			}
		}

		//�����﷨�������goto����
		int gotoCount = this.gotoStart.size();
		for(int i = 0;i < gotoCount;i++)
		{
			int start = gotoStart.get(i);
			int end = gotoEnd.get(i);
			String path = gotoPath.get(i);
			int pathIndex = gotoIndex(path);
			this.gotoTable[start][pathIndex] = end;
		}

		//�����﷨�������action����
		int stateCount = dfa.states.size();
		for(int i = 0;i < stateCount;i++)
		{
			DFAState state = dfa.get(i);//��ȡdfa�ĵ���״̬
			for(ProductionState lrd:state.set)
			{//��ÿһ�����з���
				if(lrd.index == lrd.d.list.size())
				{
					if(!lrd.d.left.equals("P'"))
					{
						int productionIndex = productionIndex(lrd.d);
						String value = "r"+productionIndex;
						actionTable[i][actionIndex(lrd.lr)] = value;//��Ϊ��Լ
					} 
					else 
					{
						actionTable[i][actionIndex("#")] = AnalyzeTable.acc;//��Ϊ����
					}
				} 
				else 
				{
					String next = lrd.d.list.get(lrd.index);//��ȡ��������ķ�����
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
		int size = ProcsandFirst.F.size();
		for(int i = 0;i < size;i++)
		{
			if(ProcsandFirst.F.get(i).equals(d))
			{
				return i;
			}
		}
		return -1;
	}


	public String getActionElement(int stateIndex,String vt)
	{
		int index = actionIndex(vt);
		return actionTable[stateIndex][index];
	}
	
	public int getGotoElement(int stateIndex,String vn)
	{
		int index = gotoIndex(vn);
		return gotoTable[stateIndex][index];
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
			//if(!actionColName[i].equals("int")&&!actionColName[i].equals("record"))
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
		int index = 0;
		for(int i = 0;i < dfa.states.size();i++)
		{
			String line = form(String.valueOf(i));
			while(index < actionColName.length)
			{
				line += "\t";
				line += form(actionTable[i][index]);
				index++;
			}
			index = actionColName.length-1;
			while(index < gotoColName.length)
			{
				line += "\t";
				if(gotoTable[i][index] == -1)
				{
					line += form("error");
				} 
				else 
				{
					line += form(String.valueOf(gotoTable[i][index]));
				}
				index++;
			}
			index = 0;
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
        String path = "LR_Analysis_Table.txt";
        try 
        {
        	//������д��
            File file = new File(path);
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
