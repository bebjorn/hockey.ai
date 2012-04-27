package agent;

import core.Player;

/**
 * A condition indicating that player has the puck within his direct reach.
 * @author Björn Berntsson
 */
public class CondHasPuck extends Condition {
	Player player;
	public CondHasPuck(Player player, Action action) // CanGetPuck or HasPuck
	{
		this.name = Condition.Name.HasPuck;
		this.player = player;
		this.onAction = action;
	}
	@Override
	public boolean equals(Condition cond)
	{
		if(name.equals(cond.name) && player.equals(((CondHasPuck)cond).player))
			return true;
		return false;
	}
	@Override
	public boolean negates(Condition cond) { // is there no other condition with negates this??
		if(cond.name.equals(Condition.Name.HasPuck))
		{
			if(((CondHasPuck)cond).player != this.player )
				return true;
		}
		return false;
	}
}
