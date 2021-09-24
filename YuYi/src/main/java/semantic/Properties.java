package semantic;

import java.util.ArrayList;
import java.util.List;

public class Properties
{
	private String name;  // 变量或者函数的name 
	private String type;  // 节点类型
	private String offset;  // 数组类型的属性
	private int width;  // 类型大小属性

	
	private String addr;  // 表达式类型的属性

	private int huiAddress;  // 回填用到的属性,指令位置
	private List<Integer> truelist = new ArrayList<Integer>();  // 列表
	private List<Integer> falselist = new ArrayList<Integer>();  // 列表
	private List<Integer> nextlist = new ArrayList<Integer>();  // 指令列表

	
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
