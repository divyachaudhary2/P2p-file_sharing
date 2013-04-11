import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Connection {

	public static Socket[] incomingConnection=new Socket[4];
	public static ObjectOutputStream[] outStream=new ObjectOutputStream[4];
	public static int[] incomingConnPackRecv=new int[4];
	public static int[] incomingConnPackSent=new int[4];
	public static int[] incomingConnPackRecvSize=new int[4];
	public static int[] incomingConnPackSentSize=new int[4];
	
	public static Socket[] outgoingConnection=new Socket[4];
	public static ObjectOutputStream[] clientOutStream=new ObjectOutputStream[4];
	public static int[] outgoingConnPackRecv=new int[4];
	public static int[] outgoingConnPackSent=new int[4];
	public static int[] outgoingConnPackRecvSize=new int[4];
	public static int[] outgoingConnPackSentSize=new int[4];
	
	//public static ConcurrentHashMap<byte[], Integer> objMap= new ConcurrentHashMap<byte[], Integer>();
	public static ConcurrentHashMap<String, Integer> objMap= new ConcurrentHashMap<String, Integer>(0,1,1);
	public static final byte[] servantId=new byte[16];
	static{
		Random generator = new Random();
		generator.nextBytes(servantId);
	}
	public static ArrayList<Socket> connections = new ArrayList<Socket>();
	public static ArrayList<Integer> list_file_size=new ArrayList<Integer>();
	public static ArrayList<String> list_file_name = new ArrayList<String>();
	public static ArrayList<InetAddress> list_IP = new ArrayList<InetAddress>();
	public static ArrayList<Short> list_port=new ArrayList<Short>();
	public static ArrayList<Integer> list_file_index=new ArrayList<Integer>();
	public static ArrayList<byte[]> list_files=new ArrayList<byte[]>();
	
	public static ArrayList<String> generatedMsgId=new ArrayList<String>();
	
	public static ArrayList<String> queries=new ArrayList<String>();
	public static int no_of_queries=0;
	public static int no_of_replies=0;
	
	public static ArrayList<String> hostConnectedTo=new ArrayList<String>();
	public static int files_on_network;
	public static float bytes_on_network;
	
	public static HashMap<Integer,Long> download = new HashMap<Integer,Long>(); 
}
