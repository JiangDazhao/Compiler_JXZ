public class DfaEdge { //Dfaת����ϵ
    public int u, v;
    public char key;

    public DfaEdge(int u, int v, char key) {
        this.u = u;
        this.v = v;
        this.key = key;
    }

    public String toString() {
        String result=u + " "+key +" "+ v;
        return result.trim()  ;
    }

    public boolean equals(Edge e) {
        if(this.toString().equals(e.toString()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
