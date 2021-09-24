package lexical;

public class Token 
{
	public int line;
	public String value;
	public int code;

	//默认构造函数，可以直接初始化类作为数据结构表示
	public Token(int line, String value, int code)
	{
		this.line = line;
		this.value = value;
		this.code = code;		
	}
	public String toString()
	{
		return this.line + ":< " + this.value + " ," + this.code + " >";
	}
}
