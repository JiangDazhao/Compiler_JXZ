package semantic;

import java.util.List;
import java.util.Stack;

import grammar.Tree;
import grammar.TreeNode;

public class util
{

	/**
	 * ����ת������Ӧ�Ĳ���ʽ���Ӷ��ж����嶯��
	 * @param tree
	 * @return
	 */
	public static String treeToProduction(Tree tree)
	{
		String result = tree.getFather().getProdChar()+" -> ";
		for(TreeNode c:tree.getChildren())
		{
			result += c.getProdChar();
			result += " ";
		}
		return result.trim();
	}

	/**
	 * �жϸý���ֵ�Ƿ��Ѿ����
	 * @param t ���ڵ�
	 * @return
	 */
	public static Boolean endPoint(TreeNode t)
	{
		if (t.getValue().equals("--"))
		{
			return false;
		}
		return true;
	}





	public static String print_ins(List<String> three_addr, List<FourAddr> four_addr)
	{
		StringBuffer s = new StringBuffer();
		for (int i=0; i<three_addr.size(); i++)
		{
			s.append((Smantic.initialPC + i) + ":  \t");
			s.append(four_addr.get(i).toString());	
			s.append("  \t");	
			s.append(three_addr.get(i));	
			//System.out.println(three_addr.get(i));
			s.append("\n");
		}
		System.out.println(s);
		return s.toString();			
	}
	
	
	public static Object[][] gui_ins(List<String> three_addr, List<FourAddr> four_addr)
	{
		int le = three_addr.size();
		Object[][] ins = new Object[le][3];
		for (int i=0; i<le; i++)
		{
			ins[i][0] = i+1;
			ins[i][1] = four_addr.get(i).toString();
			ins[i][2] = three_addr.get(i);
		}	
		return ins;			
	}

}
