package lexical;

public class Token 
{
	public int line;
	public String value;
	public int code;

	//Ĭ�Ϲ��캯��������ֱ�ӳ�ʼ������Ϊ���ݽṹ��ʾ
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
