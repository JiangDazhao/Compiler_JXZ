import java.util.*;

public class NFAtoDFA {

    //��ʼ��DFA״̬����Ŀ
    public static int Statenum = 0;
    //ÿ��DFA״̬��
    public static ArrayList<DfaState> DfaStates = new ArrayList<>();
    //DFA�еĸ�����ת����ϵ
    public static ArrayList<DfaEdge> DFAEdges = new ArrayList<>();

    static {
        formDfa();
    }

    public NFAtoDFA() {
        super();
    }

//    public static void main(String[] args) {
//        System.out.println("����DFA״̬��");
//        for (DfaState dfa : DfaStates) {
//            System.out.println("" + dfa.index + dfa.charset + dfa.isfinal);
//        }
//        System.out.println("����DFA�ߵĹ�ϵ��");
//        System.out.println(DFAEdges.size());
//        for (DfaEdge edge : DFAEdges) {
//            System.out.println(edge.toString());
//        }
////        TreeSet<String> test = new TreeSet<>();
////        test.add("S");
////        TreeSet<String> result = Closure(test);
////        System.out.println(result);
////        TreeSet<String> result1 = Move(test, "+");
////        System.out.println(result1);
//    }

    /**
     * �γ�����DFA״̬������ȷ��DFA����ת����ϵ
     */
    public static void formDfa() {
        TreeSet<String> chars = new TreeSet<>();
        chars.add("S");
        TreeSet<String> closchars = Closure(chars);
        //��ʼ����ʼDFA״̬
        DfaState startdfa = new DfaState(Statenum++, closchars, 0);
        DfaStates.add(startdfa);

        //��״̬����
        Queue<DfaState> newqueue = new LinkedList<>();
        newqueue.offer(startdfa);
        // System.out.println(NFAutil.VT);
        //��״̬����߳�����
        while (!newqueue.isEmpty()) {
            DfaState leftdfa = newqueue.poll();
            //System.out.println("����"+d.index+d.charset);
            for (String vt : NFAutil.VT) {
                TreeSet<String> movevtchars = Move(leftdfa.charset, vt);
                if (movevtchars.isEmpty()) continue; //move���޲���ʽ
                TreeSet<String> closmovevt = Closure(movevtchars);

                //��������е�DFaState״̬�����У��鵽index
                if (DfaState.Dfacontains(closmovevt)) {
                    int toindex = DfaState.indexDfacontains(closmovevt);
                    char[] tokeychar = vt.toCharArray();
                    DfaEdge newedge = new DfaEdge(leftdfa.index, toindex, tokeychar[0]);
                    DFAEdges.add(newedge);
                }

                //����������е�DfaState״̬�����У����newDfaState
                else {
                    //  System.out.println("�ܹ������¼���VT��"+vt);
                    //�ж��Ƿ�Ϊ�ս�״̬
                    int finalcharflag = 0;
                    for (String finalchar : NFAutil.FinalChars) {
                        if (closmovevt.contains(finalchar)) {
                            finalcharflag = 1;
                            break;
                        }
                    }
                    int leftindex = leftdfa.index;
                    int toindex = Statenum;
                    char[] tokeychar = vt.toCharArray();
                    DfaEdge newedge = new DfaEdge(leftindex, toindex, tokeychar[0]);
                    DFAEdges.add(newedge);

                    DfaState addque = new DfaState(Statenum++, closmovevt, finalcharflag);
                    //  System.out.println("���"+addque.index+addque.charset);
                    newqueue.offer(addque);
                    DfaStates.add(addque);
                }
            }
        }
    }


    /**
     * ������ż��ϵıհ�
     *
     * @param set ״̬��
     * @return
     */
    private static TreeSet<String> Closure(TreeSet<String> set) {
        TreeSet<String> closure = new TreeSet<>();
        closure.addAll(set);
        Iterator<String> setiter = set.iterator();
        while (setiter.hasNext()) {
            String setelement = setiter.next();
            for (Edge edge : NFAutil.NFAEdge) {
                if (String.valueOf(edge.u).equals(setelement) && String.valueOf(edge.key).equals("��")) {
                    closure.add(String.valueOf(edge.v));
                }
            }
        }
        return closure;
    }

    /**
     * ����ĳDFA״̬move VT��ķ��ż���
     *
     * @param set ״̬��
     * @param a   VT
     * @return
     */
    private static TreeSet<String> Move(TreeSet<String> set, String a) {
        TreeSet<String> Movea = new TreeSet<>();
        for (Edge edge : NFAutil.NFAEdge) {
            for (String s : set) {
                if (String.valueOf(edge.u).equals(s) && String.valueOf(edge.key).equals(a)) {
                    Movea.add(String.valueOf(edge.v));
                }
            }
        }
        return Movea;
    }

}
