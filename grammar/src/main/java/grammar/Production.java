package grammar;

import java.util.ArrayList;

public class Production 
{
	public String left;  // ����ʽ��
	public ArrayList<String> right = new ArrayList<String>();  //����ʽ�Ҳ�

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

	//������ʽ��ӡ����
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

	//�ж�������ʽ�Ƿ����
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

	//��ӡ����ʽ
	public void print()
	{
		System.out.println(this.toString());
	}
}
