import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class GrepServer implements Runnable {
	private String logfilename;
	private int ports;
	public GrepServer(int ports, String logfilename){
		this.logfilename = logfilename;
		this.ports = ports;
	}
	@Override
	public void run(){
		String fileName = this.logfilename;
		int nlines=0;
			ArrayList<String> procOutlines = new ArrayList<String>();
			try (   
					ServerSocket serversocket = new ServerSocket(ports);
					Socket clientsocket = serversocket.accept();
					PrintWriter out = new PrintWriter(clientsocket.getOutputStream(),true);
					BufferedReader in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
			){
				String pattern;
				pattern = in.readLine();
				String grepCmd = "grep"+" "+pattern+" "+fileName;
				procOutlines = CLIWrapper.execCmd(grepCmd);
				for (nlines = 0; nlines<procOutlines.size();nlines++){
					out.println(procOutlines.get(nlines));
				}
//				System.out.println("Sent: "+ nlines +" lines.");
				nlines=0;
				procOutlines.clear();
				out.flush();
			}
			catch(IOException e) {
				System.out.println("Exception in listening on port");
				System.out.println(e.getMessage());
				System.exit(-1);
			}
	}
	public static void main(String[] args) throws IOException {
		int ports = Integer.parseInt(args[0]);
		String logfilename = args[1];
		int nlines=0;
		while (true){
			ArrayList<String> procOutlines = new ArrayList<String>();
			System.out.println("Listening for incoming connections");
			try (   
					ServerSocket serversocket = new ServerSocket(ports);
					Socket clientsocket = serversocket.accept();
					PrintWriter out = new PrintWriter(clientsocket.getOutputStream(),true);
					BufferedReader in = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
			){
				System.out.println("Connected to: "+clientsocket.getRemoteSocketAddress().toString());
				String pattern;
				pattern = in.readLine();
				String grepCmd = "grep"+" "+pattern+" "+logfilename;
				procOutlines.clear();
				procOutlines = CLIWrapper.execCmd(grepCmd);
				for (nlines = 0; nlines<procOutlines.size();nlines++){
					out.println(procOutlines.get(nlines));
				}
				System.out.println("Sent: "+ nlines +" lines.");
				procOutlines.clear();
				out.flush();
			}
			catch(IOException e) {
				System.out.println("Exception in listening on port");
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}
	}
}
