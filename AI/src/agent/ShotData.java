package agent;

import core.Puck;
import core.Team;
import core.Vector;

public class ShotData {
	public int closestPos;
	public int pos;
	public boolean clockWise;
	public boolean possible;
	public ShotData()
	{
		
	}
	public ShotData calculateData(Action act, Puck puck, Team team)
	{
		int i = 1;
		Vector from = puck.subtract(act.to.subtract(act.from).multiply(/*TODO: puck radie*  */10/act.to.subtract(act.from).norm()));
		double xDiff = Math.abs((act.player1.path[0].getX()) - from.getX()); // -puck
		double yDiff = Math.abs((act.player1.path[0].getY()) - from.getY()); // -puck
		double oldDiff = yDiff+xDiff;
		double diff = 0;
		for(; i < 255; i++)
		{	
			xDiff = Math.abs(act.player1.path[i].getX() - from.getX()); // -puck
			yDiff = Math.abs(act.player1.path[i].getY() - from.getY()); // -puck
			diff = yDiff+xDiff;
			if(diff >= oldDiff)
			{
				i--;
				break;
			}
			oldDiff = diff;
		}
		this.closestPos = i;
		System.out.println("First: " + i);
		
		// Variables to account for:
		// different teams
		// different players
		// puck.y over or under player path[i].y
		// moving puck in positive or negative x direction
		// (club is located slightly in front of player base)
		
		// - if the pass is close to 90 degrees in respect to the player path we may have to move the puck closer to the player path.
		// - if the distance from the puck to the path[i] is very small and we do not wish to pass close to 90 degrees
		//		we may have to move the puck slightly away from the path[i]
		
		// when oldDiff is smaller then 0 we need to inc i if home team and dec i if away team. 
		int traverse = 0;
		
		from = puck.subtract(act.to.subtract(act.from).multiply(/*TODO: puck radie*  */5/act.to.subtract(act.from).norm()));
		double far2 = act.to.subtract(from).normSquared();
		double close2 = from.subtract(act.player1.path[i]).normSquared();
		double hyp2 = act.to.subtract(act.player1.path[i]).normSquared();
		oldDiff = far2+close2-hyp2;
		if(team == Team.HOME)
			traverse = (oldDiff < 0)? 1 : -1;
		else
			traverse = (oldDiff < 0)? -1 : 1;
		oldDiff = Math.abs(oldDiff);
		for(i = i+traverse; i > 0 && i < 255; i+=traverse)
		{	
			far2 = act.to.subtract(from).normSquared();
			close2 = from.subtract(act.player1.path[i]).normSquared();
			hyp2 = act.to.subtract(act.player1.path[i]).normSquared();
			diff = Math.abs(far2+close2-hyp2);
			if(diff >= oldDiff)
			{
				i-=traverse;
				break;
			}
			oldDiff = diff;
		}
		this.pos = i;
		System.out.println("Second: " + i);
		
		// set possible
		if(from.subtract(act.player1.path[i]).norm() > PlanningAI.CLUBREACH) // add smaller than too so that the player is not to close??
			this.possible = false;
		else
			this.possible = true;
		// end possible
		// set clockWise
		Vector fromSubTo = act.from.subtract(act.to);
		if(fromSubTo.getX() < 0 && fromSubTo.getY() < 0) // to is right and below of from
		{
			if(from.getX() > act.player1.path[pos].getX()) // Puck is right of player
			{
				clockWise = true;
			}
			else
			{
				clockWise = false;
			}
		}
		else if(fromSubTo.getX() < 0 && fromSubTo.getY() >= 0) // to is right and above of from
		{
			if(from.getX() > act.player1.path[pos].getX()) // Puck is right of player
			{
				clockWise = false;
			}
			else
			{
				clockWise = true;
			}
		}
		else if(fromSubTo.getX() >= 0 && fromSubTo.getY() < 0) // to is left and below of from
		{
			if(from.getX() > act.player1.path[pos].getX()) // Puck is right of player
			{
				clockWise = true;
			}
			else
			{
				clockWise = false;
			}
		}
		else if(fromSubTo.getX() >= 0 && fromSubTo.getY() >= 0) // to is left and above of from
		{
			if(from.getX() > act.player1.path[pos].getX()) // Puck is right of player
			{
				clockWise = false;
			}
			else
			{
				clockWise = true;
			}
		}
		// end clockWise
		return this;
	}
	
}
