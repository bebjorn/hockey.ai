package agent;

import agent.Condition.Name;
import core.Player;
import core.Vector;

public class CondCanGetPuck extends Condition {
	Player player;
	public CondCanGetPuck(Player player, Action action) // CanGetPuck or HasPuck
	{
		this.name = Condition.Name.CanGetPuck;
		this.player = player;
		this.onAction = action;
	}
	@Override
	public boolean equals(Condition cond)
	{
		if(name.equals(cond.name) && player.equals(((CondCanGetPuck)cond).player))
			return true;
		return false;
	}
	@Override
	public boolean negates(Condition cond) { // is this correct??
		if(cond.name.equals(Condition.Name.CanGetPuck))
		{
			if(((CondCanGetPuck)cond).player != this.player )
				return true;
		}
		return false;
	}
	
}
