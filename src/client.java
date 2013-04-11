import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.File;


public class client extends Thread{
int portNo;
int hostPort=6576;
public static int noOfConn=0;
public static String sharedDirectory=System.getProperty("user.dir");
int numOfFiles=0;
long sizeOfFiles=0;
BufferedReader inp=null;
String userInput=null;
Socket[] clientSideSocket=new Socket[4];
ObjectOutputStream[] outToServer=new ObjectOutputStream[4];
ObjectInputStream[] inFromServer=new ObjectInputStream[4];
//Connection objConnection= new Connection();
//List<Connection> ConnectionList =new ArrayList<Connection>();
public static int prev_size, aftr_size;
ArrayList<Integer> downloadArg=new ArrayList<Integer>();
	client(){
		
	}
	client(int portNo){
		this.portNo=portNo;
		//objConnection.setPort(6346);
	}
	public void run() {
		//System.out.println("Client Module Started...Enter User Commands");
		//System.out.println(System.getProperty("os.name"));
		try {
			System.out.println("Local IP: "+InetAddress.getByAddress(simpella.connectIP));
		} catch (UnknownHostException e1) {
			System.out.println("Cannot resolve localhost name...exiting");
			System.exit(1);
		}
		System.out.println("Simpella Net Port: "+simpella.serverPortNo);
		System.out.println("Downloading Port: "+simpella.downloadPortNo);
		System.out.println("simpella version 0.6 (c) 2002-2003 XYZ");
		inp=new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
						
			try {
				userInput = inp.readLine();
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println("Invalid Input!!...Exiting");
				//System.exit(1);
			}
			if(userInput.equalsIgnoreCase("quit") || userInput.equalsIgnoreCase("bye"))
				{
					System.out.println("Exiting....");
					System.exit(1);
				}
			else if(userInput.startsWith("info"))
			{
				String[] token=userInput.split(" ");
				if(token.length==2)
					info(token[1]);
				else
					System.out.println("Usage: info [cdhnqs]");
			}
			else if(userInput.startsWith("share"))
			{
				String[] token=userInput.split(" ");
				String argument="";
				for(int i=1;i<token.length;i++)
					argument=argument + token[i] +" ";
				//System.out.println(argument+"len="+argument.length());
				share(argument);
				
			}
				
			else if(userInput.startsWith("open")){
				// Insert validation for input string
				String[] token=userInput.split(" ");
				if(client.noOfConn<3)
				open(token[1]);
				else
					System.out.println("Client:SIMPELLA/0.6 503 Maximum number of connections reached. Sorry!\r\n");
			}
			
			else if(userInput.equals("scan")){
				scan();
			}
			else if(userInput.startsWith("download")){
				String[] token=userInput.split(" ");
				try {
					download(Integer.parseInt(token[1]));
				} catch (Exception e) {
					System.out.println("Usage: download <number>");
					//e.printStackTrace();
				}
			}
			else if(userInput.equals("monitor")){
				try {
					monitor();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(userInput.startsWith("clear")){
				String[] token=userInput.split(" ");
				//System.out.println("No of arguments="+token.length);
				if(token.length==1)
					clear(-1);
				else
					clear(Integer.parseInt(token[1]));
			}
			else if(userInput.equals("list")){
				try {
					int k=Connection.list_port.size();
					list1(0, k);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(userInput.startsWith("find")){
				String[] token=userInput.split(" ");
				String argument="";
				for(int i=1;i<token.length;i++)
					argument=argument + token[i] +" ";
				//System.out.println(argument+"len="+argument.length());
				try {
					find(argument);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(userInput.equals("update")){
				try {
					update();
				} catch (IOException e) {
					System.out.println("Client:Error calling update");
					e.printStackTrace();
				}
			}
			else
				System.out.println("Invalid Command");
		}
	}
	void download(int arg){
		//arg--;
		if(((arg-1)>=0 && (arg-1)<Connection.list_IP.size()) && Connection.list_IP.size()!=0)
		{
			System.out.println("Initiating download...please wait");
			int proceed=1;
			if(downloadArg.contains(arg-1))
			{
				long prev=Connection.download.get(arg-1);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long aftr=Connection.download.get(arg-1);
				if(prev==aftr)
					{
						proceed=1;
						downloadArg.remove(arg-1);
						
					}
				
				else
					{
						System.out.println("download: You are already downloading this file");
						proceed=0;
					}
			}
			else if(proceed==1){
			downloadArg.add(arg-1);
			new download(arg-1);
			}
		}
		else if(Connection.list_IP.size()==0)
			System.out.println("Download: Empty List!!!");
		else
			System.out.println("Usage: download <1-"+Connection.list_IP.size()+">");
	}
	void info(String arg) {
		if(arg.equals("c"))
		{
			System.out.println("CONNECTION STATS:");
			System.out.println("-----------------");
			int i,k=1;
			for(i=0;i<Connection.incomingConnection.length;i++)
			{
				if( !(Connection.incomingConnection[i]==null))
				{
					System.out.println(k+")"+Connection.incomingConnection[i].getInetAddress()+":"+Connection.incomingConnection[i].getPort()+"\tPacks: "+Connection.incomingConnPackSent[i]+"/"+Connection.incomingConnPackRecv[i]+"\tBytes: "+Connection.incomingConnPackSentSize[i]+"/"+Connection.incomingConnPackRecvSize[i]);
					k++;
				}
			}
			for(i=0;i<Connection.outgoingConnection.length;i++)
			{
				if( !(Connection.outgoingConnection[i]==null))
				{
					System.out.println(k+")"+Connection.outgoingConnection[i].getInetAddress()+":"+Connection.outgoingConnection[i].getPort()+"\tPacks: "+Connection.outgoingConnPackSent[i]+"/"+Connection.outgoingConnPackRecv[i]+"\tBytes: "+Connection.outgoingConnPackSentSize[i]+"/"+Connection.outgoingConnPackRecvSize[i]);
					k++;
				}
			}
		}else if(arg.equals("d")){
			System.out.println("DOWNLOAD STATS: Please wait");
			System.out.println("---------------");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int k=0;
			int i;
			for(i=0;i<downloadArg.size();i++)
				{
					k=downloadArg.get(i);
					System.out.println((i+1)+") "+Connection.list_IP.get(k)+":"+Connection.list_port.get(k)+"\t\t"+(Connection.download.get(k)*100/Connection.list_file_size.get(k))+"%\t\t"+(Connection.download.get(k))+"/"+Connection.list_file_size.get(k));
					System.out.println("Name: "+Connection.list_file_name.get(k));
					//k=downloadArg.size();
				}
			
		}else if(arg.equals("h")){
			System.out.println("HOST STATS: Please wait");
			System.out.println("-----------");
			Connection.files_on_network=0;
			Connection.bytes_on_network=0;
			try {
				update();
			} catch (IOException e) {
				System.out.println("Client:Error calling update");
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Hosts: "+Connection.hostConnectedTo.size()+"  Files: "+Connection.files_on_network+"  Size(in KB):"+Connection.bytes_on_network);
		}else if(arg.equals("n")){
			System.out.println("NET STATS:");
			System.out.println("----------");
			int i;
			int msg_rec=0;
			int msg_sent=0;
			int byte_rec=0;
			int byte_send=0;
			for(i=0;i<Connection.incomingConnection.length;i++)
			{
				if( !(Connection.incomingConnection[i]==null))
				{
					msg_rec+=Connection.incomingConnPackRecv[i];
					msg_sent+=Connection.incomingConnPackSent[i];
					byte_rec+=Connection.incomingConnPackRecvSize[i];
					byte_send+=Connection.incomingConnPackSentSize[i];
				}
			}
			for(i=0;i<Connection.outgoingConnection.length;i++)
			{
				if( !(Connection.outgoingConnection[i]==null))
				{
					msg_rec+=Connection.outgoingConnPackRecv[i];
					msg_sent+=Connection.outgoingConnPackSent[i];
					byte_rec+=Connection.outgoingConnPackRecvSize[i];
					byte_send+=Connection.outgoingConnPackSentSize[i];
				}
			}
			System.out.println("Msg Received: "+ msg_rec+"  Msg Sent: "+msg_sent);
			System.out.println("Unique GUIDs in memory: "+Connection.objMap.size());
			System.out.println("Bytes Rcvd: "+ byte_rec+"  Bytes Sent: "+byte_send);
		}else if(arg.equals("q")){
			System.out.println("QUERY STATS:");
			System.out.println("------------");
			System.out.println("Queries Received: "+Connection.no_of_queries+"        "+"Responses Sent: "+Connection.no_of_replies);
		}else if(arg.equals("s")){
			System.out.println("SHARE STATS:");
			System.out.println("------------");
			System.out.println("Num Shared: "+Utilities.find_noOffiles(client.sharedDirectory)+"        "+"Size Shared(in bytes): "+Utilities.find_sizeOFfiles(client.sharedDirectory));
		}else
			System.out.println("Invalid flag...Usage: info [cdhnqs]");
		
	}
	void monitor() throws IOException
	{
		System.out.println("MONITORING SIMPELLA NETWORK:");
		System.out.println("Press enter to continue");
		System.out.println("----------------------------");
		BufferedReader obj=new BufferedReader(new InputStreamReader(System.in));
		obj.readLine();
		for(int i=0;i<Connection.queries.size();i++)
			System.out.println("Search: '"+Connection.queries.get(i)+"'");
	}
	void clear(int arg)
	{
		if(arg==-1)
		{
			System.out.println("Clearing Entire LIST");
			Connection.list_file_name.clear();
			Connection.list_file_size.clear();
			Connection.list_IP.clear();
			Connection.list_port.clear();
			Connection.list_files.clear();
		}else
		{
			System.out.println("Cleared Entry '"+arg+"'");
			Connection.list_file_name.remove(arg-1);
			Connection.list_file_size.remove(arg-1);
			Connection.list_IP.remove(arg-1);
			Connection.list_port.remove(arg-1);
			Connection.list_file_index.remove(arg-1);
			//
		}
	}
void scan()
{
	System.out.println("Scanning "+client.sharedDirectory+" for files ...");
	File[] filelist = new File(client.sharedDirectory).listFiles();
	sizeOfFiles=0;
	numOfFiles=0;
	for (File file : filelist) {
		if (file.isFile()) {
			sizeOfFiles += file.length();
			numOfFiles++;
		}
	}
	System.out.println("Scanned "+numOfFiles+" files and "+sizeOfFiles+" bytes");
}
void share(String argument) {
	if(argument.startsWith("-i") && argument.length()==3)	
		System.out.println("Sharing "+client.sharedDirectory);
	else if(argument.startsWith("dir"))
		{
			String[] token=argument.split(" ");
			String dir="";
			for(int i=1;i<token.length;i++)
				dir=dir + token[i] +" ";
			if(token[1].startsWith("/"))
				sharedDirectory=sharedDirectory + "\\" + dir;
				// FOR UNIX sharedDirectory=token[1]
			else
				sharedDirectory=dir;
				//FOR UNIX sharedDirectory=sharedDirectory + "/" + dir;
			sharedDirectory=sharedDirectory.substring(0, sharedDirectory.length()-1);
		}
	else
		System.out.println("Usage: share [dir|-i] <dir>");
	
	//System.out.println(sharedDirectory);
	}
void open(String nameNport) {
	// Insert validation for parsing input string
	String[] token=nameNport.split(":");
	String host=token[0];
	int port=Integer.parseInt(token[1]);
	int proceedFlag=1;
	Iterator<Socket> iterator=Connection.connections.iterator();
	if(host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1"))
		host=simpella.infoSocket.getLocalAddress().getHostAddress();
	if((host.equalsIgnoreCase(simpella.infoSocket.getLocalAddress().getHostAddress())||host.equalsIgnoreCase(simpella.infoSocket.getLocalAddress().getCanonicalHostName()))&&(simpella.serverPortNo==port || simpella.downloadPortNo==port)){
		proceedFlag=0;
		System.out.println("Client: Self Connect not allowed");
		}
	while(iterator.hasNext() && proceedFlag==1)
	{
		Socket sock=(Socket)iterator.next();
		
		if((host.equalsIgnoreCase(sock.getInetAddress().getHostAddress()) || host.equalsIgnoreCase(sock.getInetAddress().getCanonicalHostName())) && port==sock.getPort()){
			proceedFlag=0; 
			System.out.println("Client: Duplicate connection to same IP/port not allowed");
			break;
		}
		
	}
	if(proceedFlag==1)
		{
	
	
	byte type=04;
	Message msg=new Message(type);
	try {
		Connection.outgoingConnPackRecv[noOfConn]=0;
		Connection.outgoingConnPackSent[noOfConn]=0;
		Connection.outgoingConnPackSentSize[noOfConn]=0;
		Connection.outgoingConnPackRecvSize[noOfConn]=0;
		
		clientSideSocket[noOfConn]=new Socket(host,port);
		System.out.println("Client:TCP Connection established...Begin handshake");
		outToServer[noOfConn]=new ObjectOutputStream(clientSideSocket[noOfConn].getOutputStream());
		inFromServer[noOfConn]=new ObjectInputStream(clientSideSocket[noOfConn].getInputStream());
		String strToServer="SIMPELLA CONNECT/0.6\r\n";
		byte[] byteArray= strToServer.getBytes("UTF-16LE");
		msg.setPayload(byteArray);
     	
		System.out.println("Client:"+new String(byteArray));
		//outToServer.writeUTF("SIMPELLA CONNECT/0.6\r\n");
		Connection.outgoingConnPackSent[noOfConn]++;
		Connection.outgoingConnPackSentSize[noOfConn]+=byteArray.length+23;
		outToServer[noOfConn].writeObject((Object)msg);
		Connection.outgoingConnPackRecv[noOfConn]++;
		
		Message msg1=(Message) inFromServer[noOfConn].readObject();
		byte[] fromServer=msg1.getPayload();
		Connection.outgoingConnPackRecvSize[noOfConn]+=fromServer.length+23;
		String strFromServer=new String(fromServer);
		System.out.println("Server:"+strFromServer);
		if(msg1.getMessage_type()==05)
			{
			strToServer="SIMPELLA/0.6 200 thank you for accepting me\r\n";
			byte[] byteArray1= strToServer.getBytes("UTF-16LE");
			Message m=new Message((byte)05);
			m.setPayload(byteArray1);
			Connection.outgoingConnPackSent[noOfConn]++;
			Connection.outgoingConnPackSentSize[noOfConn]+=byteArray1.length+23;
			outToServer[noOfConn].writeObject((Object)m);
			
			Connection.connections.add(clientSideSocket[client.noOfConn]);
			Connection.outgoingConnection[client.noOfConn]=clientSideSocket[client.noOfConn];
			Connection.clientOutStream[client.noOfConn]=outToServer[client.noOfConn];
			new clientSocketListen(clientSideSocket[noOfConn],outToServer[noOfConn],inFromServer[noOfConn]);
			update();
			noOfConn++;
			//System.out.println("Client:num of conn="+noOfConn);
			}
		else
			{
				System.out.println("<open>:Cannot open connection to "+host+" at this time");
				clientSideSocket[noOfConn].close();
				inFromServer[noOfConn].close();
				outToServer[noOfConn].close();
//				System.out.println("Client:num of conn="+noOfConn);
			}
		
		
	} catch (UnknownHostException e) {
		System.out.println("Unknown Host: Destination host unreachable");
		//e.printStackTrace();
	} catch (Exception e) {
		System.out.println("Connection Refused/Destination host unreachable");
	}
		}
	}

void update() throws IOException{
	Ping objPing=new Ping();
	byte type=(byte)0x00;
	Message msg= new Message(type);
	Connection.generatedMsgId.add(new String(msg.getMessage_id()));
	
	objPing.PingAll(1,msg,clientSideSocket,outToServer);
	objPing.PingAll(2,msg,Connection.incomingConnection,Connection.outStream);
}

void find(String argument) throws IOException{
	//System.out.println("argument="+argument);
	Query query=new Query(argument);
	byte type=(byte) 0x80;
	Message msg= new Message(type);
	Connection.generatedMsgId.add(new String(msg.getMessage_id()));
	int prev_size=Connection.list_files.size();
	query.QueryAll(1,msg,clientSideSocket, outToServer);
	query.QueryAll(2,msg,Connection.incomingConnection, Connection.outStream);
	System.out.println("searching Simpella Network for '"+argument+"'....Please wait");
	//int flg=0;
	//System.out.println("SIZE OF LIST====="+Connection.list_port.size());
	
	try {
		Thread.sleep(3000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	int aftr_size=Connection.list_files.size();
	if(prev_size == aftr_size)
		System.out.println("find: No match found");
	else
		{
			client.prev_size=Connection.list_IP.size();
			list(prev_size,aftr_size);
			client.aftr_size=Connection.list_IP.size();
			list1(client.prev_size,client.aftr_size);
		}
}
void list1(int prev_size,int aftr_size) throws IOException
{
	BufferedReader obj=new BufferedReader(new InputStreamReader(System.in));
	int i,k;
	k=Connection.list_port.size();
	for(i=0;i<k;i++)
	{
		if(i%10==0)
		{
			//System.out.println();
			System.out.println("\npress enter to continue");
			System.out.println((aftr_size-prev_size)+" responses received\n");
			obj.readLine();
		}
		System.out.println((i+1)+")  "+Connection.list_IP.get(i)+":"+Connection.list_port.get(i)+"\t\t"+"Size: "+Connection.list_file_size.get(i));
		System.out.println("Name: "+Connection.list_file_name.get(i));
		k=Connection.list_port.size();
		aftr_size=k;
	}
}
void list(int prev_size,int aftr_size) throws IOException{
	
	int i,j,k;
	for(int p=prev_size;p<aftr_size;p++)
	{
		byte[] a=Connection.list_files.get(p);
		Byte numOfHits=ByteBuffer.wrap(a, 0, 1).get();
		short port=ByteBuffer.wrap(a, 1, 2).getShort();
		InetAddress ip=InetAddress.getByAddress(Arrays.copyOfRange(a, 3, 7));
		for(i=0;i<numOfHits;i++)
			{
//				String s=""+numOfHits+","+ByteBuffer.wrap(a, 1, 2).getShort()+","+InetAddress.getByAddress(Arrays.copyOfRange(a, 3, 7));
				Connection.list_port.add(port);
				Connection.list_IP.add(ip);
			}
		
		int flag=0;
		byte[] rs=Arrays.copyOfRange(a, 11, a.length-11);
		//*********************
		i=0;
		
		while(i<rs.length)
		{
			Connection.list_file_index.add(ByteBuffer.wrap(rs,i,4).getInt());
			
			i=i+4;
			Connection.list_file_size.add(ByteBuffer.wrap(rs,i,4).getInt());
			i=i+4;
			k=i;
			byte name[]=new byte[4096];
			j=0;
			while(rs[k]!=(byte)'\0')
			{
				name[j]=rs[k];
				j++;
				k++;
			}
			Connection.list_file_name.add(new String(name));
			flag++;
			i=k+1;
			if(flag==numOfHits)
				break;
		}
		//********************
		aftr_size=Connection.list_files.size();
	}
	
}
}

