public class LexicalProc {
    public String left;  // 产生式左部
    public String list; //产生式右部

    /**
     * 存储产生式的左部left和右部list，此时表示类似于“A->BC”或“A->bC”的形式
     * @param s 产生式字符串
     */
    public LexicalProc(String s)
    {
        String[] div = s.split("->");
        this.left = div[0].trim();
        this.list = div[1].trim();
    }

    //将产生式打印出来
    public String toString()
    {
        String result = this.left+"->";
        result += this.list;
        return result.trim();
    }

    //判断两产生式是否相等
    public boolean equalTo(LexicalProc d)
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
