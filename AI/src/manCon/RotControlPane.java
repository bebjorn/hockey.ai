package manCon;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RotControlPane extends PlayerPanel implements ChangeListener{
	JSlider targetAngleSlider;
	JSlider rotSpeedSlider;
	final float C=255/360;
	RotControlPane(){
		
		
		setLayout(new GridLayout(2,1));

		targetAngleSlider=new JSlider(JSlider.HORIZONTAL,0,360,180);
		targetAngleSlider.setMajorTickSpacing(45);
		targetAngleSlider.setMinorTickSpacing(15);
		targetAngleSlider.setPaintTicks(true);
		targetAngleSlider.setPaintLabels(true);

		rotSpeedSlider=new JSlider(JSlider.HORIZONTAL,-100,100,0);
		rotSpeedSlider.setMajorTickSpacing(25);
		rotSpeedSlider.setMinorTickSpacing(5);
		rotSpeedSlider.setPaintTicks(true);
		rotSpeedSlider.setPaintLabels(true);
		setRotSpeedListener(this);
		setTargetAngleListener(this);
		//add(new JLabel("Target Angle"));
		add(targetAngleSlider);
		//add(new JLabel("Rotation Speed"));
		add(rotSpeedSlider);
		setSize(200,100);
	}
	public void setRotSpeedListener(ChangeListener a){
		rotSpeedSlider.addChangeListener(a);
	}
	public void setTargetAngleListener(ChangeListener a){
		targetAngleSlider.addChangeListener(a);
	}
	public void stateChanged(ChangeEvent e) {
		if(rotSpeedSlider.equals(e.getSource())){
			currentPlayer.setRotSpeed(rotSpeedSlider.getValue());
		}
		if(targetAngleSlider.equals(e.getSource())){
			currentPlayer.setTargetRot(targetAngleSlider.getValue());
		}
	}
	

	public static void main(String[] args){
		JFrame frame = new JFrame("manual control");
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		//Container contentPane = frame.getContentPane();
		frame.setSize(400, 300);
		RotControlPane p=new RotControlPane();
		frame.add(p);
		frame.setVisible(true);
		
		
	}
	
	
	
}
