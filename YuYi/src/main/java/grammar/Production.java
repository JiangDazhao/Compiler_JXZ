package grammar;

import java.util.ArrayList;

public class Production 
{
	public String left;  // 产生式左部
	public ArrayList<String> list = new ArrayList<String>();  //产生式右部
	
	/**
	 * 存储产生式的左部left和右部list，将原先的产生式“A->B C D”重新表示
	 * 此时表示类似于“A->BCD”的形式
	 * @param s 产生式字符串
	 */
	public Production(String s)
	{
		String[] div = s.split("->");
		this.left = div[0].trim();
		String[] v = div[1].split(" ");
		for(int i = 0;i < v.length;i++)
		{
			if(!v[i].trim().equals(""))	
			{
				list.add(v[i].trim());
			}	
		}
	}

	//将产生式打印出来
	public String toString()
	{
		String result = left+" -> ";
		for(String r:list)
		{
			result += r;
			result += " ";
		}
		return result.trim();
	}

	//判断两产生式是否相等
	public boolean equalTo(Production d)
	{
		if(this.toString().equals(d.toString()))
		{
			return true;
		} 
		else 
		{
			return false;
		}
	}

	//打印产生式
	public void print()
	{
		System.out.println(this.toString());
	}
}
