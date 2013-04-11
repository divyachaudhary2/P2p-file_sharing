import java.io.*;
import java.net.*;
import java.util.*;

public class simpella {
	public static int serverPortNo;
	public static int downloadPortNo;
	public static byte[] connectIP;
	public static Socket infoSocket=null;
	public static void main(String[] args) throws IOException{
		try {
			serverPortNo=Integer.parseInt(args[0]);
		} catch (Exception e) {
			serverPortNo=6346;
			System.out.println("<port1> defaulted to 6346");
		}
		try {
			downloadPortNo=Integer.parseInt(args[1]);
		} catch (Exception e) {
			downloadPortNo=5635;
			System.out.println("<port2> defaulted to 5635");
		}
		//System.out.println("TCP Server Started");
		infoSocket=new Socket("8.8.8.8",53);
		connectIP=infoSocket.getLocalAddress().getAddress();
		//infoSocket.close();
		
		Thread c1=new client(serverPortNo);
		c1.start();
		
		Thread c2=new incomingDownload(downloadPortNo);
		c2.start();
		ServerSocket tcpWelcomeSocket=new ServerSocket(serverPortNo);
		//ServerSocket downloadSocket=new ServerSocket(downloadPortNo);
		while(true){
			new incomingConnection(tcpWelcomeSocket.accept());
			//new downloadConnection(downloadSocket.accept());
			}

	}

}
