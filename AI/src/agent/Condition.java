package agent;

/**
 * The parent class for all Conditions.
 * A Condition indicates something that is true, needs to be true or will be true.
 * @author Björn Berntsson
 */
public abstract class Condition {
	enum Name{
		HasPuck, CanGetPuck, Location, PathClear;
	}
	Name name;
	public Action onAction;
	public Condition(){}
	public abstract boolean equals(Condition cond);
	public abstract boolean negates(Condition cond);
}
