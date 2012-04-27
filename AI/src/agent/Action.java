package agent;
import java.util.ArrayList;
import core.*;

/**
 * A Action represent someting the agent can do.
 * @author Björn Berntsson
 *
 */
public class Action {
	public enum Act{
		Pass, Shoot, MoveWithPuck, CollectPuck, Move, NoOp, Start, NewGoal;
	}
	Player player1 = null, player2 = null;
	Vector from = null, to= null;
	Act act = null;
	boolean boundPassPlayer = true;
	boolean boundPassFromPos = true;
	/**
	 * The conditions witch needs to be true before the action can be executed
	 */
	public ArrayList<Condition> preconditions = new ArrayList<Condition>();
	/**
	 * The conditions that taking this action will generate. 
	 */
	public ArrayList<Condition> effects = new ArrayList<Condition>();
	
	public Action(Act act)//noOp, Start, Finish    maybe remove Finish
	{
		this.act = act;
	}
	// TODO: if the players are player 3 and 5 change the preconditions so as to satisfy a pass behind the goal
	public Action(Act act, Player player1, Player player2, Vector from, Vector to)// Pass
	{
		this.act = act;
		this.player1 = player1;
		this.player2 = player2;
		this.from = from;
		this.to = to;

		boundPassPlayer = false;
		boundPassFromPos = false;
		if(player1 != null && from != null)
		{
			boundPassPlayer = true;
			boundPassFromPos = true;
			preconditions.add(new CondHasPuck(player1, this));
			preconditions.add(new CondLocation(player1, from, this));
			preconditions.add(new CondPathClear(from, to, this));	
		}
		preconditions.add(new CondLocation(player2, to, this));
		effects.add(new CondHasPuck(player2, this));
	}
	public ArrayList<Condition> bindPassFromPlayer(Player player1)
	{
		ArrayList<Condition> ret = new ArrayList<Condition>();
		boundPassPlayer = true;
		this.player1 = player1;
		preconditions.add(new CondHasPuck(player1, this));
		ret.add(preconditions.get(preconditions.size()-1));
		return ret;	
	}
	public ArrayList<Condition> bindPassFromPos(Vector from)
	{
		ArrayList<Condition> ret = new ArrayList<Condition>();
		boundPassFromPos = true;
		this.from = from;
		preconditions.add(new CondLocation(player1, from, this));
		preconditions.add(new CondPathClear(from, to, this));
		ret.addAll(preconditions.subList(preconditions.size()-2, preconditions.size()));
		return ret;	
	}
	public Action(Act act, Player player1, Vector from, Vector to)// Shoot
	{
		this.act = act;
		this.player1 = player1;
		this.player2 = null;
		this.from = from;
		this.to = to;
		
		preconditions.add(new CondLocation(player1, from, this));
		preconditions.add(new CondHasPuck(player1, this));
		preconditions.add(new CondPathClear(from, to, this));
	}
	public Action(Act act, Player player1, Vector to)// Move, MoveWithPuck
	{
		this.act = act;
		this.player1 = player1;
		this.player2 = null;
		this.from = null;
		this.to = to;
		if(act.equals(Action.Act.MoveWithPuck))
			preconditions.add(new CondHasPuck(player1, this));
		effects.add(new CondLocation(player1, to, this));
	}
	public Action(Act act, Player player1)// CollectPuck
	{
		this.act = act;
		this.player1 = player1;
		this.player2 = null;
		this.from = null;
		this.to = null;
		
		preconditions.add(new CondCanGetPuck( player1, this));
		effects.add(new CondHasPuck(player1, this));
	}
	public boolean hasEffect(Condition cond)
	{
		for(Condition effect : effects)
		{
			if(effect.equals(cond))
				return true;
		}
		return false;
	}
	public boolean isActionWithPlayer(Player player)
	{
		return player == player1;
	}
	public void copyShoot(Action act2) {
		this.player1 = act2.player1;
		this.from = act2.from;
		this.to = act2.to;
		this.preconditions = act2.preconditions;
	}
}