package manCon;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import core.Player;

public class PlayerRepresentation {
	int x=100;
	int y=100;
	float rot=2;
	Player player;
	Rectangle rec=new Rectangle(-5,-5,10,50);
	public PlayerRepresentation(Player p){
		player=p;
		
	}
	public void paint(Graphics2D g){
		try{
		Graphics2D g2d = (Graphics2D) g.create();
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2d.translate(-player.getLocation().getX(), -player.getLocation().getY());
	    g2d.rotate(Math.toRadians(player.getCurrentRot()));
	    //g2d.scale(scale, scale);
	    g2d.fill(rec);
		}catch(NullPointerException e){}
	}
}
