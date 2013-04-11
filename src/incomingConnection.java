import java.awt.RenderingHints.Key;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class incomingConnection extends Thread{
	private Socket[] clientSocket=new Socket[4];
	ObjectInputStream obj=null;
	ObjectOutputStream[] obj1=new ObjectOutputStream[4];
	
	int id;
	//private static List<Connection> connectionList=new ArrayList<Connection>();
	public static int numOfConn=-1;
	incomingConnection(Socket connection){
		incomingConnection.numOfConn=incomingConnection.numOfConn+1;
		id=incomingConnection.numOfConn;
		clientSocket[incomingConnection.numOfConn]=connection;
		Connection.incomingConnPackRecv[id]=0;
		Connection.incomingConnPackSent[id]=0;
		Connection.incomingConnPackRecvSize[id]=0;
		Connection.incomingConnPackSentSize[id]=0;
		try {
			obj1[incomingConnection.numOfConn]=new ObjectOutputStream(clientSocket[incomingConnection.numOfConn].getOutputStream());
		} catch (IOException e) {
			System.out.println("Unable to create Output Stream");
			e.printStackTrace();
		}
	
		Connection.incomingConnection[incomingConnection.numOfConn]=connection;
		Connection.outStream[incomingConnection.numOfConn]=obj1[incomingConnection.numOfConn];
	
		start();
	}
	public void run(){
		Message inputFromClient=new Message();
		try {
			obj=new ObjectInputStream(clientSocket[incomingConnection.numOfConn].getInputStream());
			while((inputFromClient = (Message)obj.readObject())!=null){
				Connection.incomingConnPackRecv[id]++;
				if(inputFromClient.getMessage_type()==04){
					byte[] byteArray=inputFromClient.getPayload();
					Connection.incomingConnPackRecvSize[id]+=byteArray.length+23;
					String strInputFromClient= new String(byteArray);
					System.out.println("Inp from client: "+strInputFromClient );
					String stroutToClient=null;
					byte type = 0;
					if(incomingConnection.numOfConn<=2)
						{
							stroutToClient="SIMPELLA/0.6 200 OK\r\n";
							type=05;
						}
					else
						{
							stroutToClient="SIMPELLA/0.6 503 Max Number of connections reached. Sorry!\r\n";
							type=06;
						}
					
					Message outToClient= new Message(type);
					outToClient.setPayload(stroutToClient.getBytes("UTF-16LE"));
					Connection.incomingConnPackSent[id]++;
					Connection.incomingConnPackSentSize[id]+=outToClient.getpayloadLength()+23;
					obj1[incomingConnection.numOfConn].writeObject(outToClient);
					Connection.incomingConnPackRecv[id]++;
					Message m=(Message)obj.readObject();
					System.out.println("Inp from client: "+new String(m.getPayload()));
					Connection.incomingConnPackRecvSize[id]+=m.getpayloadLength()+23;
					if(type==06){
					Connection.incomingConnection[id].close();
					Connection.outStream[id].close();
					obj.close();
					Connection.incomingConnection[id]=null;
					Connection.outStream[id]=null;
					
					incomingConnection.numOfConn--;
//					System.out.println("Server:no of conn="+(incomingConnection.numOfConn+1));
					}
				}
				
				
				if(inputFromClient.getMessage_type()==0x00){
					System.out.println("Server:Ping received");
					inputFromClient.setTtl((byte)(inputFromClient.getTtl()-1));
					inputFromClient.setHops((byte)(inputFromClient.getHops()+1));
					Connection.incomingConnPackRecvSize[id]+=23;
					
					int proceedFlg=0;
//						System.out.println("msg id " + new String(inputFromClient.getMessage_id()) +"Payload len="+inputFromClient.getpayloadLength()+" type="+inputFromClient.getMessage_type());
//							System.out.println("Server:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
						int proceedFlg1=1;	
						if(Connection.generatedMsgId.contains(new String(inputFromClient.getMessage_id())))
						{
							System.out.println("****** SERVER: I GENERATED THIS PING*********");
							proceedFlg1=0;
						}
							if( Connection.objMap.containsKey(new String(inputFromClient.getMessage_id())) )
							{
								proceedFlg=0;
								proceedFlg1=0;
							}
							else
							{
								if(proceedFlg1==1){
//								System.out.println("server:entry to hashmap");
								Connection.objMap.put(new String(inputFromClient.getMessage_id()), id);
//								System.out.println("Server:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
								//System.out.println("key="+new String(inputFromClient.getMessage_id())+" id="+ Connection.objMap.get(new String(inputFromClient.getMessage_id())));
								proceedFlg=1;
								}
							}
					
					if(proceedFlg==1 && proceedFlg1==1)
					{
					//send pong
					PONG objPong=new PONG();
					objPong.PongAll(inputFromClient.getMessage_id(), Connection.incomingConnection[id], Connection.outStream[id]);
					Connection.incomingConnPackSent[id]++;
					Connection.incomingConnPackSentSize[id]+=23+14;
					//objPong.PongAll(inputFromClient.getMessage_id(),clientSocket[numOfConn],obj1[numOfConn]);
					if(inputFromClient.getTtl()>0){
					//Ping objPing= new Ping();
					Socket[] arrSocket= new Socket[3];
					ObjectOutputStream[] objOutput= new ObjectOutputStream[3];
					
					Socket[] arrSocket1= new Socket[3];
					ObjectOutputStream[] objOutput1= new ObjectOutputStream[3];
					//byte[] Message_id=inputFromClient.getMessage_id();
					
//					System.out.println("Server:Forwarding ping to incoming connections");
					//inputFromClient.setHops(inputFromClient)
					for(int i=0;i<3;i++){
						if(i!=id){
						arrSocket[i]=Connection.incomingConnection[i];
						objOutput[i]=Connection.outStream[i];
						
						try{
							Connection.incomingConnPackSent[i]++;
							Connection.incomingConnPackSentSize[i]+=23;
							objOutput[i].writeObject((Object)inputFromClient);
						
						}catch(Exception e){Connection.incomingConnPackSent[i]--;Connection.incomingConnPackSentSize[i]-=23;}
						}
					
						}
//					System.out.println("Server:Forwarding ping to outgoing connections");
					for(int i=0;i<3;i++){
						arrSocket1[i]=Connection.outgoingConnection[i];
						objOutput1[i]=Connection.clientOutStream[i];
						try{
							Connection.incomingConnPackSent[i]++;
							Connection.incomingConnPackSentSize[i]+=23;
							objOutput1[i].writeObject((Object)inputFromClient);
							
						}catch(Exception e){Connection.incomingConnPackSent[i]--;Connection.incomingConnPackSentSize[i]-=23;}
					}
					}else
						System.out.println("***********SERVER PING DROPPING PACKET**************");
				}
				}
				
				if(inputFromClient.getMessage_type()==(byte)0x80){
					System.out.println("Server:Query received");
					Connection.no_of_queries++;
					byte[] a = inputFromClient.getPayload();
					String query= new String(ByteBuffer.wrap(a, 2, a.length-2).array(),"UTF-16LE");
					Connection.incomingConnPackRecvSize[id]+=a.length+23;
					if(Connection.queries.contains(query))
						;
					else
						Connection.queries.add(query);
						
					inputFromClient.setTtl((byte)(inputFromClient.getTtl()-1));
					inputFromClient.setHops((byte)(inputFromClient.getHops()+1));
					//***********************************
					int proceedFlg=0;
					System.out.println("msg id " + new String(inputFromClient.getMessage_id()) +"Payload len="+inputFromClient.getpayloadLength()+" type="+inputFromClient.getMessage_type());
//						System.out.println("Server:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
					int proceedFlg1=1;	
					if(Connection.generatedMsgId.contains(new String(inputFromClient.getMessage_id())))
					{
						System.out.println("****** SERVER: I GENERATED THIS QUERY*********");
						proceedFlg1=0;
					}
						if( Connection.objMap.containsKey(new String(inputFromClient.getMessage_id())) )
						{
							proceedFlg=0;
							proceedFlg1=0;
						}
						else
						{
							if(proceedFlg1==1){
//							System.out.println("server:entry to hashmap");
							Connection.objMap.put(new String(inputFromClient.getMessage_id()), id);
//							System.out.println("Server:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
							//System.out.println("key="+new String(inputFromClient.getMessage_id())+" id="+ Connection.objMap.get(new String(inputFromClient.getMessage_id())));
							proceedFlg=1;
							}
						}
				
				
				
					//************************************
					if(proceedFlg==1 && proceedFlg1==1)	{
					System.out.println("Server:Query="+ query);
					QUERY_HIT objQueryHit=new QUERY_HIT();
					int b=objQueryHit.QueryHitALL(inputFromClient.getMessage_id(),query, Connection.incomingConnection[id], Connection.outStream[id]);
					if(b>0)
						{
							Connection.incomingConnPackSent[id]++;
							Connection.incomingConnPackSentSize[id]+=b+23;
						}
					if(inputFromClient.getTtl()>0){
					Socket[] arrSocket= new Socket[3];
					ObjectOutputStream[] objOutput= new ObjectOutputStream[3];
					
					Socket[] arrSocket1= new Socket[3];
					ObjectOutputStream[] objOutput1= new ObjectOutputStream[3];
//					System.out.println("Server:Forwarding query to incoming connections");
					for(int i=0;i<3;i++){
						if(i!=id){
						arrSocket[i]=Connection.incomingConnection[i];
						objOutput[i]=Connection.outStream[i];
						
						try{
							Connection.incomingConnPackSent[i]++;
							Connection.incomingConnPackSentSize[i]+=a.length+23;
							objOutput[i].writeObject(inputFromClient);
						
						}catch(Exception e){Connection.incomingConnPackSent[i]--;Connection.incomingConnPackSentSize[i]-=(a.length+23);}
						}
						}
//					System.out.println("Server:Forwarding query to outgoing connections");
					for(int i=0;i<3;i++){
						arrSocket1[i]=Connection.outgoingConnection[i];
						objOutput1[i]=Connection.clientOutStream[i];
						try{
							Connection.incomingConnPackSent[i]++;
							Connection.incomingConnPackSentSize[i]+=a.length+23;
							objOutput1[i].writeObject(inputFromClient);
							
						}catch(Exception e){Connection.incomingConnPackSent[i]--;Connection.incomingConnPackSentSize[i]-=(a.length+23);}
					}
					
				}else
					System.out.println("***********SERVER QUERY DROPPING PACKET**************");
				}
				}
				if(inputFromClient.getMessage_type()==0x01){
					System.out.println("Server:Pong received");
					Connection.incomingConnPackRecvSize[id]+=14+23;
					inputFromClient.setTtl((byte)(inputFromClient.getTtl()-1));
					inputFromClient.setHops((byte)(inputFromClient.getHops()+1));
					if(inputFromClient.getTtl()>=0){
					
//					System.out.println("msg id " + new String(inputFromClient.getMessage_id()) +"Payload len="+inputFromClient.getpayloadLength()+" type="+inputFromClient.getMessage_type());
					byte[] a=inputFromClient.getPayload();
					System.out.println("port="+ByteBuffer.wrap(a, 0, 2).getShort()+" IP="+InetAddress.getByAddress(Arrays.copyOfRange(a, 2, 6))+" files shared=" + ByteBuffer.wrap(a, 6, 4).getInt() +" file size="+ ByteBuffer.wrap(a, 10, 4).getFloat());
					System.out.println("Server:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
//					for(byte b:inputFromClient.getMessage_id())
//					{
//						System.out.println("**"+b);
//					}
//					System.out.println("key="+new String(inputFromClient.getMessage_id())+" id="+ Connection.objMap.get(new String(inputFromClient.getMessage_id())));
					try{
					Connection.incomingConnPackSent[id]++;
					Connection.incomingConnPackSentSize[id]+=14+23;
					int num=Connection.objMap.get(new String(inputFromClient.getMessage_id()));
//					System.out.println("Forwarding Pong");
					Connection.outStream[num].writeObject((Object)inputFromClient);
					}catch(Exception e){
						System.out.println("Server Pong: Last Node");
						Connection.incomingConnPackSent[id]--;
						Connection.incomingConnPackSentSize[id]-=(14+23);
						if(Connection.hostConnectedTo.contains(""+InetAddress.getByAddress(Arrays.copyOfRange(a, 2, 6))+":"+ByteBuffer.wrap(a, 0, 2).getShort()))
							;
						else
							Connection.hostConnectedTo.add(""+InetAddress.getByAddress(Arrays.copyOfRange(a, 2, 6))+":"+ByteBuffer.wrap(a, 0, 2).getShort());
						Connection.files_on_network+=ByteBuffer.wrap(a, 6, 4).getInt();
						Connection.bytes_on_network+=ByteBuffer.wrap(a, 10, 4).getFloat();	
					}
					//send pong
				}else
					System.out.println("***********SERVER PONG DROPPING PACKET**************");
				}
				if(inputFromClient.getMessage_type()==(byte)0x81){
					System.out.println("Query Hit received");
					Connection.incomingConnPackRecvSize[id]+=inputFromClient.getpayloadLength()+23;
					inputFromClient.setTtl((byte)(inputFromClient.getTtl()-1));
					inputFromClient.setHops((byte)(inputFromClient.getHops()+1));
					if(inputFromClient.getTtl()>=0){
					
//					System.out.println("msg id " + new String(inputFromClient.getMessage_id()) +"Payload len="+inputFromClient.getpayloadLength()+" type="+inputFromClient.getMessage_type());
					
					//System.out.println("port="+ByteBuffer.wrap(a, 0, 2).getShort()+" IP="+InetAddress.getByAddress(Arrays.copyOfRange(a, 2, 6))+" files shared=" + ByteBuffer.wrap(a, 6, 4).getInt() +" file size="+ ByteBuffer.wrap(a, 10, 4).getFloat());
//					System.out.println("Server:size of hashmap="+Connection.objMap.size()+" values= "+Connection.objMap.entrySet());
//					System.out.println("key="+new String(inputFromClient.getMessage_id())+" id="+ Connection.objMap.get(new String(inputFromClient.getMessage_id())));
					try{
					Connection.incomingConnPackSent[id]++;
					Connection.incomingConnPackSentSize[id]+=inputFromClient.getpayloadLength()+23;
					int num=Connection.objMap.get(new String(inputFromClient.getMessage_id()));
//					System.out.println("Forwarding Query Hit");
					Connection.outStream[num].writeObject(inputFromClient);
					}catch(Exception e)
					{
						Connection.incomingConnPackSent[id]--;
						Connection.incomingConnPackSentSize[id]-=(inputFromClient.getpayloadLength()+23);
						byte[] a=inputFromClient.getPayload();
//						System.out.println("number of hits="+ByteBuffer.wrap(a, 0, 1).get()+" port="+ByteBuffer.wrap(a, 1, 2).getShort()+" IP="+InetAddress.getByAddress(Arrays.copyOfRange(a, 3, 7)));
						Connection.list_files.add(a);
						System.out.println("Server Query Hit: Last Node");
					}
				}else
					System.out.println("***********SERVER QUERY HIT DROPPING PACKET**************");
				}
				
				if(incomingConnection.numOfConn>2)
				{
					incomingConnection.numOfConn=incomingConnection.numOfConn-1;
					//outToClient="SIMPELLA/0.6 503 Maximum number of connections reached. Sorry!\r\n";
				}
//				System.out.println("Server:no of conn="+(incomingConnection.numOfConn+1));
				}
						
		}catch(SocketException e){
			Connection.incomingConnection[id]=null;
			Connection.outStream[id]=null;
		
			System.out.println("Server:Connection Reset by Client...no of conn="+(incomingConnection.numOfConn+1));
			
		} 
		catch (Exception e) {
			//System.out.println("Server:Connection Reset by Client:"+Connection.incomingConnection[incomingConnection.numOfConn].getRemoteSocketAddress());
			e.printStackTrace();
		}
	}
}
