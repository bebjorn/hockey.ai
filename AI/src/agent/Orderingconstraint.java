package agent;

/**
 * A Orderingconstraint reprecesents the fact that and action must happen before another.
 * @author Björn Berntsson
 */
public class Orderingconstraint {
	/**
	 * Must happen before "after".
	 */
	Action before;
	/**
	 * Must happen after "before".
	 */
	Action after;
	public Orderingconstraint(Action before, Action after)
	{
		this.before = before;
		this.after = after;
	}
	@Override
	public boolean equals(Object c)
	{
		if (((Orderingconstraint)c).before == before && ((Orderingconstraint)c).after == after)
			return true;
		return false;
	}
}
