import java.util.*;

public class NFAtoDFA {

    //初始化DFA状态集数目
    public static int Statenum = 0;
    //每个DFA状态集
    public static ArrayList<DfaState> DfaStates = new ArrayList<>();
    //DFA中的各条边转换关系
    public static ArrayList<DfaEdge> DFAEdges = new ArrayList<>();

    static {
        formDfa();
    }

    public NFAtoDFA() {
        super();
    }

//    public static void main(String[] args) {
//        System.out.println("所有DFA状态：");
//        for (DfaState dfa : DfaStates) {
//            System.out.println("" + dfa.index + dfa.charset + dfa.isfinal);
//        }
//        System.out.println("所有DFA边的关系：");
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
     * 形成所有DFA状态集，并确定DFA各边转换关系
     */
    public static void formDfa() {
        TreeSet<String> chars = new TreeSet<>();
        chars.add("S");
        TreeSet<String> closchars = Closure(chars);
        //初始化开始DFA状态
        DfaState startdfa = new DfaState(Statenum++, closchars, 0);
        DfaStates.add(startdfa);

        //新状态队列
        Queue<DfaState> newqueue = new LinkedList<>();
        newqueue.offer(startdfa);
        // System.out.println(NFAutil.VT);
        //新状态逐个走出队列
        while (!newqueue.isEmpty()) {
            DfaState leftdfa = newqueue.poll();
            //System.out.println("出队"+d.index+d.charset);
            for (String vt : NFAutil.VT) {
                TreeSet<String> movevtchars = Move(leftdfa.charset, vt);
                if (movevtchars.isEmpty()) continue; //move后无产生式
                TreeSet<String> closmovevt = Closure(movevtchars);

                //如果在已有的DFaState状态序列中，查到index
                if (DfaState.Dfacontains(closmovevt)) {
                    int toindex = DfaState.indexDfacontains(closmovevt);
                    char[] tokeychar = vt.toCharArray();
                    DfaEdge newedge = new DfaEdge(leftdfa.index, toindex, tokeychar[0]);
                    DFAEdges.add(newedge);
                }

                //如果不在已有的DfaState状态序列中，添加newDfaState
                else {
                    //  System.out.println("能够产生新集的VT："+vt);
                    //判断是否为终结状态
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
                    //  System.out.println("入队"+addque.index+addque.charset);
                    newqueue.offer(addque);
                    DfaStates.add(addque);
                }
            }
        }
    }


    /**
     * 计算符号集合的闭包
     *
     * @param set 状态集
     * @return
     */
    private static TreeSet<String> Closure(TreeSet<String> set) {
        TreeSet<String> closure = new TreeSet<>();
        closure.addAll(set);
        Iterator<String> setiter = set.iterator();
        while (setiter.hasNext()) {
            String setelement = setiter.next();
            for (Edge edge : NFAutil.NFAEdge) {
                if (String.valueOf(edge.u).equals(setelement) && String.valueOf(edge.key).equals("ε")) {
                    closure.add(String.valueOf(edge.v));
                }
            }
        }
        return closure;
    }

    /**
     * 计算某DFA状态move VT后的符号集合
     *
     * @param set 状态集
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
