package agent;

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
