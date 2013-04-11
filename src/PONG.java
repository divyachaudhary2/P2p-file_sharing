import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;


public class PONG extends Message {

	PONG(){
		
	}
	
	public  byte[] createPayload(Socket clientSocket){

		try{
		ByteBuffer payloadBuffer = ByteBuffer.allocate(14);
		//2 bytes port
		short port=(short)simpella.serverPortNo;
		//4 bytes IP
		byte[] IP=simpella.connectIP;
		//4bytes no of files
		int file_shared=Utilities.find_noOffiles(client.sharedDirectory);
		//4 bytes no of KB
		float kilobytes_shared=Utilities.find_sizeOFfiles(client.sharedDirectory)/1024;
		// write the port, big endian
//		System.out.println("port="+port+" IP="+InetAddress.getByAddress(IP)+" files shared=" + file_shared +" file size="+ kilobytes_shared);
		payloadBuffer.putShort(port);
		payloadBuffer.put(IP);
		payloadBuffer.putInt(file_shared);
		payloadBuffer.putFloat(kilobytes_shared);
		byte[] payload=payloadBuffer.array();
		return (payload);
		
		}catch(Exception e){
			return(null);
		}
			
	}

	public void PongAll(byte[]id,Socket clientSocket,ObjectOutputStream objOutputStream ) throws IOException{
//		System.out.println("Send pong");
		byte type=(byte)0x01;
		Message msg=new Message(id,type);
		msg.setpayloadLength(14);
		byte[] payload=createPayload(clientSocket);
		msg.setPayload(payload);
		
		objOutputStream.writeObject((Object)msg);
//		System.out.println("pong send");
		
	}
}

