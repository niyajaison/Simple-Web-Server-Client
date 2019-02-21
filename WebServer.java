//package lab1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.StringTokenizer;

/***
 * 
 * @author Niya Jaison	| UTA ID: 1001562701		| Net ID: nxj2701
 * The WebServer class is used for the Thread creation, creation of Server socket  and starting the thread.
 * References: 	1. https://github.com/aastha248/Client-Server-communication-via-sockets
 *				2. https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 *				3. https://elearn.uta.edu/bbcswebdav/pid-6173932-dt-content-rid-58916972_2/courses/2178-COMPUTER-NETWORKS-91812-001/Programming%20Assignment%201_reference_Java.pdf
 */

public class WebServer {

	private static ServerSocket listenSocket;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader serverPort = new BufferedReader(new InputStreamReader(System.in));
		
		int portNum = Integer.parseInt(serverPort.readLine());
		listenSocket = new ServerSocket(portNum); /**Initailizing the server socket*/
		//System.out.println(listenSocket.getLocalSocketAddress());		
		//System.out.println("Socket Address:"+listenSocket.getLocalSocketAddress());
		//ServerSocket listenSocket;
		while(true) {
			Socket connectionSocket= listenSocket.accept(); /**Accepting the connection request from th client*/
			//System.out.println(listenSocket.getLocalPort());
			HttpRequest httpRequest=new HttpRequest(connectionSocket);/**Creating an object of the class to process the HTTP request*/
			Thread requestThread=new Thread(httpRequest);/**Creating a Thread for each of the new connection*/
			requestThread.start();		/**starting the thread*/	
		}
	}

}
class HttpRequest implements Runnable{
	final static String RESPONSELINEEND="\r\n";
	Socket connectionSocket;
	public HttpRequest(Socket connectSocket) {
		// TODO Auto-generated constructor stub
		this.connectionSocket=connectSocket;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			processHttpRequest();
			connectionSocket.close();
		} catch (Exception e) {
			// TODO: handle exception		
			//e.printStackTrace();
		}

	}
	public void processHttpRequest() throws Exception {
	String url;
		if(connectionSocket.getPort()==0){
		 url = "http://localhost:1044/";
		}
		else{
			 url = "http://localhost:"+connectionSocket.getPort()+"/";/***/
		}	
		URL urlServerPort=new URL(url);
		BufferedReader clientRequestBuffer =new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		DataOutputStream serverResponse = new DataOutputStream(connectionSocket.getOutputStream());
		String clientRequestLine=clientRequestBuffer.readLine();

		System.out.println("\nRequest Line from client:	"+clientRequestLine );
		String headerLine = null;
		while((headerLine=clientRequestBuffer.readLine()).length()!=0) {
			System.out.println(headerLine);
		}
		//System.out.println("here");
		StringTokenizer stringTokens = new StringTokenizer(clientRequestLine);
		stringTokens.nextToken();
		String requestedFile='.'+stringTokens.nextToken();
		FileInputStream fileInputStream=null;
		int fileExist=0;
		try {
			System.out.println("Requested file: "+requestedFile);
			fileInputStream= new FileInputStream(requestedFile);
			//System.out.println("filefound");
		} catch (FileNotFoundException fileNotFoundException) {
			// TODO: handle exception
			//System.out.println("file not found");
			fileExist=-1;
		}

		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExist==0) {
			statusLine = "HTTP/1.1 200 OK: ";
			contentTypeLine = "Content‐type: "+contentType(requestedFile) + RESPONSELINEEND;
		} else {
			statusLine = "HTTP/1.1 404 Not Found:";
			contentTypeLine = "Content-Type: text/html" + RESPONSELINEEND;
			entityBody = "<HTML>" +
					"<HEAD><TITLE> File Not Found</TITLE></HEAD>" +
					"<BODY>Sorry the requested File is Not Found</BODY></HTML>";
		}
		serverResponse.writeBytes(statusLine);
		serverResponse.writeBytes(RESPONSELINEEND);
		serverResponse.writeBytes(contentTypeLine);
		serverResponse.writeBytes(RESPONSELINEEND);
		if (fileExist==0) {
			sendBytes(fileInputStream,serverResponse); 
		} else {
			serverResponse.write(entityBody.getBytes());
			
		}	
		printConnectionParameter(urlServerPort);
		serverResponse.close();
		clientRequestBuffer.close();

	}
	private static void sendBytes(FileInputStream fin,DataOutputStream dataOS) throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
	     byte[] buffer = new byte[1024];
	     int bytes = 0;
	     // Copy requested file into the socket's output stream.
	     while((bytes = fin.read(buffer)) != -1 ) {
	        dataOS.write(buffer, 0, bytes);
	     }
	}
	private static String contentType(String requestedFileName) {
		String contentType="";
		if(requestedFileName.endsWith(".htm")||requestedFileName.endsWith(".html")) {
			contentType= "text/html";
		}
		else if(requestedFileName.endsWith(".jpg")||requestedFileName.endsWith(".jpeg")) {
			contentType= "text/jpg";
		}
		return contentType;
	}
	/**
	 * @author Niya Jaison	|UTA ID: 1001562701
	 * Method	: User defined method used to print the connection parameters 
	 * Input		: A URL object which holds the URL passed by the client
	 * Output	: void
	 **/
	public void printConnectionParameter(URL serverUrl) throws Exception {
		System.out.println("********************************************************");
		System.out.println("Connection Parameters...");
		System.out.println("Host name 	:"+connectionSocket.getInetAddress().getHostName() );
		System.out.println("Port 		:"+connectionSocket.getLocalPort());
		System.out.println("Protocol 	:"+serverUrl.getProtocol());
		System.out.println("Timeout		:"+connectionSocket.getSoTimeout());
		System.out.println("Peer name	:"+connectionSocket.getLocalSocketAddress());	

		
	}

}