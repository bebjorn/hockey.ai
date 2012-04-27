package agent;
import core.Vector;

/**
 * A Condition representing that a certain path the Agent want the puck to take is clear.
 * @author Björn Berntsson
 */
public class CondPathClear extends Condition{
	Vector from, to;
	public CondPathClear(Vector from, Vector to, Action action) // Location
	{
		this.name = Condition.Name.PathClear;
		this.from = from;
		this.to = to;
		onAction = action;
	}
	@Override
	public boolean equals(Condition cond)
	{
		if(name.equals(cond.name) && from.equals(((CondPathClear)cond).from) && to.equals( ((CondPathClear)cond).to))
			return true;
		return false;
	}
	@Override
	public boolean negates(Condition cond) { // cannot be checked here!!!!
		return false;
	}
}
