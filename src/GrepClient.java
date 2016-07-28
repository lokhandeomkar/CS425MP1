import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GrepClient { 
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		String serverFile = args[0];
		String cmd = args[1];
		String grepOutputFile = args[2];

		String line;
		
		int i=0;
		String[] tempServerInfo = new String[2];
		List<String> ServerInfo = new ArrayList<String>();
		
		GrepClientMultiThread task;
		
		try{
			BufferedReader serverFileReader = new BufferedReader(new FileReader(serverFile));
			while ((line = serverFileReader.readLine()) != null){
				tempServerInfo = line.split("\\s+");
				ServerInfo.add(tempServerInfo[0]+":"+tempServerInfo[1]);
			}
			serverFileReader.close();
		}
		
		catch(IOException e1){
			System.out.println("Unable to read Server Info file.");
			e1.printStackTrace();
		}
		try {
			ExecutorService exec = new ThreadPoolExecutor(7, 7, 300L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			List<GrepClientMultiThread> l1 = new ArrayList<>();
			List<Future<ArrayList<String>>> f1;
			Writer outputFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(grepOutputFile), "utf-8"));
			long StartTime = System.nanoTime();
			for (String key: ServerInfo){
				String[] hostport = key.split(":");
				task = new GrepClientMultiThread(hostport[0],Integer.parseInt(hostport[1]),cmd);
				l1.add(task);
			}

			f1 = exec.invokeAll(l1);
			exec.shutdown();
			exec.awaitTermination(2, TimeUnit.MINUTES);
			ArrayList<String> procOutput = new ArrayList<String>();
			int totalLines = 0;
			for(Future<ArrayList<String>> obj : f1){
				procOutput = obj.get();
				i=0;
				while(i < procOutput.size()){
					outputFileWriter.write(procOutput.get(i++)+"\n");
				}
				totalLines = totalLines+i;
				procOutput.clear();
			}
			outputFileWriter.close();
			long EndTime = System.nanoTime();
			double elapsedtime = ((EndTime-StartTime)*1.0/1000000000);
			String stringTime = String.format("%5.2f", elapsedtime);
			
			System.out.println("Fetched data, time elapsed between sending query and receiving all grep output: "+ stringTime+"s");
			System.out.println("Grep output has been written to "+grepOutputFile);
			System.out.println("Found "+totalLines+" lines.");
		} 
		catch (IOException e) {
			System.out.println("Unable to read command");
			e.printStackTrace();
		}
	}
}

class GrepClientMultiThread implements Callable<ArrayList<String>> {
	private String cmd, hostname;
	private int ports;
	private ArrayList<String> threadOutput = new ArrayList<String>(1000000);
	
	public GrepClientMultiThread(String hostname, int ports, String cmd){
			this.hostname = hostname;
			this.ports = ports;
			this.cmd = cmd;
	}
	@Override
	public ArrayList<String> call() throws Exception{
		try (
				Socket client = new Socket(hostname, ports);
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		){
			out.println(cmd);
			int value = 0;
			char cmdOutput;
			String temp = new String();
			while((value = in.read()) != -1){
				cmdOutput = (char)value;
				if (cmdOutput == '\n'){
					threadOutput.add(hostname+":"+Integer.toString(ports)+": "+temp);
					temp = new String();
					continue;
				}
				else{
					temp = temp+cmdOutput;
				}
			}
//			while ((procOutput = in.readLine()) != null){
//				threadOutput.add(hostname+":"+Integer.toString(ports)+": "+procOutput);
//			}
	 	}
		catch(IOException e)
		{	
			System.out.println("ERROR connecting to server: "+hostname+":"+Integer.toString(ports));
		}
		return threadOutput;
	}
}
