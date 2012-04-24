package agent;

import core.Player;
import core.Vector;

public class CondLocation extends Condition {
	Player player;
	Vector point;
	public CondLocation(Player player, Vector point, Action action) // Location
	{
		this.name = Condition.Name.Location;
		this.player = player;
		this.point = point;
		onAction = action;
	}
	@Override
	public boolean equals(Condition cond)
	{
		if(name.equals(cond.name) && player.equals(((CondLocation)cond).player) && point.equals( ((CondLocation)cond).point))
			return true;
		return false;
	}
	@Override
	public boolean negates(Condition cond) {
		if(cond.name.equals(Condition.Name.Location))
		{
			if(((CondLocation)cond).player == this.player && ((CondLocation)cond).point != this.point)
				return true;
		}
		return false;
	}
}
