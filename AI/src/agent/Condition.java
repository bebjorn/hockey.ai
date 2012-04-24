package agent;

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
