package agent;

/**
 * A CasualLink indicates that an action a achieves a condition required by action b.
 * @author Björn Berntsson
 */
public class CausalLink {
	Action a,b;
	Condition achives;
	public CausalLink(Action a, Action b, Condition achives)
	{
		this.a = a;
		this.b = b;
		this.achives = achives;
	}
	
}
