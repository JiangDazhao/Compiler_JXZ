import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class NFAutil {

//    public static void main(String[] args)
//	{
//		for(int i=0; i<NFAEdge.size(); i++)
//		{
//            System.out.println(i+1+":  "+LexicalGram.LexProcF.get(i).toString());
//			System.out.println(i+1+":  "+NFAEdge.get(i).toString());
//		}
//		String s="auto";
//		char a='a';
//		System.out.println(isKeywords(s));
//		System.out.println(s.toCharArray());
//        System.out.println(String.valueOf(a));
//	}

    //�ؼ���
    public static TreeSet<String> keywords = new TreeSet<String>();
    //���
    public static TreeSet<String> limiters = new TreeSet<String>();
    //�����
    public static TreeSet<String> calculates = new TreeSet<String>();
    // ���ս����
    public static TreeSet<String> VN = new TreeSet<String>();
    // �ս����
    public static TreeSet<String> VT = new TreeSet<String>();
    //  ��̬
    public static TreeSet<String> FinalChars = new TreeSet<String>();
    //NFA�еĸ�����
    public static ArrayList<Edge> NFAEdge= new ArrayList<>();

    static
    {
        //�ؼ���
        keywords.add("auto");  keywords.add("double"); keywords.add("int");
        keywords.add("break"); keywords.add("else"); keywords.add("switch");
        keywords.add("case"); keywords.add("return"); keywords.add("float");
        keywords.add("continue"); keywords.add("for"); keywords.add("void");
        keywords.add("if"); keywords.add("while"); keywords.add("String");
        keywords.add("complex"); keywords.add("default"); keywords.add("main");

        //���
        limiters.add("("); limiters.add(")"); limiters.add("{");
        limiters.add("}"); limiters.add(";"); limiters.add("<");
        limiters.add(">"); limiters.add("#"); limiters.add(":");

        //�����
        calculates.add("+"); calculates.add("-"); calculates.add("*");
        calculates.add("/"); calculates.add("!"); calculates.add("=");
        calculates.add("&"); calculates.add("%"); calculates.add("~");
        calculates.add("|"); calculates.add("^");

        //���ս��
        VN.add("S"); VN.add("A"); VN.add("G");
        VN.add("I"); VN.add("J"); VN.add("K");
        VN.add("B"); VN.add("C"); VN.add("F");
        VN.add("D"); VN.add("E"); VN.add("H");
        VN.add("X"); VN.add("L"); VN.add("P");
        VN.add("Z");

        // �ս����
        VT.add("0"); VT.add("1"); VT.add("2");
        VT.add("3"); VT.add("4"); VT.add("5");
        VT.add("6"); VT.add("7"); VT.add("8");
        VT.add("9");

        VT.add("+"); VT.add("-"); VT.add("*");
        VT.add("/"); VT.add("!"); VT.add("=");
        VT.add("&"); VT.add("%"); VT.add("~");
        VT.add("|"); VT.add("^");

        VT.add("."); VT.add("e"); VT.add("i");

        VT.add("("); VT.add(")"); VT.add("{");
        VT.add("}"); VT.add(";"); VT.add("<");
        VT.add(">"); VT.add("#"); VT.add(":");

        VT.add("a"); VT.add("b"); VT.add("c");
        VT.add("d"); VT.add("e"); VT.add("f");
        VT.add("g"); VT.add("h"); VT.add("i");
        VT.add("j"); VT.add("k"); VT.add("l");
        VT.add("m"); VT.add("n"); VT.add("o");
        VT.add("p"); VT.add("q"); VT.add("r");
        VT.add("s"); VT.add("t"); VT.add("u");
        VT.add("v"); VT.add("w"); VT.add("x");
        VT.add("y"); VT.add("z"); VT.add("_");
        //VT.add("��");

        //��̬�ַ�
        FinalChars.add("A"); //����
        FinalChars.add("B"); //С��
        FinalChars.add("G"); //�����
        FinalChars.add("H"); //��ʶ��
        FinalChars.add("P"); //����
        FinalChars.add("X"); //�ս�״̬
        FinalChars.add("E"); //��ѧ������
        FinalChars.add("Z"); //���

        EdgeForm();
    }

    //�γ�NFA�еĸ�����
    private static void EdgeForm()
    {
        Iterator<LexicalProc> iterProc = LexicalGram.LexProcF.iterator();
        while(iterProc.hasNext())
        {
                LexicalProc Proc=iterProc.next();
                char[] procleft =Proc.left.toCharArray();
                char[] proclist = Proc.list.toCharArray();
                if(proclist.length>1)//��������״̬��ı�
                {
                    NFAEdge.add(new Edge(procleft[0],proclist[1],proclist[0]));
                }
                else//�������ս�״̬�ı�
                {
                    NFAEdge.add(new Edge(procleft[0],'X',proclist[0]));
                }
        }
    }

    // �ǹؼ���
    public static Boolean isKeywords(String s)
    {
            if(keywords.contains(s)) return true;
            else return false;
    }


    // ������̬�ַ�
    public static Boolean isFinalChars(String s)
    {
        if(FinalChars.contains(s)) return true;
        else return false;
    }

}
