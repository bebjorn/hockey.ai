package AI;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.swing.JFrame;

import manCon.GameTable;

import core.AIBase;
import core.AIDummy;
import core.ControllablePlayer;

public class SimpleAI extends AIBase {
	private int activePlayer;
	private boolean forward, backward, cw, ccw;
	private JFrame frame;

	private Container contentPane;

	public SimpleAI(int localPort, SocketAddress gameAddress)
			throws IOException {
		super(localPort, gameAddress);
		frame = new JFrame("simple ai");
		frame.setVisible(true);

		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		// frame.add(new AnglePane());
		contentPane = frame.getContentPane();
		frame.setSize(1200, 800);
		frame.add(new GameTable(this));
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] arg) throws IOException {
		SocketAddress gameAddress = new InetSocketAddress("S2007", 60040);

		SimpleAI m = new SimpleAI(60090, gameAddress);

		SimpleAI d = new SimpleAI(60089, gameAddress);

	}

	@Override
	public void onNewState() {
		frame.repaint();
		//System.out.println("ai");
		doAI();
		//System.out.println("ai");
	}

	public void doAI() {
		//System.out.println(getPuck());
		for (ControllablePlayer cp : this.getFriendlyPlayers()) {
			//ControllablePlayer cp=this.getFriendlyPlayers().get(1);
			this.addOrder(cp.scootAndShoot());
			
	}
		try {
			send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
