import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;


public class Query extends Message{

	public final static short min_speed=0;
	public static String search_string;
	byte[] id;
	
	Query(String search_string) {
		Query.search_string=search_string;
	}

	
	public byte[] createPayload(){

		byte[] byteArray = null;
		
		try {
			byteArray = Query.search_string.getBytes("UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding");
			e.printStackTrace();
		}
			byte[] payload=null;
		if(byteArray.length <= 231)
		{
		ByteBuffer payloadBuffer = ByteBuffer.allocate(byteArray.length+2);
		payloadBuffer.putShort(Query.min_speed);
		payloadBuffer.put(byteArray);
		payload=payloadBuffer.array();
		}
		else
			payload=null;
		return (payload);
}
	
	public void QueryAll(int flg,Message msg,Socket[] clientSideSocket,ObjectOutputStream[] outToServant) throws IOException{
		

		int i=0;

		while(i< clientSideSocket.length){
		if( !(clientSideSocket[i]==null))
		{
			
			byte[] payload=createPayload();
			if(null!=payload){
			msg.setPayload(payload);
			msg.setpayloadLength(payload.length);
//			System.out.println("Inside Queryall " + new String(msg.getMessage_id()));
			byte[] b=msg.getPayload();
//			System.out.println("payload length="+payload.length+" speed="+ByteBuffer.wrap(b, 0, 2).getShort() +" Query="+new String(ByteBuffer.wrap(b,2,msg.getpayloadLength()-2).array()));
//			System.out.println("msg id " + new String(msg.getMessage_id()) +"Payload len="+msg.getpayloadLength()+" type="+(byte)msg.getMessage_type());
			try {
				if(flg==1)
				{
					++Connection.outgoingConnPackSent[i];
					Connection.outgoingConnPackSentSize[i]+=23+payload.length;
				}
				else
					{
						++Connection.incomingConnPackSent[i];
						Connection.incomingConnPackSentSize[i]+=23+payload.length;
					}
				outToServant[i].writeObject((Object)msg);
			} catch (SocketException e) {
				if(flg==1)
				{
					--Connection.outgoingConnPackSent[i];
					Connection.outgoingConnPackSentSize[i]-=(23+payload.length);
				}
				else
					{
						--Connection.incomingConnPackSent[i];
						Connection.incomingConnPackSentSize[i]-=(23+payload.length);
					}
				outToServant[i]=null;
				clientSideSocket[i]=null;
//				System.out.println("Queryall exception caught");
				//e.printStackTrace();
			}
		}
			else
			{
				System.out.println("Error in forming Query Payload: Size of Payload greater than 256 bytes!!!");
			}
		
		}
		i++;	
	}
	}}	
