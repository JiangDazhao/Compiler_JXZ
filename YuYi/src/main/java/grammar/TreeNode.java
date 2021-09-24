package grammar;


public class TreeNode
{
	private int id;  // �ڵ���
	private String prodChar;  // ��������
	private String value;  // ����ֵ
	private int line;  // ��������
	
	/**
	 * �﷨��ÿһ���ڵ�Ĺ��캯��
	 * @param id �ڵ��ţ����Թ���һ���ڽӱ�ʱ�����ӽڵ�
	 * @param prodChar ��������
	 * @param value ����ֵ
	 * @param line ��������
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
