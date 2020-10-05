package Project_1;

import java.io.*;
import java.net.*;
import java.util.*;

public class Talk {
	
	private static Socket client;
	private static boolean isClient;
	private static boolean isServ;
	private static boolean auto;
	private static boolean CLO;
	private static int CLONum;
	private static int cPort;
	private static String[] CLP;
	private static String autoHostIP = null;
	private static String autoPortCom = null;
	private static String autoPort = null;
	private static ServerSocket server;
	public static BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) {
		System.out.println("Welcome to the Talk.java program by John.");
		System.out.println("If at any point you get lost, type in 'Talk -help.'");
		if (args.length != 0)
		{
			CLO = true;
			CLONum = args.length;
			CLP = args;
		}
		commands();
		System.out.println("Thanks for coming!");
	}
	
	private static void commands()
	{
		try
			{
			String com = ""; 	// This will store the first command in the sequence, which will take the client to the corresponding method
			String par1 = "";	// This will store the second command (if applicable for the first command) as a parameter for port or IP/host
			String par2 = "";	// This will store the third part of the command if applicable, as a parameter storing -p in port number
			String par3 = "";	// This will store the final part of the command which gives the port number.
			if (CLO)
			{
				for( int i = 0; i < CLONum; i++)
					{
						switch(i)
						{
						case 0:
							com = CLP[i];
							break;
						case 1:
							par1 = CLP[i];
							break;
						case 2:
							par2 = CLP[i];
							break;
						case 3:
							par3 = CLP[i];
							break;
						case 4:
							System.out.println("Error, too many arguments detected, exiting program.");
							System.exit(-1);
						}
					}
			}
			else
			{
				System.out.println("Please input a command");
				String commands = scan.readLine();
				if (commands == null)
				{
					System.out.println("Error detected, string holds no values, please relaunch and try again");
					System.exit(-1);
				}
				String temp = "";
				int whtSpc = 0;
				for (int i = 0; i < commands.length(); i++)
				{
					if (commands.charAt(i) == ' ')
					{
						switch(whtSpc)
						{
							case 0:
								com = temp;
								break;
							case 1:
								par1 = temp;
								break;
							case 2:
								par2 = temp;
								break;
							case 3:
								par3 = temp;
								break;
						}
						whtSpc++;
						temp = "";
					}
					else
					{
						if(commands.charAt(i) != '[' || commands.charAt(i) != ']')
							temp = temp + commands.charAt(i);
					}
					if (i == commands.length() - 1)
					{
						switch(whtSpc)
						{
							case 0:
								com = temp;
								break;
							case 1:
								par1 = temp;
								break;
							case 2:
								par2 = temp;
								break;
							case 3:
								par3 = temp;
								break;
						}
						temp = "";
					}
				}
			}
			switch(com)
				{
					case "-h":
						if (CLONum > 4)
						{
							System.out.println("Too many parameters for this type of instruction, system exiting");
							System.exit(-1);
						}
						connect(par1, par2, par3);
					case "-s":
						if (CLONum > 3)
						{
							System.out.println("Too many parameters for this type of instruction, system exiting");
							System.exit(-1);
						}
						server(par1, par2);
					case "-a":
						if (CLONum > 4)
						{
							System.out.println("Too many parameters for this type of instruction, system exiting");
							System.exit(-1);
						}
						auto(par1, par2, par3);
					case "-help":
						if (CLONum > 1)
						{
							System.out.println("Too many parameters for this type of instruction, system exiting");
							System.exit(-1);
						}
						help();
				}
			if (!com.equals("-h") || !com.equals("-s") || com.equals("-a") || com.equals("-help"))
			{
				System.out.println("Could not recognize command " + "'" + com + "'" + " please type in '-help' for help.");
			}
		}
		catch(IOException e)
		{
			System.out.println("Issue reading command, please try again");
			if (!CLO)
				commands();
			else
				System.exit(-1);
		}
		if(!CLO)
			commands();
		else
			System.exit(-1);
	}
	
	private static String chat() {
		/*
		 *  This is a method that transports the clients to a "chat room"
		 *  I've designed it so that they cannot use any of the commands
		 *  and ruin the connection. I might add an "exit" command
		 */
		System.out.println("This is the chat room, to exit please type '-exit'");
		if (auto)
		{
			auto = false;
			autoHostIP = "";
			autoPortCom = "";
			autoPort = "";
		}
		String send;
		BufferedReader in;
		PrintWriter out;
		try {
			if (isClient)
			{
				in=new BufferedReader(new InputStreamReader(System.in));
				BufferedReader etc=new BufferedReader(new InputStreamReader(client.getInputStream()));
	       		out = new PrintWriter(client.getOutputStream(), true);
				while(isClient)
				{
					if (in.ready())
					{
					   	send = in.readLine();	 
					   	if (send.equals("STATUS"))
							System.out.println("You are connected to " + client.getInetAddress() + " on port " + client.getPort());
					   	else if (send.equals("-exit"))
					   		System.exit(0);
					   	else
					   		out.println(send);
					}
					if (etc.ready())
					{
						send = etc.readLine();
						if (send.equals("STATUS"))
							System.out.println("You are connected to " + client.getInetAddress() + " on port " + client.getPort());
					   	else if (send.equals("-exit"))
					   		System.exit(0);
					   		System.out.println("[REMOTE]:" + send);
					}
				}
			}
			if(isServ)
			{
				in=new BufferedReader(new InputStreamReader(client.getInputStream()));
	       		BufferedReader to = new BufferedReader(new InputStreamReader(System.in));
	       		out = new PrintWriter(client.getOutputStream(), true);
				while(isServ)
				{
					if (in.ready())
					{
						send=in.readLine();
						if (send.equals("-exit"))
					   		System.exit(0);
					   	else 
					   		System.out.println("[REMOTE]:" + send);
					}
					else if (to.ready())
					{
						send=to.readLine();
						if (send.equals("STATUS"))
							System.out.println("You are connected to " + client.getInetAddress() + " on port " + client.getLocalPort());
						else if (send.equals("-exit"))
					   		System.exit(0);
					   	else 
					   		out.println(send);
					}
				}
			}
		}
		catch(UnknownHostException e)
		{
			System.out.println("Issue with connection, please try again");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		catch(IOException e)
		{
			System.out.println("Issue with I/O, please use acceptable commands.");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Issue with inputs, try again with valid parameters.");
			System.exit(-1);
		}
		return null;
	}
	
	private static void connect(String hostIP, String portCom, String port) {
		/*
		 * 	The connect method is the one used when attempting to connect to
		 * 	the host of a server. When unsuccessful, it should be able to
		 * 	convey its failure to the user.
		 * 
		 * 	Inputs: Talk -h [hostname | IPaddress] [-p portnumber]
		 * 
		 *  Where port number is the port of the server.
		 */
		boolean IP = false;
		boolean portGiven = false;
		int portNum;
		if (!portCom.equals("-p") && !port.equals(""))
		{
			System.out.println("Error detected, " + portCom + " is an invalid operator, please input another command");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		if (hostIP.equals("-p"))
		{
			IP = false;
			if (!portCom.isEmpty())
				portGiven = true;
			port = portCom;
		}
		else if (portCom.equals("-p"))
		{
			IP = true;
			if (!port.isEmpty())
				portGiven = true;
		}
		if(portGiven == false)
			port = "12987";
		portNum = Integer.parseInt(port); // Changes the String port number into an int
		if (portNum > 65535 || portNum < 0)	// Tests to see if the port number is valid to begin with
		{
			System.out.println("Error detected, port number invalid. Please try another command");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		try
		{
			if(IP == false)
				hostIP = InetAddress.getLocalHost().toString();
			client = new Socket(hostIP, portNum);
			System.out.println("Established connection as client, enjoy your conversation!");
		}
		catch (UnknownHostException e)
		{
			System.out.println("Issue connecting to host, please ensure you've typed in the correct port number and IP/Host address.");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		catch (IOException e)
		{
			System.out.println("Issue connecting to host, please ensure you've typed in the correct port number and IP/Host address.");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		isClient = true;
		chat();
		
	}
	
	private static void server(String portCom, String port) {
		/*
		 *  The server method is the one in which the client takes the role
		 *  of the host. It should be constantly listening for connections
		 *  to your port. If the port isn't available, it should display:
		 *  "Server unable to listen on specified port."
		 *  
		 *  Inputs: Talk -s [-p portnumber]
		 */
		int portNum = 0;

		try {
			String IP = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (!portCom.equals("-p") && !port.equals(""))
		{
			System.out.println("Error detected, " + portCom + " is an invalid operator, please input another command");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		else if (portCom.equals("-p") && port.equals(""))
		{
			System.out.println("Error detected, expected port number after " + portCom);
			System.exit(-1);
		}
		else if (!portCom.equals("-p") && port.equals(""))
		{
			System.out.println("Error detected, invalid command");
			System.exit(-1);
		}
		if (port.isEmpty())
			port = "12987";
		try 
		{
		portNum = Integer.parseInt(port); // Changes the String port number into an int
		}
		catch(NumberFormatException e)
		{
			System.out.println("Issue with port given, likely included non-numerical character, please retry");
			System.exit(-1);
		}
		if (portNum > 65535 || portNum < 0)	// Tests to see if the port number is valid to begin with
		{
			System.out.println("Error detected, port number invalid. Please try another command");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		try 
		{
			server = new ServerSocket(portNum);
			System.out.println("Server listening for connections on port "+ portNum);
		}
		catch (IOException e)
		{
			if (auto)
			{
				connect(autoHostIP, autoPortCom, autoPort);
			}
			System.out.println("Could not listen for connections on port "+ portNum + ", please try again");
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		try
		{
			client = server.accept();
			System.out.println("Accepted connection from " + client.getInetAddress());
		}
		catch(IOException e)
		{
			System.out.println("Issue with accepting on port " + port);
			if(!CLO)
				commands();
			else
				System.exit(-1);
		}
		isServ = true;
		cPort = portNum;
		chat();
	}
	
	private static void auto(String hostIP, String portCom, String port) {
		/*
		 *  The auto method is one in which the computer automatically
		 *  queries its purpose. It will start as a client, trying to
		 *  communicate with the specified IP, but if the server is not
		 *  found, it will automatically switch to becoming a host on the
		 *  specified port number.
		 */
		auto = false;
		if (hostIP.equals("-p"))
		{
			if(portCom.isEmpty())
			{
				System.out.println("Expected port number after '-p' program will now exit.");
				System.exit(-1);
			}
			port = portCom;
			portCom = "-p";
			hostIP = "";
		}
		autoHostIP = hostIP;
		autoPortCom = portCom;
		autoPort = port;
		auto = true;
		server(portCom, port);
	}
	
	private static void help() {
		/*
		 *  The help method is what you would expect, a method that helps
		 *  the user understand how to use this program. It should print
		 *  the name of the program creator, and instructions on how to
		 *  use the program. (Presumably, just copy any paste the comments
		 *  outlined above.)
		 */
		System.out.println("You can do the following commands:");
		System.out.println("Talk -h [hostname | IPaddress] [-p portnumber]");
		System.out.println("Talk -s [-p portnumber]");
		System.out.println("Talk -a [hostname|IPaddress] [-p portnumber]");
		if (!CLO)
			commands();
	}
}