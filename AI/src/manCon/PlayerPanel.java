package manCon;

import core.ControllablePlayer;
import core.Player;



public class PlayerPanel extends javax.swing.JPanel {
	static ControllablePlayer currentPlayer;

	public ControllablePlayer getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(ControllablePlayer currentPlayer) {
		this.currentPlayer = currentPlayer;
		repaint();
	}
	
}
