package grammar;

public class ProductionState implements Cloneable {
    public Production d;  // 产生式
    public String firstsearch;  //  向前搜索符
    public int dotindex;  // .位置,产生式符号左边

    /**
     * DFA状态集中每一个状态
     * 此时表示类似于“A->BC.D, a”的形式
     *
     * @param d     产生式
     * @param firstsearch    向前搜索符
     * @param dotindex .位置
     */
    public ProductionState(Production d, String firstsearch, int dotindex) {
        this.d = d;
        this.firstsearch = firstsearch;
        this.dotindex = dotindex;
    }

    public String toString() {
        String result = d.left + "->";
        int length = d.right.size();
        for (int i = 0; i < length; i++) {
            if (length == 1 && d.right.get(0).equals("ε")) {
                result += " .";
                break;
            } else {
                result += " ";
                if (i == dotindex) {
                    result += ".";
                }
                result += d.right.get(i);
            }
        }
        if (dotindex == length && !d.right.get(0).equals("ε")) {
            result += ".";
        }
        //加上后继符号
        result += " ,";
        result += firstsearch;
        return result;
    }

    public boolean equalTo(ProductionState procstate) {
        if (d.equalTo(procstate.d) && firstsearch.hashCode() == procstate.firstsearch.hashCode() && dotindex == procstate.dotindex) {
            return true;
        } else {
            return false;
        }
    }

    public void print() {
        System.out.println(this.toString());
    }

    public Object clone() {
        return new ProductionState(d, firstsearch, dotindex);
    }

}
