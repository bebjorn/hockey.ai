package manCon;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class AnglePane extends PlayerPanel implements ChangeListener{
	//int targetAngle=45;
	//int currentAngle=20;
	int radius;
	int iconSize=10;
	AnglePane(){
		setSize(200,200);
		
		
	}
	
	public void paint(Graphics g){
		super.paint(g);
		if(getHeight()>getWidth()){
			radius=getWidth()/2-iconSize;
		}else{
			radius=getHeight()/2-iconSize;
		}
		int centerX=getWidth()/2;
		int centerY=getHeight()/2;
		int cuX=centerX+(int)(radius*Math.sin(Math.toRadians(currentPlayer.getCurrentRot())));
		int cuY=centerY+(int)(radius*Math.cos(Math.toRadians(currentPlayer.getCurrentRot())));
		int taX=centerX+(int)(radius*Math.sin(Math.toRadians(currentPlayer.getTargetRot()))/2);
		int taY=centerY+(int)(radius*Math.cos(Math.toRadians(currentPlayer.getTargetRot()))/2);
		
		g.drawRect(cuX-iconSize/2, cuY-iconSize/2, iconSize, iconSize);
		g.drawOval(taX-iconSize/2, taY-iconSize/2, iconSize, iconSize);
		g.setColor(Color.BLACK);
		g.fillOval(centerX, centerY, iconSize, iconSize);
		//System.out.println(currentPlayer.getId());
	}
	public void stateChanged(ChangeEvent e) {
		repaint();
	}
//	public int getTargetAngle() {
//		return targetAngle;
//	}
//	public void setTargetAngle(int targetAngle) {
//		this.targetAngle = targetAngle;
//	}
//	public int getCurrentAngle() {
//		return currentAngle;
//	}
//	public void setCurrentAngle(int currentAngle) {
//		this.currentAngle = currentAngle;
//	}
	public static void main(String[] args){
		JFrame frame = new JFrame("manual control");
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		
		Container contentPane = frame.getContentPane();
		AnglePane aP=new AnglePane();
		contentPane.add(aP);
		
		frame.setSize(400, 300);
		frame.repaint();
		int ang1=40;
		int ang2=70;
		while(true){
			ang1+=2;
			ang2-=1;
			
			frame.repaint();
			try
			{
			Thread.sleep(75); // do nothing for 1000 miliseconds (1 second)
			}
			catch(InterruptedException e)
			{
			e.printStackTrace();
			}
			
		}
	}

	
	
}
