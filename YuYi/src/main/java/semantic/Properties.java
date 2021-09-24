package semantic;

import java.util.ArrayList;
import java.util.List;

public class Properties
{
	private String name;  // �������ߺ�����name 
	private String type;  // �ڵ�����
	private String offset;  // �������͵�����
	private int width;  // ���ʹ�С����

	
	private String addr;  // ���ʽ���͵�����

	private int huiAddress;  // �����õ�������,ָ��λ��
	private List<Integer> truelist = new ArrayList<Integer>();  // �б�
	private List<Integer> falselist = new ArrayList<Integer>();  // �б�
	private List<Integer> nextlist = new ArrayList<Integer>();  // ָ���б�

	
	public String getName()
	{
		return name;
	}

	
	public String getAddr()
	{
		return addr;
	}

	public String getType()
	{
		return type;
	}

	public String getOffset()
	{
		return offset;
	}
	
	public int getWidth()
	{
		return width;
	}

	public int getQuad()
	{
		return huiAddress;
	}
	
	public List<Integer> getNextList()
	{
		return nextlist;
	}

	public List<Integer> getTrue()
	{
		return truelist;
	}

	public List<Integer> getFalse()
	{
		return falselist;
	}

	
	public void setName(String name)
	{
		this.name=name;
	}

	public void setValue(String addr)
	{
		this.addr=addr;
	}

	public void setType(String type)
	{
		this.type=type;
	}

	public void setOffset(String offset)
	{
		this.offset=offset;
	}
	
	public void setWidth(int width)
	{
		this.width=width;
	}
	
	public void sethuiAddress(int huiAddress)
	{
		this.huiAddress=huiAddress;
	}

	public void setNext(List<Integer> nextlist)
	{
		this.nextlist=nextlist;
	}

	public void setTrue(List<Integer> truelist)
	{
		this.truelist=truelist;
	}

	public void setFalse(List<Integer> falselist)
	{
		this.falselist=falselist;
	}

}
