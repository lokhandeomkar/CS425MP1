import java.io.*;
import java.util.ArrayList;

public class CLIWrapper {
	
//	public static ArrayList<String> jGrep(String[] args) throws IOException{
//		String pattern = args[0];
//		String file = args[1];
//		String re = ".*" + pattern + ".*";
//		String line;
//		try {
//			BufferedReader fileData = new BufferedReader(new FileReader(file));	
//			while ((line = fileData.readLine()) != null){
//				if(line.matches(re)){
////					System.out.println(line);
//					cmdOut.add(line);
//				}
//			}
//			fileData.close();
//		}
//		catch (IOException e){
//			System.out.println("Error in CLIWrapper");
//			e.printStackTrace();
//		}
//		if(cmdOut.isEmpty())
//		{
//			cmdOut.add("Pattern not found");
//		}
//		return cmdOut;
//	}
	
	public static ArrayList<String> execCmd(String cmd) throws IOException{
		BufferedReader processOut = null;
		ArrayList<String> cmdOut = new ArrayList<String>();
		int nlines=0;
		try {
			Process process = new ProcessBuilder("bash", "-c", cmd).start();
			processOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			cmdOut = new ArrayList<String>();
			String line;
			int value;
			char cmdChar;
			String temp = new String();
			while((value = processOut.read()) != -1){
				cmdChar = (char)value;
				if (cmdChar == '\n'){
					cmdOut.add(temp);
					temp = new String();
					nlines++;
					continue;
				}
				else{
					temp = temp+cmdChar;
				}
			}
//			while(true){
//				line = processOut.readLine();
//				if(line != null){
//					cmdOut.add(line);
//					nlines++;
//				}
//				else{
//					break;
//				}
//			}
			System.out.println("CLIWrapper: "+nlines +" lines found for "+cmd);
		}
		catch (IOException e){
			System.out.println("Error in CLIWrapper");
			e.printStackTrace();
		}
		return cmdOut;
	}
}