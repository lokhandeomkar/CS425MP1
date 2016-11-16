// @BeforeClass: The code that runs before the entire test fixture. 
// Used When a number of tests share the same setup code.


import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;


public class UnitTester {
	private static long t0;
	private static long t1;

	@BeforeClass
	public static void preTest(){
		new File("./test").mkdir();
		String serverInfoFile = "./test/serverinfo.txt";
		String serverInfoMultiFile = "./test/serverinfomulti.txt";
		int nLines = 100000;
		int nLines_test = 1700000;

		String[] user1 = new String[]{"anish", "omkar","patrick","tom"};
		String[] host1 = new String[]{"google.com", "youtube.com","facebook.com","reddit.com"};
		String outputFileSingleServer =  "./test/logfile.log";
		String outputFile100MB =  "./logfile100.log";
		String outputFileSameMultiServer1 =  "./test/logfile_same1.log";
		String outputFileSameMultiServer2 =  "./test/logfile_same2.log";
		String outputFileSameMultiServer3 =  "./test/logfile_same3.log";
		String outputFileSameMultiServer4 =  "./test/logfile_same4.log";
		String outputFileDiffMultiServer1 =  "./test/logfile_diff1.log";

		String[] user2 = new String[]{"omkar","anish","patrick","tom"};
		String[] host2 = new String[]{"google.com", "youtube.com","facebook.com","reddit.com"};
		String outputFileDiffMultiServer2 =  "./test/logfile_diff2.log";

		String[] user3 = new String[]{"omkar","patrick","anish","tom"};
		String[] host3 = new String[]{"google.com", "youtube.com","facebook.com","reddit.com"};
		String outputFileDiffMultiServer3 =  "./test/logfile_diff3.log";

		String[] user4 = new String[]{"omkar","tom","patrick","anish"};
		String[] host4 = new String[]{"google.com", "youtube.com","facebook.com","reddit.com"};
		String outputFileDiffMultiServer4 =  "./test/logfile_diff4.log";


		//		write the files which have the serverips
		try {
			Writer serverFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serverInfoFile), "utf-8"));
			serverFileWriter.write("localhost 1234\n");
			serverFileWriter.close();

			Writer serverFileWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serverInfoMultiFile), "utf-8"));
			serverFileWriter2.write("localhost 1234\n");
			serverFileWriter2.write("localhost 1235\n");
			serverFileWriter2.write("localhost 1236\n");
			serverFileWriter2.write("localhost 1237\n");
			serverFileWriter2.close();
		}
		catch (UnsupportedEncodingException | FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		//		generate the logfiles

		try {
			//			logfile for testing 1 server with frequent repeating and rare
			GenerateLog(nLines,host1,user1,outputFileSingleServer);
			GenerateLog(nLines_test, host1,user1, outputFile100MB);
			//			logfile for testing mutliple servers with frequent repeating and rare, same files on all servers
			GenerateLog(nLines,host1,user1,outputFileSameMultiServer1);
			GenerateLog(nLines,host1,user1,outputFileSameMultiServer2);
			GenerateLog(nLines,host1,user1,outputFileSameMultiServer3);
			GenerateLog(nLines,host1,user1,outputFileSameMultiServer4);

			//			logfile for testing multiple servers with different files on each server
			GenerateLog(nLines,host1,user1,outputFileDiffMultiServer1);
			GenerateLog(nLines,host2,user2,outputFileDiffMultiServer2);
			GenerateLog(nLines,host3,user3,outputFileDiffMultiServer3);
			GenerateLog(nLines,host4,user4,outputFileDiffMultiServer4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void GenerateLog(int nLines, String[] host, String[] user, String outputFile) throws IOException{
		Format dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		t0 = Timestamp.valueOf("2011-01-01 00:00:00").getTime();
		t1 = Timestamp.valueOf("2015-01-01 00:00:00").getTime();


		int i=0;
		int nFreq=(int)(0.7*nLines);
		int nSFreq=(int)(0.25*nLines);
		//		int nRare=(int)(0.05*nLines);

		Map<Integer,String> hostNames = new HashMap<Integer,String>();
		hostNames.put(1, host[0]);
		hostNames.put(2, host[1]);
		hostNames.put(3, host[2]);

		Map<Integer,String> userName = new HashMap<Integer,String>();
		userName.put(1,user[0]);
		userName.put(2, user[1]);
		userName.put(3, user[2]);

		Map<Integer,String> resourceName = new HashMap<Integer,String>();
		resourceName.put(1, "GET /img/shuttle.jpg");
		resourceName.put(2, "GET /img/astronaut.jpg");
		resourceName.put(3, "GET /img/nasa.jpg");

		List<Integer> integerSequence = new ArrayList<Integer>();

		for (i=0; i<nLines;++i){
			if (i<nFreq){
				integerSequence.add(1);
			}
			else if ((i>=nFreq) && (i<nSFreq+nFreq)){
				integerSequence.add(2);
			}
			else {
				integerSequence.add(3);
			}
		}
		long seed = 1234;
		long seed2 = 1235;
		long seed3 = 1236;
		Random randomNumber =  new Random(1234);
		double randomDouble;

		ArrayList<String> userdata = new ArrayList<String>();
		ArrayList<String> hostnamedata = new ArrayList<String>();
		ArrayList<String> resourcedata = new ArrayList<String>();
		ArrayList<String> datedata = new ArrayList<String>();

		Collections.shuffle(integerSequence, new Random(seed));
		for (i=0; i<nLines; ++i){
			userdata.add(userName.get(integerSequence.get(i)));
		}
		Collections.shuffle(integerSequence, new Random(seed2));
		for (i=0; i<nLines; ++i){
			hostnamedata.add(hostNames.get(integerSequence.get(i)));
		}
		Collections.shuffle(integerSequence, new Random(seed3));
		for (i=0; i<nLines; ++i){
			resourcedata.add(resourceName.get(integerSequence.get(i)));
		}

		for (i=0; i<nLines;i++){
			randomDouble = randomNumber.nextDouble();
			Date RandomDate = new Date(t0+(long)(randomDouble*(t1-t0)));
			String dateString = dateFormat.format(RandomDate);
			datedata.add(dateString);
		}
		try {
			Writer outputFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
			for (i=0; i<nLines;i++){
				outputFileWriter.write(hostnamedata.get(i)+"\t"+datedata.get(i)+"\t"+userdata.get(i)+"\t"+resourcedata.get(i)+"\n");
			}
			outputFileWriter.close();
		}
		catch (UnsupportedEncodingException | FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void CLIWrapperShouldReturnPatternNotFound() throws IOException {
		ArrayList<String> correct_output = new ArrayList<String>();
		String testcmd =  "grep pattern nonexistfile";
		System.out.println("\nTesting CLI Wrapper: Searching for non existent pattern in nonexistent file");
		assertEquals("CLIWrapper: Testing nonexistent pattern in nonexistent file.",correct_output,CLIWrapper.execCmd(testcmd));
		System.out.println("CLIWrapper Testing: Test for Non Existent Pattern: OK");
		System.out.println("------------------------------------------------------------");
		
	}
	@Test
	public void CLIWrapperShouldReturnCorrectFrequentPatterns() throws IOException {
		String referenceFile = "./referencelogfiles/reference_CLIFrequentPattern.txt";
		ArrayList<String> expected_output = new ArrayList<String>();
		BufferedReader correctOutputFile = new BufferedReader(new FileReader(referenceFile));
		String line;
		while((line = correctOutputFile.readLine())!=null){
			expected_output.add(line);
		}
		String logfilename = "./test/logfile_same1.log";
		String testcmd =  "grep 'anish' "+logfilename;
		ArrayList<String> actual_output = CLIWrapper.execCmd(testcmd);
		assertEquals("CLIWrapper: Testing frequent pattern in file.",expected_output,actual_output);
		System.out.println("CLIWrapper Testing: Testing for Frequent patterns: OK");
		System.out.println("------------------------------------------------------------");
	}
	@Test
	public void CLIWrapperShouldReturnCorrectSFrequentPatterns() throws IOException {
		String referenceFile = "./referencelogfiles/reference_CLISFrequentPattern.txt";
		ArrayList<String> expected_output = new ArrayList<String>();
		BufferedReader correctOutputFile = new BufferedReader(new FileReader(referenceFile));
		String line;
		while((line = correctOutputFile.readLine())!=null){
			expected_output.add(line);
		}
		String logfilename = "./test/logfile_same1.log";
		String testcmd =  "grep 'omkar' "+logfilename;
		ArrayList<String> actual_output = CLIWrapper.execCmd(testcmd);
		assertEquals("CLIWrapper: Testing somewhat frequent pattern in file.",expected_output,actual_output);
		System.out.println("CLIWrapper: Testing semifrequent pattern in file: OK");
		System.out.println("------------------------------------------------------------");

	}
	@Test
	public void CLIWrapperShouldReturnCorrectRarePatterns() throws IOException {
		String referenceFile = "./referencelogfiles/reference_CLIRarePattern.txt";
		ArrayList<String> expected_output = new ArrayList<String>();
		BufferedReader correctOutputFile = new BufferedReader(new FileReader(referenceFile));
		String line;
		while((line = correctOutputFile.readLine())!=null){
			expected_output.add(line);
		}
		String logfilename = "./test/logfile_same1.log";
		String testcmd =  "grep 'patrick' "+logfilename;
		ArrayList<String> actual_output = CLIWrapper.execCmd(testcmd);
		assertEquals("CLIWrapper: Testing somewhat frequent pattern in file.",expected_output,actual_output);
		System.out.println("CLIWrapper: Testing rare pattern in file: OK");
		System.out.println("------------------------------------------------------------");
	}

	@Test
	public void SingleServerFrequentPatternGrep() throws IOException{
		String correctFileName = "./referencelogfiles/reference_SingleFrequent.txt";
		String outputFileName = "./test/output_SingleFrequent.txt";
		String serverInfoFile = "./test/serverinfo.txt";
		String logfilename = "./test/logfile.log";

		// starting servers
		String line;
		String[] tempServerInfo;
		ArrayList<String> ServerInfo = new ArrayList<String>();

		try{
			BufferedReader serverFileReader = new BufferedReader(new FileReader(serverInfoFile));
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
		ExecutorService exec = Executors.newFixedThreadPool(7);
		for (String key: ServerInfo){
			String[] hostport = key.split(":");
			Runnable server = new GrepServer(Integer.parseInt(hostport[1]),logfilename);
			exec.execute(server);
		}
		//server started

		String[] params = new String[]{serverInfoFile,"anish",outputFileName};
		try {
			GrepClient.main(params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		exec.shutdownNow();		
		BufferedReader correctFileReader = new BufferedReader(new FileReader(correctFileName));
		BufferedReader outputFileReader = new BufferedReader(new FileReader(outputFileName));

		int correctFileLines = 0;
		int outputFileLines = 0;

		while(correctFileReader.readLine() != null){
			correctFileLines++;
		}
		while(outputFileReader.readLine() != null){
			outputFileLines++;
		}
		correctFileReader.close();
		outputFileReader.close();		
		assertEquals("Testing for same number of lines.",correctFileLines, outputFileLines);
		System.out.println("SingleServer: Testing frequent patterns using a single server client: OK");
		System.out.println("------------------------------------------------------------");
	}
	@Test
	public void SingleServerSFrequentPatternGrep() throws IOException{
		String correctFileName ="./referencelogfiles/reference_SingleSFrequent.txt";
		String outputFileName = "./test/output_SingleSFrequent.txt";
		String logfilename = "./test/logfile.log";
		String serverInfoFile = "./test/serverinfo.txt";

		// starting servers
		String line;
		String[] tempServerInfo;
		ArrayList<String> ServerInfo = new ArrayList<String>();

		try{
			BufferedReader serverFileReader = new BufferedReader(new FileReader(serverInfoFile));
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
		ExecutorService exec = Executors.newFixedThreadPool(7);
		for (String key: ServerInfo){
			String[] hostport = key.split(":");
			Runnable server = new GrepServer(Integer.parseInt(hostport[1]),logfilename);
			exec.execute(server);
		}
		//server started

		String[] params = new String[]{serverInfoFile,"omkar",outputFileName};
		try {
			GrepClient.main(params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		exec.shutdownNow();
		try {
			exec.awaitTermination(30L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//		read the correct input file and the produced output file, and check if both produced the same lines of output
		BufferedReader correctFileReader = new BufferedReader(new FileReader(correctFileName));
		BufferedReader outputFileReader = new BufferedReader(new FileReader(outputFileName));

		int correctFileLines = 0;
		int outputFileLines = 0;

		while(correctFileReader.readLine() != null){
			correctFileLines++;
		}
		while(outputFileReader.readLine() != null){
			outputFileLines++;
		}
		correctFileReader.close();
		outputFileReader.close();		
		assertEquals("Testing for same number of lines.",correctFileLines, outputFileLines);
		System.out.println("SingleServer: Testing semi frequent patterns using a single server client: OK");
		System.out.println("------------------------------------------------------------");
	}
	@Test
	public void SingleServerRarePatternGrep() throws IOException{
		String correctFileName = "./referencelogfiles/reference_SingleRare.txt";
		String outputFileName = "./test/output_SingleRare.txt";
		String serverInfoFile = "./test/serverinfo.txt";
		String logfilename = "./test/logfile.log";

		// starting servers
		String line;
		String[] tempServerInfo;
		ArrayList<String> ServerInfo = new ArrayList<String>();

		try{
			BufferedReader serverFileReader = new BufferedReader(new FileReader(serverInfoFile));
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
		ExecutorService exec = Executors.newFixedThreadPool(7);
		for (String key: ServerInfo){
			String[] hostport = key.split(":");
			Runnable server = new GrepServer(Integer.parseInt(hostport[1]),logfilename);
			exec.execute(server);
		}
		//server started

		String[] params = new String[]{serverInfoFile,"patrick",outputFileName};
		try {
			GrepClient.main(params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		exec.shutdownNow();
		try {
			exec.awaitTermination(30L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//		read the correct input file
		BufferedReader correctFileReader = new BufferedReader(new FileReader(correctFileName));
		BufferedReader outputFileReader = new BufferedReader(new FileReader(outputFileName));

		int correctFileLines = 0;
		int outputFileLines = 0;

		while(correctFileReader.readLine() != null){
			correctFileLines++;
		}
		while(outputFileReader.readLine() != null){
			outputFileLines++;
		}
		correctFileReader.close();
		outputFileReader.close();		
		assertEquals("Testing for same number of lines.",correctFileLines, outputFileLines);
		System.out.println("SingleServer: Testing rare patterns using a single server client: OK");
		System.out.println("------------------------------------------------------------");
	}

	@Test
	public void MultiServerFrequentPatternSameLogfile() throws IOException, InterruptedException{
		String correctFileName = "./referencelogfiles/reference_MultiFrequentSame.txt";
		String outputFileName = "./test/output_MultiFrequentSame.txt";
		String serverInfoFile = "./test/serverinfomulti.txt";
		String[] logfilename = new String[]{"./test/logfile_same1.log","./test/logfile_same2.log","./test/logfile_same3.log","./test/logfile_same4.log"};

		// starting servers
		String line;
		String[] tempServerInfo;
		ArrayList<String> ServerInfo = new ArrayList<String>();
		GrepServer task;
		ExecutorService exec;

		try{
			BufferedReader serverFileReader = new BufferedReader(new FileReader(serverInfoFile));
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
		exec = new ThreadPoolExecutor(7, 7, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		int i=0;
		String templine;
		while(i<ServerInfo.size()){
			templine = ServerInfo.get(i);
			String[] hostport = templine.split(":");
			task = new GrepServer(Integer.parseInt(hostport[1]),logfilename[i]);
			exec.submit(task);
			++i;
		}		
		String[] params = new String[]{serverInfoFile,"anish",outputFileName};
		try {
			GrepClient.main(params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		exec.shutdown();
		exec.awaitTermination(60, TimeUnit.SECONDS);

		BufferedReader correctFileReader = new BufferedReader(new FileReader(correctFileName));
		BufferedReader outputFileReader = new BufferedReader(new FileReader(outputFileName));

		int correctFileLines = 0;
		int outputFileLines = 0;

		while(correctFileReader.readLine() != null){
			correctFileLines++;
		}
		while(outputFileReader.readLine() != null){
			outputFileLines++;
		}
		correctFileReader.close();
		outputFileReader.close();
		exec.shutdown();
		assertEquals("Testing for same number of lines.",correctFileLines, outputFileLines);
		System.out.println("MultiServer: Testing frequent patterns for 4 servers with same logfile: OK");
		System.out.println("------------------------------------------------------------");
	}
	@Test
	public void MultiServerDifferentLogFiles() throws IOException, InterruptedException{
		String correctFileName = "./referencelogfiles/reference_MultiDifferent.txt";
		String outputFileName = "./test/output_MultiDifferent.txt";
		String serverInfoFile = "./test/serverinfomulti.txt";
		String[] logfilename = new String[]{"./test/logfile_diff1.log","./test/logfile_diff2.log","./test/logfile_diff3.log","./test/logfile_diff4.log"};

		// starting servers
		String line;
		String[] tempServerInfo;
		ArrayList<String> ServerInfo = new ArrayList<String>();
		GrepServer task;
		ExecutorService exec;

		try{
			BufferedReader serverFileReader = new BufferedReader(new FileReader(serverInfoFile));
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
		exec = new ThreadPoolExecutor(7, 7, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		int i=0;
		String templine;
		while(i<ServerInfo.size()){
			templine = ServerInfo.get(i);
			String[] hostport = templine.split(":");
			task = new GrepServer(Integer.parseInt(hostport[1]),logfilename[i]);
			exec.submit(task);
			++i;
		}		
		String[] params = new String[]{serverInfoFile,"anish",outputFileName};
		try {
			GrepClient.main(params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		exec.shutdown();
		exec.awaitTermination(60, TimeUnit.SECONDS);

		BufferedReader correctFileReader = new BufferedReader(new FileReader(correctFileName));
		BufferedReader outputFileReader = new BufferedReader(new FileReader(outputFileName));

		int correctFileLines = 0;
		int outputFileLines = 0;

		while(correctFileReader.readLine() != null){
			correctFileLines++;
		}
		while(outputFileReader.readLine() != null){
			outputFileLines++;
		}
		correctFileReader.close();
		outputFileReader.close();
		exec.shutdown();
		assertEquals("Testing for same number of lines.",correctFileLines, outputFileLines);
		System.out.println("MultiServer: Testing same query on 4 servers each with a different log file: OK");
		System.out.println("------------------------------------------------------------");
	}
}
