import java.net.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.io.*;
public class download extends Thread
{
InetAddress ip;
	short port;
	int file_index;
	String file_name;
	int file_size;
	int arg;
	download(int arg){
//		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<INSIDE DOWNLOAD>>>>>>>>>>>>>>>>>>>>>>>");
		ip=Connection.list_IP.get(arg);
		port=Connection.list_port.get(arg);
		file_index=Connection.list_file_index.get(arg);
		file_name=Connection.list_file_name.get(arg);
		file_size=Connection.list_file_size.get(arg);
		this.arg=arg;
		start();
}
public void run(){
try {
Socket client1 = new Socket(ip,port);
ObjectOutputStream out = new ObjectOutputStream(client1.getOutputStream());
ObjectInputStream in = new ObjectInputStream(client1.getInputStream());
String get="GET /get/"+file_index+"/"+file_name+" HTTP/1.1\r\nUser-Agent: Simpella\r\nHost: "+ip.toString()+":"+port+"\r\nConnection: Keep-Alive\r\nRange: bytes=0-\r\n\r\n";
System.out.println("Inp frm Client="+get);
out.writeObject(get);
String msg=(String)in.readObject();
System.out.println("Input from Server:"+msg);
if(msg.startsWith("HTTP/1.1 200 OK")){
System.out.println("****************Starting Downloading File:"+file_name+"********************");

//Database.download.add(r);
//

File file=new File(client.sharedDirectory+"\\"+file_name);
try {
	file.createNewFile();
} catch (Exception e) {
	System.out.println("download:Unable to create file");
}

BufferedInputStream inp=new BufferedInputStream(client1.getInputStream(),1024);

BufferedOutputStream outp=new BufferedOutputStream(new FileOutputStream(file),1024);


byte[] b=new byte[1024];
long l=0;
int i=0;
Connection.download.put(arg, l);
while(inp.read(b,0,1024)>-1)
{
	outp.write(b,0, b.length);
	l=l+1024;
	Connection.download.put(arg, l);

}
System.out.println("**************Download Complete*****************");
inp.close();
outp.flush();
outp.close();
//this.stop();
}
else if(msg.startsWith("HTTP/1.1 503 File not found")){
System.out.println("download: File not found.");
}
}
 catch (UnknownHostException e) {
e.printStackTrace();
} catch (Exception e) {
e.printStackTrace();
}
}
}

