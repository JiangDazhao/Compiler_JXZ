import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Lexical {
    private String text;  // ����Ĳ��������ı�
    private JTable jtable1;  // ʵΪObject[][]���飬�洢ʶ����Ϣ����������-Token-�ֱ���-�������
    private JTable jtable2;  // ʵΪObject[][]���飬�洢���󱨸棬��������-��������-������Ϣ��
    private JTable jtable3;  // Object[][]���飬�洢���Ǳ�ʶ����������ʶ��-��ʶ��λ�á�
    private JTable jtable4;  // Object[][]���飬�洢���ǳ�������������-����λ�á�

    /**
     * ���캯��
     *
     * @param text    String�ͣ�����Ĳ��������ı�
     * @param jtable1 ʵΪObject[][]���飬�洢ʶ����Ϣ����������-Token-�������-�ֱ��롱
     * @param jtable2 ʵΪObject[][]���飬�洢���󱨸棬��������-��������-������Ϣ��
     * @param jtable3 ʵΪObject[][]���飬�洢���Ǳ�ʶ����������ʶ��-��ʶ��λ�á�
     * @param jtable4 ʵΪObject[][]���飬�洢���ǳ�������������-����λ�á�
     */
    public Lexical(String text, JTable jtable1, JTable jtable2, JTable jtable3, JTable jtable4) {
        this.text = text;
        this.jtable1 = jtable1;
        this.jtable2 = jtable2;
        this.jtable3 = jtable3;
        this.jtable4 = jtable4;
    }

    // ��¼���ű�λ��
    public static int symbol_pos = 0;
    // ���ű���λ�õ�HashMap
    public static Map<String, Integer> symbol = new HashMap<String, Integer>();

    // ��¼����λ��
    public static int constant_pos = 0;
    // ��������λ�õ�HashMap
    public static Map<String, Integer> constant = new HashMap<String, Integer>();  // ������HashMap

    // ����token�б�
    public static StringBuffer result = new StringBuffer();

//       public static void main(String[] args) {
//        DfaState startstate = NFAtoDFA.DfaStates.get(0);
//        System.out.println(startstate.toString());
//       }

    /**
     * ���ĺ���
     * �����Ѿ����ɵ�DFA״̬ת����
     * ���з������ݣ�ʶ����Ӧ��Ϣ
     */
    public void lex() {
        String[] texts = text.split("\n");
        //String[] texts={"int","a"};
        symbol.clear();
        symbol_pos = 0;
        constant.clear();
        constant_pos = 0;
        //System.out.println(texts.length);
        for (int m = 0; m < texts.length; m++) {
            int line = m + 1;
            String str = texts[m];
            String[] spacesplit = str.trim().split(" ");
//			int line=m+1;
//			System.out.println("��"+line+"�У�");
//			for(int i=0;i<spacesplit.length;i++)
//			{
//				System.out.println(spacesplit[i]);
//			}
//			for (int i = 0; i < spacesplit.length; i++) {
//				System.out.println(spacesplit[i]);
//			}
            System.out.println("ʶ�����Ϊ" + str);

            //һ�л��������ɸ�String��
            for (int i = 0; i < spacesplit.length; i++) {
                //���ÿո�ֿ���String�ַ�ת��Ϊchar����
                char[] splitcharstr = spacesplit[i].trim().toCharArray();

                int edgestart = 0; //��ʼ��״̬
                int edgeend = 0; //ת������״̬
                String token = "";//�����зָ��ַ���ʶ�𲿷�
                int j;// ��splitcharstrȫ�����������

                //��������charstr����
                // System.out.println(splitcharstr.length);
                int havemistake = 0; //ͬһ��charstr����ֻ����һ�δ�����
                for (j = 0; j < splitcharstr.length; j++) {
                    //ÿ��char�ܷ��ҵ���ת����ϵ
                    int flag = 0;
                    for (DfaEdge de : NFAtoDFA.DFAEdges) {
                        if (de.u == edgestart && de.key == splitcharstr[j]) {
                            edgeend = de.v;
                            edgestart = edgeend;
                            token += splitcharstr[j];
                            System.out.println("����" + splitcharstr[j] + "��Ϊ" + token + " edgeendΪ��" + edgeend);
                            flag = 1;
                            break;
                        }
                    }
                    //System.out.println(j+" "+flag);
                    if (flag == 0)        //�Ҳ�����ת����ϵ
                    {
                        DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                        tableModel2.addRow(new Object[]{line, String.valueOf(splitcharstr), "���Ϸ��ַ�"});
                        jtable2.invalidate();
                        havemistake = 1;
                        //System.out.println(spacesplit[i]);
                        break;
                    }
                }
                if (DfaState.isFinalState(edgeend) && j == splitcharstr.length) //������ս�״̬
                {
                    boolean havedone = false;
                    if (NFAutil.isKeywords(token) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "�ؼ���"});
                        jtable1.invalidate();
                        result.append(" " + line + ",�ؼ���," + token + "\n");

                    }
                    if (DfaState.isLimiters(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "���"});
                        jtable1.invalidate();
                        result.append(" " + line + ",���," + token + "\n");
                    }
                    if (DfaState.isCalculators(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "�����"});
                        jtable1.invalidate();
                        result.append(" " + line + ",�����," + token + "\n");
                    }
                    if (DfaState.isXiaoShu(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "С������"});
                        jtable1.invalidate();
                        result.append(" " + line + ",С������," + token + "\n");

                        constant.put(token, constant_pos);
                        DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                        tableModel4.addRow(new Object[]{token, constant_pos});
                        jtable4.invalidate();
                        constant_pos++;
                    }
                    if (DfaState.isZhenShu(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "��������"});
                        jtable1.invalidate();
                        result.append(" " + line + ",��������," + token + "\n");

                        constant.put(token, constant_pos);
                        DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                        tableModel4.addRow(new Object[]{token, constant_pos});
                        jtable4.invalidate();
                        constant_pos++;
                    }
                    if (DfaState.isSci(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "��ѧ��������"});
                        jtable1.invalidate();
                        result.append(" " + line + ",��ѧ��������," + token + "\n");

                        constant.put(token, constant_pos);
                        DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                        tableModel4.addRow(new Object[]{token, constant_pos});
                        jtable4.invalidate();
                        constant_pos++;
                    }
                    if (DfaState.isFushu(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "��������"});
                        jtable1.invalidate();
                        result.append(" " + line + ",��������," + token + "\n");

                        constant.put(token, constant_pos);
                        DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                        tableModel4.addRow(new Object[]{token, constant_pos});
                        jtable4.invalidate();
                        constant_pos++;
                    }
                    if (DfaState.isName(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "��ʶ��"});
                        jtable1.invalidate();
                        result.append(" " + line + ",��ʶ��," + token + "\n");

                        symbol.put(token, symbol_pos);
                        DefaultTableModel tableModel3 = (DefaultTableModel) jtable3.getModel();
                        tableModel3.addRow(new Object[]{token, symbol_pos});
                        jtable3.invalidate();
                        symbol_pos++;
                    }
                } else if (havemistake == 0) {
                    DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                    tableModel2.addRow(new Object[]{line, String.valueOf(splitcharstr), "���Ϸ��ַ�"});
                    jtable2.invalidate();
                    //System.out.println(spacesplit[i]);
                }

            }
        }
    }

    /**
     * ������
     */
    public static void writefile(StringBuffer str) {
        String path = "Token.txt";
        try {
            File file = new File(path);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(str.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}