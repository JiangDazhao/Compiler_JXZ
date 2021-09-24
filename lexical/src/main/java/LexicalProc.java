public class LexicalProc {
    public String left;  // ����ʽ��
    public String list; //����ʽ�Ҳ�

    /**
     * �洢����ʽ����left���Ҳ�list����ʱ��ʾ�����ڡ�A->BC����A->bC������ʽ
     * @param s ����ʽ�ַ���
     */
    public LexicalProc(String s)
    {
        String[] div = s.split("->");
        this.left = div[0].trim();
        this.list = div[1].trim();
    }

    //������ʽ��ӡ����
    public String toString()
    {
        String result = this.left+"->";
        result += this.list;
        return result.trim();
    }

    //�ж�������ʽ�Ƿ����
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

    //��ӡ����ʽ
    public void print()
    {
        System.out.println(this.toString());
    }
}
