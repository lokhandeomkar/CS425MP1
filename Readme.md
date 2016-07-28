First start a server on the relevant computers, and then start the client as follows:

1. To run the unittest: Run "rununittest.sh". Ensure that the directory structure is the same as on gitlab. This creates sample log files, runs grep on them and compares the output against reference files. The sample reference files have been tarred into a file in the src directory

2. GrepServer.java: To run it type 'java GrepServer port logfiletoquery'

3. GrepServer.client: To run it type 'java GrepClient serveripfile patterntosearch resultsfile'
serveripfile contains servers and their ips, one server per line. The client tries to connect to all the servers in this file.

UnitTests were conducted using JUnit. UnitTester.java creates the required logfiles in the 'src/test' directory and then performs the following tests:
1. Checks if the CLIWrapper returns 0 lines if searching for a nonexistent pattern in file.
2. Checks if the CLIWrapper returns correct number of lines if searching for a frequent (70%) pattern in file.
3. Checks if the CLIWrapper returns correct number of lines if searching for a semifrequent (25%) pattern in file.
4. Checks if the CLIWrapper returns correct number of lines if searching for a rare (5%) pattern in file.
5. Checks if a single server client on local-host returns correct number of lines if searching for a frequent (70%) pattern.
6. Checks if a single server client on local-host returns correct number of lines if searching for a semifrequent (25%) pattern.
7. Checks if a single server client on local-host returns correct number of lines if searching for a rare (5%) pattern.
8. Checks if a 4 server 1 client combination on local-host returns correct number of lines if searching for a frequent pattern, with all servers querying the same log file
9. Checks if a 4 server 1 client combination on local-host returns correct number of lines if searching for a pattern, with servers querying different files where pattern appears in 70%, 25%, 5% and 0% of the lines.

100 MB Logfile query latency:
1. Querying for 'anish' which appears in 70% of the lines: 19.26s
2. Querying for 'omkar' which appears in 25% of the lines: 7.19s
3. Querying for 'patrick' which appears in 5% of the lines: 1.80