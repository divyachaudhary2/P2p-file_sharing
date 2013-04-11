import java.io.File;


public class Utilities {
	String sharedDirectory=System.getProperty("user.dir");
	
	long sizeOfFiles=0;
	public String share(String argument) {
		if(argument.startsWith("-i") && argument.length()==3)	
			System.out.println("Sharing "+sharedDirectory);
		else if(argument.startsWith("dir"))
			{
				String[] token=argument.split(" ");
				String dir="";
				for(int i=1;i<token.length;i++)
					dir=dir + token[i] +" ";
				if(token[1].startsWith("/"))
					sharedDirectory=sharedDirectory + "\\" + dir;
					// FOR UNIX sharedDirectory=token[1]
				else
					sharedDirectory=dir;
					//FOR UNIX sharedDirectory=sharedDirectory + "/" + dir;
				sharedDirectory=sharedDirectory.substring(0, sharedDirectory.length()-1);
			}
		return sharedDirectory;
		//System.out.println(sharedDirectory);
		}
	
	public static int find_noOffiles(String sharedDirectory)
	{	int numOfFiles=0;
		File[] filelist = new File(sharedDirectory).listFiles();
		for (File file : filelist) {
			if (file.isFile()) {
				numOfFiles++;
			}
		
		}
		
		return numOfFiles;
		
	}
	public static int find_sizeOFfiles(String sharedDirectory){
		File[] filelist = new File(sharedDirectory).listFiles();
		int sizeOfFiles=0;
		for (File file : filelist) {
			if (file.isFile()) {
				sizeOfFiles += file.length();
			}
			//System.out.println(file+"\t"+file.length());
		}
		return sizeOfFiles;
	}

}
