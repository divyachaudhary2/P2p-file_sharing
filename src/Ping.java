import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Ping extends Message {
	ObjectOutputStream outToServant=null;
	//ObjectInputStream inFromServant=null;
	Ping(){
		
	}
	Ping(byte type){
		super(type);
	}

	public void PingAll(int flg,Message msg, Socket[] clientSideSocket,ObjectOutputStream[] outToServant) throws IOException{
		

		int i=0;

		while(i< clientSideSocket.length){
		if( !(clientSideSocket[i]==null))
		{
			
			msg.payloadLength=0;
//			System.out.println("Inside ping " + new String(msg.getMessage_id()));
//			System.out.println(clientSideSocket[i].getRemoteSocketAddress());
			
			try {
				if(flg==1)
					{
						++Connection.outgoingConnPackSent[i];
						Connection.outgoingConnPackSentSize[i]+=23;
					}
				else
					{
						++Connection.incomingConnPackSent[i];
						Connection.incomingConnPackSentSize[i]+=23;
					}
				outToServant[i].writeObject((Object)msg);
				
			} catch (SocketException e) {
				if(flg==1)
					{
						--Connection.outgoingConnPackSent[i];
						Connection.outgoingConnPackSentSize[i]-=23;
					}
				else
					{
						--Connection.incomingConnPackSent[i];
						Connection.incomingConnPackSentSize[i]-=23;
					}
				outToServant[i]=null;
				clientSideSocket[i]=null;
//				System.out.println("Ping exception caught");
				//e.printStackTrace();
			}
		}
		i++;
		}
			
	}
	
}