package grammar;

import java.util.ArrayList;

public class Tree
{
	private TreeNode fatherNode;  // ���ڵ�
	private ArrayList<TreeNode> children;  // �����б�
	
	/**
	 * �﷨���Ĺ��캯��
	 * @param fatherNode ���ڵ�
	 * @param children �����б�
	 */
	public Tree(TreeNode fatherNode, ArrayList<TreeNode> children)
	{
		this.fatherNode = fatherNode;
		this.children = children;
	}
	
	public TreeNode getFather() 
	{
		return fatherNode;
	}
	
	public ArrayList<TreeNode> getChildren() 
	{
		return children;
	}


	public String toString()
	{
		String result = fatherNode.toString() + " -> ";
		for (int i=0; i<children.size(); i++)
		{
			result += children.get(i).toString();
		}
		return result;
	}
	
	
	public void print()
	{
		String result = fatherNode.toString() + " -> ";
		for (int i=0; i<children.size(); i++)
	{
		result += children.get(i).toString();
	}
		System.out.println(result);
	}


}
