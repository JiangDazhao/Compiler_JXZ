package grammar;

public class gramDfaEdge {
    public int Dfafrom; //DfaEdge出发状态
    public int Dfato; //DfaEdge到达状态
    public String DfaDirection; //DfaEdge上的Key

    public gramDfaEdge(int Dfafrom, int Dfato, String DfaDirection) {
        this.Dfafrom=Dfafrom;
        this.Dfato=Dfato;
        this.DfaDirection=DfaDirection;
    }
}
