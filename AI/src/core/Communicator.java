package core;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

class Communicator {
	public static void main(String[] args) throws IOException {
		doHandshake(new InetSocketAddress("localhost", 60000));
	}

	static void doHandshake(InetSocketAddress adr) throws IOException {
		int localPort = 60095;
		int gamePort = 60040;
		int port = 60001;
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		try {
			socket1 = new DatagramSocket(new InetSocketAddress(
					InetAddress.getLocalHost(), localPort++));
			socket2 = new DatagramSocket(new InetSocketAddress(
					InetAddress.getLocalHost(), localPort++));

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] buf = "hello".getBytes();
		DatagramPacket data = new DatagramPacket(buf, buf.length,
				InetAddress.getLocalHost(), gamePort);
		socket1.send(data);
		System.out.println("waitning");
		DatagramPacket rcv = new DatagramPacket(new byte[1000], 1000);
		socket1.receive(rcv);
		System.out.println("this is " + new String(rcv.getData()) + "player");
		socket2.send(data);
		DatagramPacket rcv2 = new DatagramPacket(new byte[1000], 1000);
		socket2.receive(rcv2);
		System.out.println("this is " + new String(rcv.getData()) + "player");
		//new Thread(new Sender(socket1,gamePort)).start();
		for (;;) {
			long t=System.nanoTime();
			socket1.receive(rcv);
			byte[] pos = rcv.getData();
			int[] a = new int[rcv.getLength() / 4];
			
			for (int i = 0; i < rcv.getLength() / 4; i++) {
				for (int j = 0; j < 4; j++) {
					a[i] += (pos[4 * i + j] & 0xff) << (8 * j);
				}
			}
			System.out.println(System.nanoTime()-t);
		}
	}

	
}

