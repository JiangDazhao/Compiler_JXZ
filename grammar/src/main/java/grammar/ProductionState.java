package grammar;

public class ProductionState implements Cloneable {
    public Production d;  // ����ʽ
    public String firstsearch;  //  ��ǰ������
    public int dotindex;  // .λ��,����ʽ�������

    /**
     * DFA״̬����ÿһ��״̬
     * ��ʱ��ʾ�����ڡ�A->BC.D, a������ʽ
     *
     * @param d     ����ʽ
     * @param firstsearch    ��ǰ������
     * @param dotindex .λ��
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
            if (length == 1 && d.right.get(0).equals("��")) {
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
        if (dotindex == length && !d.right.get(0).equals("��")) {
            result += ".";
        }
        //���Ϻ�̷���
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
