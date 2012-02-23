package manCon;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import core.AIBase;
import core.AIDummy;
import core.ControllablePlayer;
import core.DummyHandshake;
import core.PrimitiveOrder;

public class ManualControl extends AIBase implements ActionListener{// extends AIBase implements KeyListener {
	JFrame frame;
	JFrame frame2;
	PlayerNumberPane pPane;
	RotControlPane rotConPane;
	TransControlPane transConPane;
	AnglePane angPane;
	GameTable table;
	//ArrayList<ControllablePlayer> playerList=new ArrayList<ControllablePlayer>();
	ManualControl(int port ,SocketAddress adr) throws IOException{//int port ,SocketAddress adr
		super(port ,adr);
		
		PlayerPanel.currentPlayer=getFriendlyPlayers().get(0);
		frame = new JFrame("manual control");
		
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		//frame.add(new AnglePane());
		//contentPane = frame.getContentPane();
		frame.setSize(800, 500);
		
		pPane=new PlayerNumberPane(getFriendlyPlayers());
		rotConPane=new RotControlPane();
		transConPane=new TransControlPane();
		angPane=new AnglePane();
		JButton sendButton=new JButton("Send Order");
		sendButton.addActionListener(this);
		rotConPane.setTargetAngleListener(angPane);
		pPane.addPlayerPanel(angPane);
		pPane.addPlayerPanel(rotConPane);
		frame.add(pPane,BorderLayout.NORTH);
		frame.add(angPane,BorderLayout.CENTER);
		frame.add(rotConPane,BorderLayout.SOUTH);
		frame.add(transConPane,BorderLayout.WEST);
		frame.add(sendButton,BorderLayout.EAST);
		frame.setVisible(true);
		
		frame2 = new JFrame("GameTable");
		
		frame2.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame2.setLayout(new BorderLayout());
		//frame.add(new AnglePane());
		//contentPane = frame.getContentPane();
		frame2.setSize(800, 500);
		table=new GameTable(this);
		frame2.add(table);
		frame2.setVisible(true);
	}
	public static void main(String[] arg) throws IOException {
		SocketAddress gameAddress = new InetSocketAddress("S2007", 60040);

		ManualControl m = new ManualControl(60090, gameAddress);

		AIBase d = new AIDummy(60089, gameAddress);

	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			PlayerPanel.currentPlayer.sendOrder();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void onNewState() {
		
		frame.repaint();
		frame2.repaint();
		//System.out.println(PlayerPanel.currentPlayer.getCurrentPos());
		
	}
	

}
