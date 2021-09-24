package grammar;

import java.util.ArrayList;

public class Production 
{
	public String left;  // 产生式左部
	public ArrayList<String> right = new ArrayList<String>();  //产生式右部

	public Production(String s)
	{
		String[] div = s.split("->");
		this.left = div[0].trim();
		String[] v = div[1].split(" ");
		for(int i = 0;i < v.length;i++)
		{
			if(!v[i].trim().equals(""))	
			{
				right.add(v[i].trim());
			}	
		}
	}

	//将产生式打印出来
	public String toString()
	{
		String result = left+" -> ";
		for(String r:right)
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
