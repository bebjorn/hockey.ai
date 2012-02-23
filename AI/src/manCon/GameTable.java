package manCon;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import core.*;

public class GameTable extends JPanel {
	private BufferedImage image;
	PlayerRepresentation[] players=new PlayerRepresentation[12];
	PuckRepresentation puck;
	Dimension dim=new Dimension(900,480);
    public GameTable(AIBase game) {
    	//JFrame frame=new JFrame();
    	//frame.add(this);
    	//frame.setVisible(true);
    	int i=0;
    	for(Player p:game.getFriendlyPlayers()){
    		players[i++]=new PlayerRepresentation(p);
    	}
    	for(Player p:game.getOpposingPlayers()){
    		players[i++]=new PlayerRepresentation(p);
    	}
    	puck=new PuckRepresentation(game.getPuck());
       try {                
          image = ImageIO.read(new File("src/resources/playfield.bmp"));
          
          
          this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
          
       } catch (IOException ex) {
            // handle exception...
       }
    }

    @Override
    public void paintComponent(Graphics g) {
    	
    	Graphics2D g2=(Graphics2D) g.create();
    	float scale=900/image.getWidth();
    	g2.scale(scale,scale);
    	g2.drawImage(image, 0, 0, null);
    	g.translate(450, 230);
        for(int i=0;i<players.length;i++){
        	players[i].paint((Graphics2D) g);
        }
        
        puck.paint((Graphics2D) g);
        g.drawString("PUCK: " + puck.toString() , 10, 10);
        
    }
    public static void main(String[] arg){
    	
    }
}
