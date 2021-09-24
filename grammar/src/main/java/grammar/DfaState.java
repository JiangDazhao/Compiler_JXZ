package grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DfaState
{
	// ��Ŀ�����,��DFA״̬��
	public int id;  
	// ��DFA״̬�еĲ���ʽ״̬���б�,ÿ��Ԫ�ر�ʾһ������ʽ״̬
	public ArrayList<ProductionState> set = new ArrayList<ProductionState>();

	/**
	 * һ��DFA״̬
	 * @param id ��Ŀ�����,��DFA״̬��
	 */
	public DfaState(int id)
	{
		this.id = id;
	}

	/**
	 * �жϲ���ʽ״̬�Ƿ���ĳ����Ŀ����
	 * @param procstate ����ʽ״̬
	 */
	public boolean lrdStatecontains(ProductionState procstate)
	{
		for(ProductionState l:set)
		{
			if(l.equalTo(procstate))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * �Ƿ���DFA״̬������µ���������ʽ״̬
	 * ����Ŀ�����޴˲���ʽ״̬����ӣ��������
	 * @param d ����ʽ״̬
	 */
	public boolean DFAaddNewLrd(ProductionState d)
	{
		if(lrdStatecontains(d))
		{
			return false;
		}
		else
		{
			set.add(d);
			return true;
		}
	}

	/**
	 * ����һ��DFA״̬������"."����ķ���
	 * @return ����һ����Ŀ��������"."����ķ���
	 */
	public ArrayList<String> getCharAfterDot()
	{	ArrayList<String> result = new ArrayList<String>();
			for(ProductionState procstate:set)
		{
			if(procstate.d.right.size()==procstate.dotindex)  // ��Լ״̬
			{
				continue;
			}
			String s = procstate.d.right.get(procstate.dotindex);  // "."����ķ���
			if(!result.contains(s))
			{
				result.add(s);
			}
		}
		return result;
	}

	
	/**
	 * ����һ����Ŀ���У�s����Ŀ��"."����ķ�����ƥ��ģ����ڵĲ���ʽ״̬�б�
	 * @param s ��������ķ���
	 * @return ����ʽ״̬�б�
	 */
	public ArrayList<ProductionState> getLRDmatchs(String s)
	{
		ArrayList<ProductionState> result = new ArrayList<ProductionState>();
		for(ProductionState procstate:set)
		{
			if(procstate.d.right.size() != procstate.dotindex)  // ���ڵ�ǰ״̬�����зǹ�Լ״̬
			{
				String s1 = procstate.d.right.get(procstate.dotindex);//�ҵ�Ŀǰ.����ַ�
				if(s1.equals(s))
				{
					result.add((ProductionState)procstate.clone());
				}
			}
		}
		return result;
	}
	
	public boolean equalTo(DfaState state)
	{
		if(this.toString().hashCode()==state.toString().hashCode())
		{
            // if(contains(set,state.set)&&contains(state.set,set)){
			return true;
		} 
		else 
		{
			return false;
		}
	}
	
	public String toString()
	{
		String result = "";
		for(int i = 0;i < set.size();i++)
		{
			result += set.get(i);
			if(i < set.size()-1)
			{
				result += "\n";
			}
		}
		return result;
	}
	
	public void print()
	{
		Iterator<ProductionState> iter = set.iterator();
		while(iter.hasNext())
		{
			iter.next().print();
		}
	}
}
