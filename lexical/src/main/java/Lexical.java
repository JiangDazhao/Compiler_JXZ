import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Lexical {
    private String text;  // 读入的测试样例文本
    private JTable jtable1;  // 实为Object[][]数组，存储识别信息，即“行数-Token-种别码-单词类别”
    private JTable jtable2;  // 实为Object[][]数组，存储错误报告，即“行数-错误内容-错误信息”
    private JTable jtable3;  // Object[][]数组，存储的是标识符表，即“标识符-标识符位置”
    private JTable jtable4;  // Object[][]数组，存储的是常量表，即“常量-常量位置”

    /**
     * 构造函数
     *
     * @param text    String型，读入的测试样例文本
     * @param jtable1 实为Object[][]数组，存储识别信息，即“行数-Token-单词类别-种别码”
     * @param jtable2 实为Object[][]数组，存储错误报告，即“行数-错误内容-错误信息”
     * @param jtable3 实为Object[][]数组，存储的是标识符表，即“标识符-标识符位置”
     * @param jtable4 实为Object[][]数组，存储的是常量表，即“常量-常量位置”
     */
    public Lexical(String text, JTable jtable1, JTable jtable2, JTable jtable3, JTable jtable4) {
        this.text = text;
        this.jtable1 = jtable1;
        this.jtable2 = jtable2;
        this.jtable3 = jtable3;
        this.jtable4 = jtable4;
    }

    // 记录符号表位置
    public static int symbol_pos = 0;
    // 符号表及其位置的HashMap
    public static Map<String, Integer> symbol = new HashMap<String, Integer>();

    // 记录常量位置
    public static int constant_pos = 0;
    // 常量表及其位置的HashMap
    public static Map<String, Integer> constant = new HashMap<String, Integer>();  // 常量表HashMap

    // 保存token列表
    public static StringBuffer result = new StringBuffer();

//       public static void main(String[] args) {
//        DfaState startstate = NFAtoDFA.DfaStates.get(0);
//        System.out.println(startstate.toString());
//       }

    /**
     * 核心函数
     * 根据已经构成的DFA状态转换表
     * 按行分析数据，识别相应信息
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
//			System.out.println("第"+line+"行：");
//			for(int i=0;i<spacesplit.length;i++)
//			{
//				System.out.println(spacesplit[i]);
//			}
//			for (int i = 0; i < spacesplit.length; i++) {
//				System.out.println(spacesplit[i]);
//			}
            System.out.println("识别的行为" + str);

            //一行划开成若干个String集
            for (int i = 0; i < spacesplit.length; i++) {
                //将用空格分开的String字符转换为char数组
                char[] splitcharstr = spacesplit[i].trim().toCharArray();

                int edgestart = 0; //开始的状态
                int edgeend = 0; //转换到的状态
                String token = "";//缓冲中分割字符的识别部分
                int j;// 把splitcharstr全部跑完的索引

                //操作单个charstr数组
                // System.out.println(splitcharstr.length);
                int havemistake = 0; //同一个charstr数组只进行一次错误处理
                for (j = 0; j < splitcharstr.length; j++) {
                    //每个char能否找到边转换关系
                    int flag = 0;
                    for (DfaEdge de : NFAtoDFA.DFAEdges) {
                        if (de.u == edgestart && de.key == splitcharstr[j]) {
                            edgeend = de.v;
                            edgestart = edgeend;
                            token += splitcharstr[j];
                            System.out.println("输入" + splitcharstr[j] + "变为" + token + " edgeend为：" + edgeend);
                            flag = 1;
                            break;
                        }
                    }
                    //System.out.println(j+" "+flag);
                    if (flag == 0)        //找不到边转换关系
                    {
                        DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                        tableModel2.addRow(new Object[]{line, String.valueOf(splitcharstr), "不合法字符"});
                        jtable2.invalidate();
                        havemistake = 1;
                        //System.out.println(spacesplit[i]);
                        break;
                    }
                }
                if (DfaState.isFinalState(edgeend) && j == splitcharstr.length) //如果是终结状态
                {
                    boolean havedone = false;
                    if (NFAutil.isKeywords(token) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "关键字"});
                        jtable1.invalidate();
                        result.append(" " + line + ",关键字," + token + "\n");

                    }
                    if (DfaState.isLimiters(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "界符"});
                        jtable1.invalidate();
                        result.append(" " + line + ",界符," + token + "\n");
                    }
                    if (DfaState.isCalculators(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "运算符"});
                        jtable1.invalidate();
                        result.append(" " + line + ",运算符," + token + "\n");
                    }
                    if (DfaState.isXiaoShu(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "小数常量"});
                        jtable1.invalidate();
                        result.append(" " + line + ",小数常量," + token + "\n");

                        constant.put(token, constant_pos);
                        DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                        tableModel4.addRow(new Object[]{token, constant_pos});
                        jtable4.invalidate();
                        constant_pos++;
                    }
                    if (DfaState.isZhenShu(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "整数常量"});
                        jtable1.invalidate();
                        result.append(" " + line + ",整数常量," + token + "\n");

                        constant.put(token, constant_pos);
                        DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                        tableModel4.addRow(new Object[]{token, constant_pos});
                        jtable4.invalidate();
                        constant_pos++;
                    }
                    if (DfaState.isSci(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "科学计数常量"});
                        jtable1.invalidate();
                        result.append(" " + line + ",科学计数常量," + token + "\n");

                        constant.put(token, constant_pos);
                        DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                        tableModel4.addRow(new Object[]{token, constant_pos});
                        jtable4.invalidate();
                        constant_pos++;
                    }
                    if (DfaState.isFushu(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "复数常量"});
                        jtable1.invalidate();
                        result.append(" " + line + ",复数常量," + token + "\n");

                        constant.put(token, constant_pos);
                        DefaultTableModel tableModel4 = (DefaultTableModel) jtable4.getModel();
                        tableModel4.addRow(new Object[]{token, constant_pos});
                        jtable4.invalidate();
                        constant_pos++;
                    }
                    if (DfaState.isName(edgeend) && !havedone) {
                        havedone = true;
                        DefaultTableModel tableModel = (DefaultTableModel) jtable1.getModel();
                        tableModel.addRow(new Object[]{line, token, "标识符"});
                        jtable1.invalidate();
                        result.append(" " + line + ",标识符," + token + "\n");

                        symbol.put(token, symbol_pos);
                        DefaultTableModel tableModel3 = (DefaultTableModel) jtable3.getModel();
                        tableModel3.addRow(new Object[]{token, symbol_pos});
                        jtable3.invalidate();
                        symbol_pos++;
                    }
                } else if (havemistake == 0) {
                    DefaultTableModel tableModel2 = (DefaultTableModel) jtable2.getModel();
                    tableModel2.addRow(new Object[]{line, String.valueOf(splitcharstr), "不合法字符"});
                    jtable2.invalidate();
                    //System.out.println(spacesplit[i]);
                }

            }
        }
    }

    /**
     * 输出结果
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