//Playas only!
package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.Image;
import java.awt.Point;

import javax.imageio.ImageIO;

public class Player {
	int id;
	int currentPos;
	private int currentRot;
	Vector location;
	BufferedImage reachMask;
	
	//PlayerPath path;
	public Vector[] path = new Vector[256];
	public Player(int i){
		id=i;
		/*Vector[] pathPoints=getPathPoints(i,Team.HOME);
		path=new PlayerPath(pathPoints);*/
		if(!getPathPoints(Team.HOME))
		{
			//failed
		}
		if(!getReachMask(Team.HOME))
		{
			//failed
		}
	}
	public Player(int i,Team team){
		id=i;
		/*Vector[] pathPoints=getPathPoints(i,team);
		path=new PlayerPath(pathPoints);*/
		if(!getPathPoints(team))
		{
			//failed
		}
		if(!getReachMask(team))
		{
			//failed
		}
	}
	private void setState(int pos,int rot){
		this.currentRot=rot*360/255;
		this.currentPos=pos;
		//location=path.getCoordinate(pos);
	}
	public int getCurrentRot() {
		return currentRot;
	}
	public int getCurrentPos() {
		return currentPos;
	}
	public Vector getLocation(){
		return path[currentPos];
	}
	public int getId(){
		return id;
	}
	private boolean getReachMask(Team team) {
		try{
			if(team == Team.HOME)
				reachMask = ImageIO.read(new File("player0"+id+".png"));
			else
				reachMask = ImageIO.read(new File("player1"+id+".png"));
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		return true;
	}
	private boolean getPathPoints(Team team) {		
		ArrayList<Vector> x = new ArrayList<Vector>();
		try {
			Scanner fileScan;
			if(team == Team.HOME)
			{
				File file = new File("player0"+id+".txt");
				fileScan = new Scanner(new File("player0"+id+".txt"));
			}
				
			else
				fileScan = new Scanner(new File("player1"+id+".txt"));
			while (fileScan.hasNext()) {
			      if (fileScan.hasNextDouble()) {
			        double xx = fileScan.nextDouble();
			        double yy = fileScan.nextDouble();
			        x.add(new Vector(xx, yy));
			      }
			      else {
			        String str = fileScan.next();
			        System.out.println("Data format error:"+str+"END");         
			      }
			}
		}
		catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		if(x.size() < 2)
			return false;
		
		Vector[] points= new Vector[x.size()];
		x.toArray(points);
		Vector[] relative = new Vector[x.size()];
		double[] length = new double[x.size()];
		length[0] = 0;
		for (int i = 1; i < x.size(); i++) {
			relative[i - 1] = (points[i].subtract(points[i-1]));
			length[i] = length[i - 1] + relative[i - 1].norm();
			//System.out.println(length[i]);
		}
		double totalLength = length[length.length - 1];
		
		for (int i = 0; i < 256; i++) {
			double p = i / 256.0;
			int q = 0;
			while (length[q] <= p * totalLength) {
				++q;
				if (q == points.length)
					break;
			}
			--q;
			double f = (p * totalLength - length[q]) / (length[q + 1] - length[q]);
			this.path[i] = points[q].add(relative[q].multiply(f));
		}
		return true;
	}
	
	public double getAngleToPoint(Vector point){
		return Math.atan2(point.getX()-location.getX(),point.getY()-location.getY());
	}
	//Wrong!!??
	public double getDistanceToPoint(Point2D point){
		return Math.sqrt(point.getX()*point.getX()+point.getY()*point.getY());
	}
	public boolean canReachVector(Vector p){
		if (p.x > reachMask.getWidth()/2 || p.y > reachMask.getHeight()/2 || p.x < -reachMask.getWidth()/2 || p.y < -reachMask.getWidth()/2)
			return false;
		
		if(reachMask.getRGB((int)p.x + reachMask.getWidth()/2, (int)p.y + reachMask.getHeight()/2) == -1)
			return true;		
		return false;
	}
	
	public String toString(){
		return "player "+id+"\tpos:"+this.getCurrentPos()+"\trot: "+this.getCurrentRot();
	}
	public static void main(String[] args) throws InterruptedException{
		Player p= new Player(0);
		p.getPathPoints(Team.HOME);
		for(int i=1;i<255;i=(i+10)){
			p.setState(i,0);
			System.out.println("pos: "+i+"\t"+p.getLocation());
			Thread.sleep(100);
		}
	}
	
	
}

