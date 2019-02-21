//package lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

/**
 * Author: Niya Jaison	| UTA ID:1001562701	|Net ID: nxj2701
 * The WebClient is the client class.This is a single threaded class.
 * The class contains - Client Server connection initiation, displaying the server output
 * 						and displaying the connection parameter
 * References: 	1. https://github.com/aastha248/Client-Server-communication-via-sockets
 *				2. https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 * */
public class WebClient {
	/**
	 * @author Niya Jaison	| UTA ID: 1001562701
	 * Member Variable list:
	 * clientSocket		- A socket object for establishing the connection with server
	 * urlClient			- A URL object to hold the URL passed by the client
	 * RESPONSELINEEND	- A final variable that holds the line feeds for HTTP message format
	 */
	public static Socket clientSocket;
	public static URL urlClient;
	final static String RESPONSELINEEND="\r\n";

	public static void main(String[] args) throws Exception {
		/**The inputs are provided via console in the order <server IP address> <port number> <Required File name with extension>*/
		BufferedReader consoleInBuffer = new BufferedReader(new InputStreamReader(System.in)); /**A BufferedReader for reading the console input*/
		String host;
		try {
			host = consoleInBuffer.readLine();
			Integer port =Integer.parseInt(consoleInBuffer.readLine());
			String requiredFile=consoleInBuffer.readLine();
			if(port==0) {/**If port number is skipped port number is set to 1044*/
				port=1044;
			}
			String url="http:/"+host+":"+port+"/"+requiredFile;
			System.out.println(url);
			urlClient = new URL(url);
			long startTime=System.currentTimeMillis();/**Storing the system time at the begining of socket creation so as to calculate the RTT*/
			clientSocket=new Socket(host, port);/**Creating a socket with the host and port*/
			PrintStream requestToServer = new PrintStream(clientSocket.getOutputStream());/**A PrintStream object to send the request to the server */
			//System.out.println("here");
			requestToServer.println("GET /"+requiredFile+" HTTP/1.1"+RESPONSELINEEND);/**Sending the request to server*/

			BufferedReader responseFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));/**A BufferedReader object for reading the response from the server.*/
			long endTime=System.currentTimeMillis();/**Storing the system time at the end of response reception so as to calculate the RTT*/
			System.out.println("Response status:"+responseFromServer.readLine());
			System.out.println("Content type:"+responseFromServer.readLine());

			System.out.println("Requested File:");
			String line = "";
			while ((line = responseFromServer.readLine()) != null) {/**Displaying the server response/the content of the requested file*/
				System.out.println(line);
			}

			System.out.println("********************************************************");
			System.out.println("Round Trip Time: "+(endTime-startTime)+" milli second");/**RTT calculated*/
			
			printConnectionParameter(urlClient);/**Calling the userdefined function to display the connection parameter.*/
			clientSocket.close();
			
		} catch (SocketException e) {
			// TODO: handle exception
			System.out.println(e.getLocalizedMessage());
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * @author Niya Jaison	|UTA ID: 1001562701
	 * Method	: User defined method used to print the connection parameters 
	 * Input		: A URL object which holds the URL passed by the client
	 * Output	: void
	 **/
	
	public static void printConnectionParameter(URL clientUrl) throws Exception {
		System.out.println("Connection Parameters...");
		System.out.println("Host name 	:"+clientSocket.getInetAddress().getHostName() );
		System.out.println("Port 		:"+clientSocket.getLocalPort());
		System.out.println("Protocol 	:"+clientUrl.getProtocol());
		System.out.println("Timeout		:"+clientSocket.getSoTimeout());
		System.out.println("Peer name	:"+clientSocket.getLocalSocketAddress());	

		
	}
}
