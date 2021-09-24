import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LexicalGram {
    // �����ķ�����ʽ��
    public static ArrayList<LexicalProc> LexProcF = new ArrayList<LexicalProc>();
    static
    {
        // ���ļ��ж�ȡ�ķ��������Ӧ�Ĳ���ʽ
        try
        {
            //read("grammar.txt");
            read("lexicalgram.txt");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args)
//	{
//		for(int i=0; i<LexProcF.size(); i++)
//		{
//			System.out.println(i+1+":  "+LexProcF.get(i).toString());
//		}
//	}

        /**
         * ���ļ��ж�ȡ�ķ����Ҵ洢��CFG��ľ�̬�����У���ž��������Դ���index
         * @param filename
         * @throws FileNotFoundException
         */
        private static void read(String filename) throws FileNotFoundException
        {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNext())
            {
                String line = scanner.nextLine().trim();
                if(!line.equals(""))
                {
                    String[] div = line.split("->");
                    String right = div[1];
                    LexicalProc derivation = new LexicalProc(div[0].trim()+"->"+right.trim());
                    LexProcF.add(derivation);//�洢����̬��������
                }
            }
            scanner.close();
        }

}
