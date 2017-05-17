import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;


public class Server {
 public final static int FILE_SIZE = 6022386;

  public static void main (String [] args ) throws IOException {
if(args[0].equals("start")){
int SOCKET_PORT = Integer.parseInt(args[1]); 
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    ServerSocket servsock = null;
    Socket sock = null;
    int bytesRead;
    int current = 0;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    try {
      servsock = new ServerSocket(SOCKET_PORT);
      while (true) {
        System.out.println("Waiting...");
        try {
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);

	//receive string from client
	
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	String clientinfo = inFromClient.readLine();
	String[] parts = clientinfo.split("-");
	String part1 = parts[0]; // command to execute
	String part2 = parts[1]; // path

	
	//download
	if(part1.equals("download")){
	String FILE_TO_SEND =part2;
          // send file
          File myFile = new File (FILE_TO_SEND);
	if(myFile.exists()){
          byte [] mybytearray  = new byte [(int)myFile.length()];
          fis = new FileInputStream(myFile);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = sock.getOutputStream();
          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray,0,mybytearray.length);
          os.flush();
          System.out.println("Done.");
	}
	else {
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes("Error:0xxx001 Requested file does not exist");
	}
	}
	
	//upload
	if(part1.equals("upload")){
	//send message to client 
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	String s="pathreceived";
	outToServer.writeBytes(s + '\n');
	String FILE_TO_RECEIVED=part2;
	//resume upload
	File f=new File(part2);
	long fs=0;
	if(f.exists()) fs=f.length();
	//receive file
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
     System.out.println("File " + FILE_TO_RECEIVED + " uploaded (" + current + " bytes read)");
	//String msg="filereceived";
	//outToServer.writeBytes(msg + '\n');
	System.out.println("DONE receiving the file uploaded");
	}

	//server directory files
	if(part1.equals("dir")){
	File folder = new File(part2);
	if(folder.exists()){
	File[] listOfFiles = folder.listFiles();
	String list="";
	

   	 for (int i = 0; i < listOfFiles.length; i++) {
      	if (listOfFiles[i].isFile()) {
       // System.out.println("File " + listOfFiles[i].getName());
	list+=listOfFiles[i].getName()+"  ";
      } else if (listOfFiles[i].isDirectory()) {
        //System.out.println("Directory " + listOfFiles[i].getName());
	list+=listOfFiles[i].getName()+" ";
      }
   	 }
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes(list + '\n');
	}
	else {
	DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
	outToServer.writeBytes("Error:0xxx002 Requested directory does not exist");
	}
	}
	
	//create new directory
	if(part1.equals("mkdir")){
	File file = new File(part2);
        if (!file.exists()) {
            if (file.mkdir()) {
               // System.out.println("Directory is created!");
		DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
		outToServer.writeBytes("Error 0- Directory is created on server: " + '\n');
            } else {
               // System.out.println("Failed to create directory!");
		DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
		outToServer.writeBytes("Error:: Directory is not created on server: " + '\n');
            }
        }
	else{
		DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
		outToServer.writeBytes("Error0xxx005:: Directory is not created on server: " + '\n');
	}
	}

	//remove directory
	if(part1.equals("rmdir")){
	File file = new File(part2);
        if (file.isDirectory()) {
            if (file.delete()) {
               // System.out.println("Directory is created!");
		DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
		outToServer.writeBytes("Error 0- Directory is removed on server: " + '\n');
            } else {
               // System.out.println("Failed to create directory!");
		DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
		outToServer.writeBytes("Error 0xxx007:: unable to remove directory on server: " + '\n');
            }
        }
	else{
		DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
		outToServer.writeBytes("Error0xxx006:: Directory is not present on server: " + '\n');
	}
	}


	//remove file
	if(part1.equals("rm")){
	File file = new File(part2);
            if (file.delete()) {
               // System.out.println("Directory is created!");
		DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
		outToServer.writeBytes("Error 0- File is removed on server: " + '\n');
            } else {
               // System.out.println("Failed to create directory!");
		DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
		outToServer.writeBytes("Error 0xxx008:: unable to remove file on server: " + '\n');
            }
	}
	
	//shutdown file server
	if(part1.equals("shutdown")){
	
         break;	
	}
	
        }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
      if (fos != null) fos.close();
      if (bos != null) bos.close();
          if (sock!=null) sock.close();
        }
      }
    }
    finally {
      if (servsock != null) servsock.close();
    }
}
else System.out.println("please give START command to start the server");
  }
}
