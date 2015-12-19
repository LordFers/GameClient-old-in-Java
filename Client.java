package client;

import javax.swing.JOptionPane;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Client
{
    public static void main(String[] args)
    {
        //new Connection().initialize();
        //System.out.print("Initializing and processing the engine-OpenGL with lwjgl 2.9.x...");
        //System.out.print("ms: " + System.currentTimeMillis() + " - mm" + System.currentTimeMillis() / 1000);
        new Engine().initialize();
    }
    
    public void MessageBox(String str)
    {
        JOptionPane.showMessageDialog(null, str);
    }

    public void Sleep(long ms)
    {
        try{
            Thread.sleep(ms);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public String mid(String str, int start, int end)
    {
        String buff = "";
        for(int i = start; i < end; i++)
        {
            char a = str.charAt(i);
            buff = buff + a;
        }
        return buff;
    }
    
    public String left(String str, String separe)
    {
        String buff = "";
        for(int i = 0; i < str.length(); i++)
        {
            char a = str.charAt(i);
            String compare = "" + a;
            if(compare.intern() == separe.intern()) return buff;
            buff = buff + a;
        }
        return buff;
    }
    
    int GetVar(String filename, String index, String var)
    {
        try{
            File text = new File("resources/inits/arms.dat");
            Scanner scan = new Scanner(text,"UTF-8");
            index = "[" + index + "]";
            var = var;

            while(scan.hasNextLine())
            {
                String line = scan.nextLine();
                line = line.toUpperCase();
                line = line.replaceAll("\\s", "");
                if (line.intern() == index.intern())
                {
                    while(scan.hasNextLine())
                    {
                        line = scan.nextLine();
                        String compare = new Scanner(line).useDelimiter("\\=").next();
                        if (compare.intern() == var.intern())
                        {
                            line = left(line, "'");
                            line = mid(line, var.length()+1,line.length());
                            if (line.length() < 1) return 1;
                            int a = new Scanner(line).nextInt();
                            return a;
                        }
                    }
                }
            }  
        }
        catch (FileNotFoundException fi)
        {
            fi.printStackTrace();
        }
        return 1;
    }
}
