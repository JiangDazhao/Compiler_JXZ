package grammar;


public class TreeNode
{
	private int id;  // 节点编号
	private String prodChar;  // 符号类型
	private String value;  // 符号值
	private int line;  // 所在行数
	
	/**
	 * 语法树每一个节点的构造函数
	 * @param id 节点编号，用以构造一个邻接表时，链接节点
	 * @param prodChar 符号类型
	 * @param value 符号值
	 * @param line 所在行数
	 */
	public TreeNode(int id, String prodChar, String value, int line)
	{
		this.id = id;
		this.prodChar = prodChar;
		this.value = value;
		this.line = line;
	}
	
	public int getId() 
	{
		return id;
	}

	public String getProdChar()
	{
		return prodChar;
	}
	
	public String getValue() 
	{
		return value;
	}

	public int getLine() 
	{
		return line;
	}
	
	public String toString()
	{
		String result = "{" + String.valueOf(id) + "," + prodChar + "," + value + "}";
		return result;
	}
	
	public void print()
	{
		String result = "{" + String.valueOf(id) + "," + prodChar + "," + value + "}";
		System.out.println(result);
	}

}
