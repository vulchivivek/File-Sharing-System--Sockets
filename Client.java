import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.io.*;
import java.net.*;


public class Client {
  
   public final static int FILE_SIZE = 6022386; // file size temporary hard coded
                                               // should bigger than the file to be downloaded

  public static void main (String [] args ) throws IOException, InterruptedException {
	String env = System.getenv("PA1_SERVER");
	//System.out.println(env);
	String[] parts = env.split(":");
	String SERVER = parts[0]; // computername
	int SOCKET_PORT = Integer.parseInt(parts[1]); // port number
    int bytesRead;
    int current = 0;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    Socket sock = null;
    try {
      sock = new Socket(SERVER, SOCKET_PORT);
      System.out.println("Connecting...");
	
	//send the string to server- download
	if(args[0].equals("download")){
	String sendserver="download-";
	sendserver=sendserver.concat(args[1]);
	  DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes(sendserver + '\n');
	//resume download
	File f=new File(args[2]);
	long fs=0;
	if(f.exists()) fs=f.length();

	String FILE_TO_RECEIVED =args[2];	
	System.out.println("Error-0");
      // receive file
      byte [] mybytearray  = new byte [FILE_SIZE];
      InputStream is = sock.getInputStream();
      fos = new FileOutputStream(FILE_TO_RECEIVED);
      bos = new BufferedOutputStream(fos);
      bytesRead = is.read(mybytearray,0,mybytearray.length);
      current = bytesRead;

      do {
         bytesRead =
            is.read(mybytearray, current, (mybytearray.length-current));
         if(bytesRead >= 0) current += bytesRead;
      } while(bytesRead > -1);

      bos.write(mybytearray, 0 , current);
      bos.flush();
	System.out.print("      downloaded 	...");
	for (int m=0;m<=100;m++){ System.out.print("\r"+m+"%");  Thread.sleep(30);}
	System.out.println();
      System.out.println("File " + FILE_TO_RECEIVED
          + " downloaded (" + current + " bytes read)");
	
	}



	//send the string to server - upload
	if(args[0].equals("upload")){
	String sendserver="upload-";
	sendserver=sendserver.concat(args[2]);
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes(sendserver + '\n');
	String FILE_TO_SEND=args[1];
	
	//message from server that file is received
	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	String servermsg = inFromClient.readLine();
	if(servermsg.equals("pathreceived")){

	//send file
	File myFile = new File (FILE_TO_SEND);
          byte [] mybytearray  = new byte [(int)myFile.length()];
          fis = new FileInputStream(myFile);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = sock.getOutputStream();
          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray,0,mybytearray.length);
          os.flush();
	System.out.print("     uploaded 	...");
	for (int m=0;m<=100;m++){ System.out.print("\r"+m+"%");  Thread.sleep(30);}
	System.out.println();
	     System.out.println("File " + FILE_TO_SEND
          + " uploaded");
		System.out.println("Error 0- NO ERROR");
	}
	}

	//getting directories from server's root
	if(args[0].equals("dir")){
	String sendserver="dir-";
	sendserver=sendserver.concat(args[1]);
	  DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes(sendserver + '\n');
	 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	String listOfFiles = inFromClient.readLine();
	if(!listOfFiles.equals("Error:0xxx002 Requested directory does not exist")){
	System.out.println("Error 0- NO ERROR");
	File file = new File(args[1]);
	String parentPath = file.getAbsoluteFile().getParent();
	System.out.print(parentPath+" ");
	System.out.println(listOfFiles);
		
	}
	else System.out.println(listOfFiles);
	}

	//making a new directory on server file system
	if(args[0].equals("mkdir")){
	String sendserver="mkdir-";
	sendserver=sendserver.concat(args[1]);
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes(sendserver + '\n');
	 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	String str = inFromClient.readLine();
	System.out.println(str+" "+ args[1]);
	}

	//removing a directory on server file system
	if(args[0].equals("rmdir")){
	String sendserver="rmdir-";
	sendserver=sendserver.concat(args[1]);
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes(sendserver + '\n');
	 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	String str = inFromClient.readLine();
	System.out.println(str+" "+ args[1]);
	}

	//removing a file on server file system
	if(args[0].equals("rm")){
	String sendserver="rm-";
	sendserver=sendserver.concat(args[1]);
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes(sendserver + '\n');
	 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	String str = inFromClient.readLine();
	System.out.println(str+" "+ args[1]);
	}

	//shutdown server file system
	if(args[0].equals("shutdown")){
	String sendserver="shutdown-";
	sendserver=sendserver.concat("shutdown");
	System.out.println("File server has been shutdown sucessfully ");
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes(sendserver + '\n');
	}
	
	//else System.out.println("command not available");
    }
    finally {
      if (fos != null) fos.close();
      if (bos != null) bos.close();
          if (bis != null) bis.close();
          if (os != null) os.close();
      if (sock != null) sock.close();
    }
  }

}