package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DummyHandshake {
	public DummyHandshake(int localPort,SocketAddress gameAddress) throws IOException {
		//int localPort = 60095;
		//int gamePort = 60040;
		//int port = 60001;
		DatagramSocket socket = null;
		
		try {
			socket = new DatagramSocket(new InetSocketAddress(
					InetAddress.getLocalHost(), localPort));
			

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] buf = "hello".getBytes();
		DatagramPacket data = new DatagramPacket(buf, buf.length,
				gameAddress);
		socket.send(data);
		System.out.println("waitning");
		DatagramPacket rcv = new DatagramPacket(new byte[1000], 1000);
	}
}
