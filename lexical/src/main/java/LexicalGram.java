import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LexicalGram {
    // 三型文法产生式集
    public static ArrayList<LexicalProc> LexProcF = new ArrayList<LexicalProc>();
    static
    {
        // 从文件中读取文法，添加相应的产生式
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
         * 从文件中读取文法并且存储到CFG类的静态容器中，编号就是容器自带的index
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
                    LexProcF.add(derivation);//存储到静态的容器中
                }
            }
            scanner.close();
        }

}
