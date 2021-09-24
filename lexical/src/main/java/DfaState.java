import java.sql.SQLOutput;
import java.util.TreeSet;

public class DfaState { //DfaState
    public int index;
    public TreeSet<String> charset;
    public int isfinal;

    DfaState() {
    }

    DfaState(int index, TreeSet<String> charset, int isfinal) {
        this.index = index;
        this.charset = charset;
        this.isfinal = isfinal;
    }

//    public static void main(String[] args) {
////        TreeSet<String> a= new TreeSet<>();
////        a.add("G");
////        a.add("I");
////        a.add("H");
////        TreeSet<String> b= new TreeSet<>();
////        b.add("G");
////        b.add("I");
////        System.out.println(ContainEqual(a,b));
////
////    }

    /**
     * �ж����е�DFA״̬���У��Ƿ������״̬a��ͬ���ż���״̬�����򷵻���ͬ״̬�����
     *
     * @param a DFA״̬a
     * @return ��ͬ״̬�����
     */
    public static int indexDfacontains(TreeSet<String> a) {
        for (int i = 0; i < NFAtoDFA.DfaStates.size(); i++) {
            DfaState d = NFAtoDFA.DfaStates.get(i);
            if (ContainEqual(d.charset, a)) {
                return d.index;
            }
        }
        return -1;
    }

    /**
     * �ж����е�DFA״̬�����Ƿ������״̬a��ͬ���ż�
     *
     * @param a DFA״̬a
     * @return
     */
    public static boolean Dfacontains(TreeSet<String> a) {
        for (int i = 0; i < NFAtoDFA.DfaStates.size(); i++) {
            DfaState d = NFAtoDFA.DfaStates.get(i);
            if (ContainEqual(d.charset, a)) {
                return true;
            }
        }
        return false;
    }


    /**
     * �ж���DFA״̬�ķ��ż��Ƿ���ͬ
     *
     * @param a ״̬��a
     * @param b ״̬��b
     * @return
     */
    public static boolean ContainEqual(TreeSet<String> a, TreeSet<String> b) {
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag;
        for (String i : a) {
            if (!b.contains(i)) flag1 = false;
        }
        for (String j : b) {
            if (!a.contains(j)) flag2 = false;
        }
        flag = flag1 & flag2;
        return flag;
    }

    /**
     * ������Ŷ�Ӧ��dfastate
     * @param dfastateindex
     * @return
     */
    public static DfaState indextoState(int dfastateindex)
    {
        DfaState dfastate = null;
        for(DfaState d:NFAtoDFA.DfaStates)
        {
            if (d.index==dfastateindex) dfastate=(DfaState)d.clone();
        }
        return dfastate;
    }

    /**
     * �ж��Ƿ�Ϊ�ս�״̬
     * @param dfastateindex
     * @return
     */
    public static boolean isFinalState(int dfastateindex)
    {
        DfaState dfastate =new DfaState() ;
        for(DfaState d:NFAtoDFA.DfaStates)
        {
            if (d.index==dfastateindex)
            {
                dfastate=(DfaState)d.clone();
                break;
            }
        }
        if(dfastate.isfinal==1) return true;
        else return false;
    }

    //��ӡDfaState
    public String toString() {
        String result = index + " " + charset + " " + isfinal;
        return result.trim();
    }

    public Object clone()
    {
        return new DfaState(index,charset,isfinal);
    }


    /**
     * �����ս��Dfa״̬�ж��Ƿ�Ϊ��ʶ��
     * @param Dfaindex
     * @return
     */
    public static boolean isName(int Dfaindex)
    {
        DfaState s= DfaState.indextoState(Dfaindex);
        if (s.charset.contains("H")) return true;
        else return false;
    }

    /**
     * �����ս��Dfa״̬�ж��Ƿ�Ϊ����
     * @param Dfaindex
     * @return
     */
    public static boolean isFushu(int Dfaindex)
    {
        DfaState s= DfaState.indextoState(Dfaindex);
        if (s.charset.contains("P")) return true;
        else return false;
    }

    /**
     * �����ս��Dfa״̬�ж��Ƿ�Ϊ����
     * @param Dfaindex
     * @return
     */
    public static boolean isZhenShu(int Dfaindex)
    {
        DfaState s= DfaState.indextoState(Dfaindex);
        if (s.charset.contains("A")) return true;
        else return false;
    }

    /**
     * �����ս��Dfa״̬�ж��Ƿ�ΪС��
     * @param Dfaindex
     * @return
     */
    public static boolean isXiaoShu(int Dfaindex)
    {
        DfaState s= DfaState.indextoState(Dfaindex);
        if (s.charset.contains("B")) return true;
        else return false;
    }

    /**
     * �����ս��Dfa״̬�ж��Ƿ�Ϊ��ѧ������
     * @param Dfaindex
     * @return
     */
    public static boolean isSci(int Dfaindex)
    {
        DfaState s= DfaState.indextoState(Dfaindex);
        if (s.charset.contains("E")) return true;
        else return false;
    }

    /**
     * �����ս��Dfa״̬�ж��Ƿ�Ϊ���
     * @param Dfaindex
     * @return
     */
    public static boolean isLimiters(int Dfaindex)
    {
        DfaState s= DfaState.indextoState(Dfaindex);
        if (s.charset.contains("Z")) return true;
        else return false;
    }

    /**
     * �����ս��Dfa״̬�ж��Ƿ�Ϊ�����
     * @param Dfaindex
     * @return
     */
    public static boolean isCalculators(int Dfaindex)
    {
        DfaState s= DfaState.indextoState(Dfaindex);
        if (s.charset.contains("G")) return true;
        else return false;
    }

}
