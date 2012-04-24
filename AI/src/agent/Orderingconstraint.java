package agent;

public class Orderingconstraint {
	Action before, after;
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
