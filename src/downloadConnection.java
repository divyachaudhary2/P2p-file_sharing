import java.io.*;
import java.net.*;

class downloadConnection extends Thread {
protected Socket clientsocket;
public downloadConnection(Socket s){
//	System.out.println("<<<<<<<<<<DOWNLOAD THREAD>>>>>>>>");
clientsocket=s;
start();
}
public void run(){
try {
//System.out.println("Dwonload Incomming Connection from IP: " + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
ObjectInputStream in = new ObjectInputStream(clientsocket.getInputStream());
ObjectOutputStream out = new ObjectOutputStream(clientsocket.getOutputStream());
while (true) {
Object b=in.readObject();

String clientMessage = (String)b;
System.out.println("Download Server: Input from Client");
System.out.println(clientMessage);
if(clientMessage.startsWith("GET")){
String filename=clientMessage.substring(clientMessage.indexOf("/", 9+1)+1, clientMessage.indexOf("HTTP/1.1")-2);
//System.out.println("File is :"+filename+":");
String fullpath=client.sharedDirectory + "\\"+filename;
//System.out.println("Full path:"+fullpath+":");
File file=new File(fullpath);
if(file.exists()&&file.isFile()){
String outToClient=""+"HTTP/1.1 200 OK\r\nServer: Simpella0.6\r\nContent-type: application/binary\r\nContent-length: "+file.length()+"\r\n\r\n";
out.writeObject(outToClient);
System.out.println("Download Server: Begin Download");
BufferedInputStream inpFromFile=new BufferedInputStream(new FileInputStream(file),1024);
BufferedOutputStream outToSock=new BufferedOutputStream(clientsocket.getOutputStream(),1024);
byte[] data = new byte[1024];

while(inpFromFile.read(data,0,1024)>-1)
{
	outToSock.write(data, 0, data.length);
}
System.out.println("Download SERVER= Transfer Complete");
inpFromFile.close();
outToSock.flush();
outToSock.close();
break;
}
else{
out.writeObject(new String("HTTP/1.1 503 File not found.\r\n\r\n"));
System.out.println("Rejected Connection: File not found");
break;
}
}
}
}catch (EOFException e){
System.out.println("Disconnected to IP: " + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
//this.stop();
} 
catch (IOException e) {
System.out.println("Disconnected to IP: " + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
//this.stop();
}
catch (Exception e) {
System.out.println("Disconnected to IP: " + clientsocket.getInetAddress().getHostAddress()+ " PORT: "+clientsocket.getPort());
//this.stop();
}
}
}
