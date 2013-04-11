import java.io.IOException;
import java.net.ServerSocket;


public class incomingDownload extends Thread{
	
	int port;
	incomingDownload(int welcomePort) {
	port = welcomePort;
	}
	public void run()
	{
		try {
			ServerSocket ss=new ServerSocket(port);
			while(true){
				new downloadConnection(ss.accept());
				}
		} catch (IOException e) {
			System.out.println("Unable to start Download server at port:"+port);
			System.exit(1);
			//e.printStackTrace();
		}
	}
}
