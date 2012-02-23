package manCon;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import core.Player;
import core.Puck;

public class PuckRepresentation {
	Puck puck;
	//Oval pu=new Oval(0,0,10,10);
	public PuckRepresentation(Puck p){
		puck=p;
		
	}
	public void paint(Graphics2D g){
		Graphics2D g2d = (Graphics2D) g.create();
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2d.translate(puck.getX(), puck.getY());
	    
	    //System.out.println(puck.getX()+"\t"+ puck.getY());
	    //g2d.rotate(Math.toRadians(player.getCurrentRot()));
	    //g2d.scale(scale, scale);
	    g2d.fillOval(0, 0, 30, 30);
	}
	public String toString(){
		return puck.toString();
	}
	
}
