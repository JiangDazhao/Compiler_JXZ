package grammar;

public class gramDfaEdge {
    public int Dfafrom; //DfaEdge����״̬
    public int Dfato; //DfaEdge����״̬
    public String DfaDirection; //DfaEdge�ϵ�Key

    public gramDfaEdge(int Dfafrom, int Dfato, String DfaDirection) {
        this.Dfafrom=Dfafrom;
        this.Dfato=Dfato;
        this.DfaDirection=DfaDirection;
    }
}
