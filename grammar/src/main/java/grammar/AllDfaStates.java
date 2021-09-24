package grammar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AllDfaStates
{
	// 所有项目集列表，每个元素为一个DFA状态
	public ArrayList<DfaState> states = new ArrayList<DfaState>();
	
	
	public DfaState get(int i)
	{
		return states.get(i);
	}
	
	public int size()
	{
		return states.size();
	}

	public void printAllStates()
	{
		int size = states.size();
		for(int i = 0;i < size;i++)
		{
			System.out.println("I"+i+":");
			states.get(i).print();
		}
	}
	
	
	
	public void writefile()
	{		
        String direction = "LR_DFA_State_Set.txt";
        try 
        {
            File file = new File(direction);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            
    		int size = states.size();
    		for(int i = 0;i < size;i++)
    		{
    			bw.write("\n"+"I"+i+":"+"\n"); 
    			//System.out.println("I"+i+":");
    			bw.write(states.get(i).toString());
    			bw.write("\n");
    		} 
            bw.close(); 
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
	
	/*
	public DFA()
	{
		
	}*/

}
