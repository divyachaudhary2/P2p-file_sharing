import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class QUERY_HIT extends Message {
	byte number_hits;
	short port=(short)simpella.downloadPortNo;
	byte[] IP=simpella.connectIP;
	public static final int speed=10000;
	
	String servantId;
	ArrayList<Integer> file_index=new ArrayList<Integer>();
	ArrayList<Integer> file_size=new ArrayList<Integer>();
	ArrayList<String> file_name = new ArrayList<String>();
	int QUERY_HIT_PAYLOAD_LENGTH;
	QUERY_HIT(){
		
	}
	QUERY_HIT(byte[] id, byte num_hits, short port, byte[] IP){
		super(id, (byte)0x81);
		this.number_hits=num_hits;
		this.port=port;
		this.IP=IP;	
	}

	public  byte[] createPayload(String argument ){

		try{
			File[] filelist = new File(client.sharedDirectory).listFiles();
			number_hits=0;
	
			int i=0;
			for (File file : filelist) {
				if (file.isFile()) {
					
					if(file.getName().matches("(?i).*"+argument.substring(1, argument.length()-1)+".*")){
	
						file_index.add(i+1);
						file_size.add((int)file.length());
						file_name.add(file.getName());
						number_hits++;
//						System.out.println("Query hit:Match Found="+file.getName());
					}
					
				}
			i++;
			}
//			System.out.println("Query hit: No of hits found="+number_hits);
			if(number_hits>0)
			{
			QUERY_HIT_PAYLOAD_LENGTH=0;
		
			for(i=0;i<number_hits;i++)
			{
				String s=new String(file_name.get(i)+"\0");
				byte[] name=s.getBytes("UTF-16LE");
				QUERY_HIT_PAYLOAD_LENGTH = QUERY_HIT_PAYLOAD_LENGTH + name.length;
//				System.out.println("string="+s+" size="+name.length);
			}
			QUERY_HIT_PAYLOAD_LENGTH = QUERY_HIT_PAYLOAD_LENGTH + 27 + number_hits*8 +23;
//			System.out.println("Query hit Payload length = "+QUERY_HIT_PAYLOAD_LENGTH);
			
			if(QUERY_HIT_PAYLOAD_LENGTH <= 4096){
			ByteBuffer payloadBuffer = ByteBuffer.allocate(QUERY_HIT_PAYLOAD_LENGTH);
			payloadBuffer.put(number_hits);	
			payloadBuffer.putShort(port);
			payloadBuffer.put(IP);
			payloadBuffer.putInt(speed);
			for(i=0;i<number_hits;i++)
			{
				payloadBuffer.putInt(file_index.get(i));
				payloadBuffer.putInt(file_size.get(i));
				payloadBuffer.put(new String(file_name.get(i)+"\0").getBytes());
			}
			payloadBuffer.put(Connection.servantId);
//			System.out.println("Servent ID = "+new String(Connection.servantId));
		
			byte[] payload=payloadBuffer.array();
			return (payload);
			}
			else
				System.out.println("Error in forming Query hit Payload: Overall size of message exceeds 4KB!!!...Dropping Packet");
				return null;
			}
			else 
				return null;

		}catch(Exception e){
			return(null);
		}

	}

	public int QueryHitALL(byte[]id,String argument,Socket clientSocket,ObjectOutputStream objOutputStream ) throws IOException{
//		System.out.println("Sending query hit");
		byte type=(byte)0x81;
		
		byte[] payload=createPayload(argument);
		if(null!=payload){
		Message msg=new Message(id,type);
		msg.setpayloadLength(payload.length);
		msg.setPayload(payload);
		Connection.no_of_replies++;
		objOutputStream.writeObject((Object)msg);
//		System.out.println("query hit send send");
		return payload.length;
		}
		else
//			System.out.println("Query hit: Zero hits");
			return 0;
	}

}
