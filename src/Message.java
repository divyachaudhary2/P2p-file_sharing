
import java.io.Serializable;
import java.util.Random;
public class Message implements Serializable{
	
	protected byte[] Message_id =new byte[16];
//	final static byte PING=(byte)0x00;
//	final static byte PONG=(byte)0x01;
//	final static byte QUERY=(byte) 0x80;
//	final static byte QUERY_HIT=(byte)0x81;
	protected  byte message_type;
	protected byte ttl;
	protected byte hops;
	protected byte[] payload;
	protected int payloadLength;
//	final static int PING_PAYLOAD_LENGTH=0;
//	final static int PONG_PAYLOAD_LENGTH=14;
//	static int QUERY_PAYLOAD_LENGTH=0;
//	static int QUERY_HIT_PAYLOAD_LENGTH=0;
	
	Message(){
		
	}
	
	Message(byte type){
		this.Message_id=formMessageId();
		this.message_type=type;
		this.ttl=7;
		this.hops=0;
	}
	Message( byte[] id, byte type){
		this.Message_id=id;
		this.message_type=type;	
		this.ttl=7;
		this.hops=0;
	}
	
	public  byte[] getMessage_id() {
		return Message_id;
	}
	public  void setMessage_id(byte[] message_id) {
		Message_id = message_id;
	}
	
	public byte getMessage_type() {
		return message_type;
	}
	public void setMessage_type(byte message_type) {
		this.message_type = message_type;
	}
	public byte getTtl() {
		return ttl;
	}
	public void setTtl(byte ttl) {
		this.ttl = ttl;
	}
	public byte getHops() {
		return hops;
	}
	public void setHops(byte hops) {
		this.hops = hops;
	}
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	public int getpayloadLength() {
		return payloadLength;
	}
	public void setpayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
	}
	

public static byte[] formMessageId(){
	byte[] id= new byte[16];
	Random generator = new Random();
	generator.nextBytes(id);
	id[7]=(byte) 0xff;
	id[15]=0x00;
	return id;
}


}
