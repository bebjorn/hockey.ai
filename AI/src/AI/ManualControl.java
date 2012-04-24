package AI;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.swing.JFrame;

import manCon.GameTable;

import core.AIBase;
import core.AIDummy;
import core.DummyHandshake;
import core.PrimitiveOrder;

public class ManualControl extends AIBase implements KeyListener {
	private int activePlayer;
	private boolean forward, backward, cw, ccw;
	private JFrame frame;

	private Container contentPane;
	
	ManualControl(int localPort, SocketAddress gameAddress) throws IOException {
		super(localPort, gameAddress);
		frame = new JFrame("manual control");
		frame.setVisible(true);
		frame.addKeyListener(this);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		//frame.add(new AnglePane());
		contentPane = frame.getContentPane();
		frame.setSize(1200, 800);
		frame.add(new GameTable(this));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		char key = arg0.getKeyChar();
		int pl = Character.digit(key, 10);
		if (pl >= 0 && pl < 6) {
			activePlayer = pl;
			//System.out.println(pl);
		}
		if (arg0.getKeyCode() == KeyEvent.VK_UP) {
			forward = true;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
			backward = true;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
			cw = true;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
			ccw = true;
		}
		updatePlayerMovement();

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getKeyCode() == KeyEvent.VK_UP) {
			forward = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
			backward = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
			cw = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
			ccw = false;
		}
		//updatePlayerMovement();
	}

	void updatePlayerMovement() {
		int moveSpeed = 100;
		int rotSpeed = 100;
		int destinationPos = getFriendlyPlayers().get(activePlayer).getCurrentPos();
		int destinationRot = getOpposingPlayers().get(activePlayer).getCurrentRot();
		int rot = 0;
		int maxPos = 255;
		int minPos = 0;

		if (forward && !backward) {
			destinationPos = maxPos;
		} else if (!forward && backward) {
			destinationPos = minPos;
		} else {
			moveSpeed = 0;
		}
		if (ccw && !cw) {
			rot = -rotSpeed;// rotSpeed;
			destinationRot =191;
		} else if (!ccw && cw) {
			rot = rotSpeed;
			
			destinationRot = 64;
		}
		addOrder(new PrimitiveOrder(activePlayer, moveSpeed, destinationPos,
				rot, destinationRot));
		try {
			send();
			System.out.println("command sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] arg) throws IOException {
		SocketAddress gameAddress = new InetSocketAddress("S2007", 60040);

		//ManualControl m = 
				new ManualControl(60090, gameAddress);

		//ManualControl d = 
				new ManualControl(60089, gameAddress);

	}

	@Override
	public void onNewState() {
		frame.repaint();
		
	}

}
