package core;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Sender extends OrderList {
	
	DatagramSocket socket;
	SocketAddress destination;
	int port=60001;
	public Sender(DatagramSocket sock,SocketAddress gameAddress) throws SocketException, UnknownHostException{
		super();
		destination=gameAddress;
		socket = sock;
	}
	public void send() throws IOException{
		
		if(!isEmpty()){
			System.out.print(this);
			byte[] buf=getBytes();
			
			DatagramPacket data = new DatagramPacket(buf,buf.length,destination);
			socket.send(data);
			this.clear();
		}
	}
	public static void main(String[] arg) throws IOException{
		
		
			
		
	}
	public boolean add(PrimitiveOrder o){
		if(o!=null){
			return super.add(o);
		}
		return false;
	}
	
}
