import java.io.*;
import java.util.Scanner;

public class FunctionNameExtractor
{
	public static void main(String[] args)
	{	
		try
		{
			BufferedReader br ;
			int count = 0;
			String prototypes[] = {"public static void","public static int","public static float","public static char","public static String", "private static void ","private static int","private static float","private static char","private static String","public void","public int","public float","public char","public String", "private void ","private int","private float","private char","private String","public static ArrayList","public static boolean","private static boolean"};
			//String prototypes[] = {"public static","private static","protected static",
			String filename = new String();
			Scanner input = new Scanner(System.in);
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("functions.txt"),true));
			while(true)
			{
				count = 1;
				System.out.print("Enter Filename : ");
				filename = input.nextLine();
				if(filename.equalsIgnoreCase("END"))
					System.exit(0);
				br = new BufferedReader(new FileReader(new File(filename)));
				String temp ;
				bw.write(filename+"\r\n");
				System.out.println(filename);
				bw.write("------------------------\r\n");
				System.out.println("------------------------\n");
				while((temp = br.readLine())!=null)
				{
					for(int i=0;i<prototypes.length;i++)
					{
						if(temp.contains(prototypes[i]))
						{
							bw.write(count+". "+temp+"\r\n");
							System.out.println(count+". "+temp);
							count++;
						}
					}
				}
				bw.write("\r\n\r\n");
				System.out.println("\n");
				bw.flush();
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}