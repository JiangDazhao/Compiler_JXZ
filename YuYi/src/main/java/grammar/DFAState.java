package grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DFAState 
{
	// 项目集编号,即DFA状态号
	public int id;  
	// 该DFA状态中的产生式状态集列表,每个元素表示一个产生式状态
	public ArrayList<ProductionState> set = new ArrayList<ProductionState>();

	/**
	 * 一个DFA状态
	 * @param id 项目集编号,即DFA状态号
	 */
	public DFAState(int id)
	{
		this.id = id;
	}

	/**
	 * 判断产生式状态是否在某个项目集中
	 * @param lrd 产生式状态
	 */
	public boolean lrdStatecontains(ProductionState lrd)
	{
		for(ProductionState l:set)
		{
			if(l.equalTo(lrd))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否在DFA状态中添加新的衍生产生式状态
	 * 若项目集中无此产生式状态则添加，否则不添加
	 * @param d 产生式状态
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
	 * 返回一个DFA状态中所有"."后面的符号
	 * @return 返回一个项目集中所有"."后面的符号
	 */
	public ArrayList<String> getCharAfterDot()
	{	ArrayList<String> result = new ArrayList<String>();
			for(ProductionState lrd:set)
		{
			if(lrd.d.list.size()==lrd.index)  // 规约状态
			{
				continue;
			}
			String s = lrd.d.list.get(lrd.index);  // "."后面的符号
			if(!result.contains(s))
			{
				result.add(s);
			}
		}
		return result;
	}

	
	/**
	 * 返回一个项目集中，s与项目集"."后面的符号相匹配的，所在的产生式状态列表
	 * @param s 即将读入的符号
	 * @return 产生式状态列表
	 */
	public ArrayList<ProductionState> getLRDmatchs(String s)
	{
		ArrayList<ProductionState> result = new ArrayList<ProductionState>();
		for(ProductionState lrd:set)
		{
			if(lrd.d.list.size() != lrd.index)  // 对于当前状态中所有非规约状态
			{
				String s1 = lrd.d.list.get(lrd.index);//找到目前.后的字符
				if(s1.equals(s))
				{
					result.add((ProductionState)lrd.clone());
				}
			}
		}
		return result;
	}
	
	public boolean equalTo(DFAState state)
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
