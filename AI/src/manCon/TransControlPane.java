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

public class TransControlPane extends PlayerPanel implements ChangeListener{
	JSlider targetTransSlider;
	JSlider transSpeedSlider;
	final float C=255/360;
	TransControlPane(){
		
		
		setLayout(new GridLayout(1,2));

		targetTransSlider=new JSlider(JSlider.VERTICAL,0,100,50);
		targetTransSlider.setMajorTickSpacing(50);
		targetTransSlider.setMinorTickSpacing(20);
		targetTransSlider.setPaintTicks(true);
		targetTransSlider.setPaintLabels(true);

		transSpeedSlider=new JSlider(JSlider.VERTICAL,0,100,0);
		transSpeedSlider.setMajorTickSpacing(25);
		transSpeedSlider.setMinorTickSpacing(5);
		transSpeedSlider.setPaintTicks(true);
		transSpeedSlider.setPaintLabels(true);
		setTransSpeedListener(this);
		setTargetTransListener(this);
		//add(new JLabel("Target Angle"));
		add(targetTransSlider);
		//add(new JLabel("Rotation Speed"));
		add(transSpeedSlider);
		setSize(100,200);
	}
	public void setTransSpeedListener(ChangeListener a){
		transSpeedSlider.addChangeListener(a);
	}
	public void setTargetTransListener(ChangeListener a){
		targetTransSlider.addChangeListener(a);
	}
	public void stateChanged(ChangeEvent e) {
		if(transSpeedSlider.equals(e.getSource())){
			currentPlayer.setTransSpeed(transSpeedSlider.getValue());
		}
		if(targetTransSlider.equals(e.getSource())){
			currentPlayer.setTargetPos(targetTransSlider.getValue());
		}
	}
	

	public static void main(String[] args){
		JFrame frame = new JFrame("manual control");
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		//Container contentPane = frame.getContentPane();
		frame.setSize(400, 300);
		TransControlPane p=new TransControlPane();
		frame.add(p);
		frame.setVisible(true);
		
		
	}
	
	
	
}
