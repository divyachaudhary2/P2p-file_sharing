import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class clientSocketListen extends Thread{
	Socket socket=null;
	ObjectInputStream obj=null;
	ObjectOutputStream obj1=null;
	int id;
	
	clientSocketListen(Socket socket,ObjectOutputStream outToServer,ObjectInputStream inFromServer)
	{
		this.socket=socket;
		obj1=outToServer;
		obj=inFromServer;
		id=client.noOfConn;
		start();
	}
	
	public void run()
	{
		try{
//			System.out.println("inside client socket listen");
//			System.out.println("addr="+socket.getRemoteSocketAddress());
		Message msg=new Message();
		while((msg = (Message)obj.readObject())!=null){
			Connection.outgoingConnPackRecv[id]++;
			Connection.outgoingConnPackRecvSize[id]+=23;
			if(msg.getMessage_type()==0x00){
				System.out.println("Client Socket Listen:Ping received");
				msg.setTtl((byte)(msg.getTtl()-1));
				msg.setHops((byte)(msg.getHops()+1));
				int proceedFlg=0;
//				System.out.println("msg id " + new String(msg.getMessage_id()) +"Payload len="+msg.getpayloadLength()+" type="+msg.getMessage_type());
//				System.out.println("Client Socket Listen:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
				int proceedFlg1=1;	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(Connection.generatedMsgId.contains(new String(msg.getMessage_id())))
				{
					System.out.println("******Client Sock Listen I GENERATED THIS PING*********");
					proceedFlg1=0;
				}
				
				if( Connection.objMap.containsKey(new String(msg.getMessage_id())) )
				{
					proceedFlg=0;
					proceedFlg1=0;
				}
				else
				{
					if(proceedFlg1==1){
//					System.out.println("client socket listen:entry to hashmap");
					Connection.objMap.put(new String(msg.getMessage_id()),(id+3));
//					System.out.println("Client Socket Listen:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
					proceedFlg=1;
				}
				}
				
				
				
				if(proceedFlg==1 && proceedFlg1==1){
				PONG objPong=new PONG();
				objPong.PongAll(msg.getMessage_id(),Connection.outgoingConnection[id],Connection.clientOutStream[id]);
				Connection.outgoingConnPackSent[id]++;
				Connection.outgoingConnPackSentSize[id]+=14+23;
				if(msg.getTtl()>0){
				Socket[] arrSocket= new Socket[3];
				ObjectOutputStream[] objOutput= new ObjectOutputStream[3];
				
				Socket[] arrSocket1= new Socket[3];
				ObjectOutputStream[] objOutput1= new ObjectOutputStream[3];
				System.out.println("Client Socket Listen:Forwarding ping to outgoing connections");
				for(int i=0;i<3;i++){
					if(i!=id){
					arrSocket1[i]=Connection.outgoingConnection[i];
					objOutput1[i]=Connection.clientOutStream[i];
					
					try{
						Connection.outgoingConnPackSent[i]++;
						Connection.outgoingConnPackSentSize[i]+=23;
						objOutput1[i].writeObject((Object)msg);
					
					}catch(Exception e){
						Connection.outgoingConnPackSent[i]--;
						Connection.outgoingConnPackSentSize[i]-=(23);
					}
					}
						
				}
				System.out.println("Client Socket Listen:Forwarding ping to incoming connections");
				for(int i=0;i<3;i++){
					arrSocket[i]=Connection.incomingConnection[i];
					objOutput[i]=Connection.outStream[i];
					try{
						Connection.outgoingConnPackSent[i]++;
						Connection.outgoingConnPackSentSize[i]+=23;
						objOutput[i].writeObject(msg);
						
					}catch(Exception e){Connection.outgoingConnPackSent[i]--;Connection.outgoingConnPackSentSize[i]-=23;}
				}
			}else
				System.out.println("***********CLIENT SOCKET LISTEN - PING DROPPING PACKET**************");
				}
				}
			
			if(msg.getMessage_type()==(byte)0x80){
				System.out.println("Client Socket Listen:Query received");
				Connection.no_of_queries++;
				byte[] a = msg.getPayload();
				Connection.outgoingConnPackRecvSize[id]+=a.length+23;
				String query= new String(ByteBuffer.wrap(a, 2, a.length-2).array(),"UTF-16LE");
				if(Connection.queries.contains(query))
				;
				else
					Connection.queries.add(query);
				msg.setTtl((byte)(msg.getTtl()-1));
				msg.setHops((byte)(msg.getHops()+1));
				//*****************************
				int proceedFlg=0;
//				System.out.println("msg id " + new String(msg.getMessage_id()) +"Payload len="+msg.getpayloadLength()+" type="+msg.getMessage_type());
//				System.out.println("Client Socket Listen:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
				int proceedFlg1=1;	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(Connection.generatedMsgId.contains(new String(msg.getMessage_id())))
				{
					System.out.println("******Client Sock Listen I GENERATED THIS QUERY*********");
					proceedFlg1=0;
				}
				
				if( Connection.objMap.containsKey(new String(msg.getMessage_id())) )
				{
					proceedFlg=0;
					proceedFlg1=0;
				}
				else
				{
					if(proceedFlg1==1){
//					System.out.println("client socket listen:entry to hashmap");
					Connection.objMap.put(new String(msg.getMessage_id()),(id+3));
//					System.out.println("Client Socket Listen:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
					proceedFlg=1;
				}
				}

				//****************************
				if(proceedFlg==1 && proceedFlg1==1){
//				System.out.println("Client Socket Listen:Query="+ query);
				QUERY_HIT objQueryHit=new QUERY_HIT();
				int b=objQueryHit.QueryHitALL(msg.getMessage_id(),query,Connection.outgoingConnection[id],Connection.clientOutStream[id]);
				if(b>0)
					{
						Connection.outgoingConnPackSent[id]++;
						Connection.outgoingConnPackSentSize[id]+=b+23;
					}
				
				if(msg.getTtl()>0){
				Socket[] arrSocket= new Socket[3];
				ObjectOutputStream[] objOutput= new ObjectOutputStream[3];
				
				Socket[] arrSocket1= new Socket[3];
				ObjectOutputStream[] objOutput1= new ObjectOutputStream[3];
			
//				System.out.println("Client Socket Listen:Forwarding query to outgoing connections");
				for(int i=0;i<3;i++){
					if(i!=id){
					arrSocket1[i]=Connection.outgoingConnection[i];
					objOutput1[i]=Connection.clientOutStream[i];
					
					try{
						Connection.outgoingConnPackSent[i]++;
						Connection.outgoingConnPackSentSize[i]+=a.length+23;
						objOutput1[i].writeObject(msg);
					
					}catch(Exception e){Connection.outgoingConnPackSent[i]--;Connection.outgoingConnPackSentSize[i]-=(a.length+23);}
					}
				}
//				System.out.println("Client Socket Listen:Forwarding query to incoming connections");
				for(int i=0;i<3;i++){
					arrSocket[i]=Connection.incomingConnection[i];
					objOutput[i]=Connection.outStream[i];
					try{
						Connection.outgoingConnPackSent[i]++;
						Connection.outgoingConnPackSentSize[i]+=a.length+23;
						objOutput[i].writeObject(msg);
					
					}catch(Exception e){Connection.outgoingConnPackSent[i]--;Connection.outgoingConnPackSentSize[i]-=(a.length+23);}
				}
			}else
				System.out.println("***********CLIENT SOCKET LISTEN - QUERY DROPPING PACKET**************");
			}
			}
			
			if(msg.getMessage_type()==0x01){
				System.out.println("Client Socket Listen:Pong received");
				msg.setTtl((byte)(msg.getTtl()-1));
				msg.setHops((byte)(msg.getHops()+1));
				Connection.outgoingConnPackRecvSize[id]+=23+14;
				if(msg.getTtl()>=0){

				byte[] a=msg.getPayload();
//				System.out.println("port="+ByteBuffer.wrap(a, 0, 2).getShort()+" IP="+InetAddress.getByAddress(Arrays.copyOfRange(a, 2, 6))+" files shared=" + ByteBuffer.wrap(a, 6, 4).getInt() +" file size="+ ByteBuffer.wrap(a, 10, 4).getFloat());

				int num;
				System.out.println("msg id= " + new String(msg.getMessage_id()) +"Payload len="+msg.getpayloadLength()+" type="+msg.getMessage_type());
				try{
					Connection.outgoingConnPackSent[id]++;
					Connection.outgoingConnPackSentSize[id]+=14+23;
					num=Connection.objMap.get(new String(msg.getMessage_id()));
					if(num<=2)
					{
//						System.out.println("client socket listen: Forward Pong to server out stream");
						
						Connection.outStream[num].writeObject((Object)msg);
					}
					else
						{
						//	++Connection.outgoingConnPackSent[id];
							Connection.clientOutStream[num-3].writeObject(msg);
							
						}
				}catch(Exception e)
				{
					System.out.println("client socket listen Pong: Last node");
					Connection.outgoingConnPackSent[id]--;
					Connection.outgoingConnPackSentSize[id]-=(14+23);
					if(Connection.hostConnectedTo.contains(""+InetAddress.getByAddress(Arrays.copyOfRange(a, 2, 6))+":"+ByteBuffer.wrap(a, 0, 2).getShort()))
						;
					else
						Connection.hostConnectedTo.add(""+InetAddress.getByAddress(Arrays.copyOfRange(a, 2, 6))+":"+ByteBuffer.wrap(a, 0, 2).getShort());
					Connection.files_on_network+=ByteBuffer.wrap(a, 6, 4).getInt();
					Connection.bytes_on_network+=ByteBuffer.wrap(a, 10, 4).getFloat();
					
				}
				//send pong
			}else
				System.out.println("***********CLIENT SOCKET LISTEN - PONG DROPPING PACKET**************");
			}
			
			if(msg.getMessage_type()==(byte)0x81){
				System.out.println("Client Socket Listen: Query hit received");
				msg.setTtl((byte)(msg.getTtl()-1));
				msg.setHops((byte)(msg.getHops()+1));
				Connection.outgoingConnPackRecvSize[id]+=23+msg.getpayloadLength();
				int proceed=0;
				if(msg.getTtl()>=0){
					proceed=1;
				}else
					System.out.println("***********CLIENT SOCKET LISTEN - QUERY HIT DROPPING PACKET**************");
				//System.out.println("port="+ByteBuffer.wrap(a, 0, 2).getShort()+" IP="+InetAddress.getByAddress(Arrays.copyOfRange(a, 2, 6))+" files shared=" + ByteBuffer.wrap(a, 6, 4).getInt() +" file size="+ ByteBuffer.wrap(a, 10, 4).getFloat());
				int num;
				
//				System.out.println("msg id= " + new String(msg.getMessage_id()) +"Payload len="+msg.getpayloadLength()+" type="+msg.getMessage_type());
				try{
					Connection.outgoingConnPackSent[id]++;
					Connection.outgoingConnPackSentSize[id]+=msg.getpayloadLength()+23;
					num=Connection.objMap.get(new String(msg.getMessage_id()));
					if(num<=2 && proceed==1)
					{
						System.out.println("client socket listen: Forward query hit to server out stream");
						Connection.outStream[num].writeObject(msg);
					}
					else if(proceed==1)
					{
						System.out.println("client socket listen: Forward query hit to client out stream");
						Connection.clientOutStream[num-3].writeObject(msg);
					}
				
				}catch(Exception e)
				{
					Connection.outgoingConnPackSent[id]--;
					Connection.outgoingConnPackSentSize[id]-=(msg.getpayloadLength()+23);
					byte[] a=msg.getPayload();
					System.out.println("number of hits="+ByteBuffer.wrap(a, 0, 1).get()+" port="+ByteBuffer.wrap(a, 1, 2).getShort()+" IP="+InetAddress.getByAddress(Arrays.copyOfRange(a, 3, 7)));
					Connection.list_files.add(a);
					System.out.println("client Socket listen Query Hit: Last node");
				}
			
			}
		}
	}catch(SocketException e){
		Connection.outgoingConnection[id]=null;
		Connection.clientOutStream[id]=null;
		client.noOfConn--;
		System.out.println("Client:Connection Reset by Server...no of conn="+client.noOfConn);
	
	}
		catch(Exception e){e.printStackTrace();}
	}
}
