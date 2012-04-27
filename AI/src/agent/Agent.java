package agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import core.Player;
import core.Puck;
import core.TeamOfPlayers;
import core.Vector;


/**
 * Agent plans attacks.
 * @author Björn Berntsson
 */
public class Agent {
	/**
	 *  The actions in the plan
	 */
	ArrayList<Action> plan = new ArrayList<Action>();
	/**
	 *  The ordering constraints between the actions
	 */
	ArrayList<Orderingconstraint> orderingConstraints = new ArrayList<Orderingconstraint>();
	/**
	 *  The casual links between the actions
	 */
	ArrayList<CausalLink> causalLinks = new ArrayList<CausalLink>();
	/**
	 *  The Conditions of the actions witch are not yet met.
	 */
	ArrayList<Condition> openConditions = new ArrayList<Condition>();
	
	
	/**
	 * The Action that contain the current state of the "real" world.
	 */
	public Action current;
	/**
	 * The Action that represents the goal that the agent will try to reach.
	 */
	public Action shoot;
	Puck puck;
	private TeamOfPlayers friendlyPlayers;
	private TeamOfPlayers opposingPlayers;
	public Agent(Puck puck, TeamOfPlayers friendlyPlayers, TeamOfPlayers opposingPlayers)
	{
		this.puck = puck;
		this.friendlyPlayers = friendlyPlayers;
		this.opposingPlayers = opposingPlayers;
		reset();
	}
	public void reset()
	{
		plan = new ArrayList<Action>();
		current = new Action(Action.Act.Start);
		shoot = new Action(Action.Act.Shoot);
		orderingConstraints = new ArrayList<Orderingconstraint>();
		causalLinks = new ArrayList<CausalLink>();
		openConditions = new ArrayList<Condition>();
		plan.add(current);
		plan.add(shoot);
		orderingConstraints.add(new Orderingconstraint(current, shoot));
	}
	public void setNewGoal(Action act)//ArrayList<Condition> goals)
	{
		reset();
		shoot.copyShoot(act);//preconditions = goals;
		openConditions.addAll(shoot.preconditions);
	}
	//-------remove flaw, maybe give a action.--------
	/**
	 * Refines the plan by doing one of several different steps.
	 * @return A noOp Action or the next action to take
	 */
	public Action Continuous_Pop_Agent()
	{
		Action action = new Action(Action.Act.NoOp);
		//current.effects = current;
		
		//--add/remove/change goal--
		//shoot.preconditions = goal;
		
		// remove unsupported link     
		for(CausalLink link : causalLinks)
		{
			if(link.a == current )
			{
				if(!current.hasEffect(link.achives))
				{
					openConditions.add(link.achives);
					causalLinks.remove(link);
					return action;
				}
			}
		}
		// remove redundant action
		for(Action act : plan)
		{
			if(act == current || act == shoot)// not start or shoot
				continue;
			boolean exist = false;
			
			for(CausalLink link : causalLinks)
			{
				if (link.a == act)
				{
					exist = true;
					break;
				}
			}
			if(!exist)
			{
				plan.remove(act);
				// remove links
				return action;
			}
		}
		// --add casual link to a open precondition, with new or existing action(see POP)--
		Condition open = null;
		if(!openConditions.isEmpty())
		{
			/*for(Condition cond : openConditions) // take On conditions first, the resulting moves often satisfy clear conditions
			{
				if(cond.name.equals(Condition.Name.On))
				{
					open = cond;
					openConditions.remove(cond);
					break;
				}
			}*/
			if(open == null)
				open = openConditions.remove(openConditions.size()-1);
			
			
			
			choseAction(open); // pick/add a action to satisfy the open condition, and
			resolveThreats();  // -- resolve any resulting conflicts(see POP)
			
			if (!consistant())
				return null;	// backtrack???? or simply pick a new goal and hope for the best
			return action;
		}
		// try to act
		for(Action act : plan)
		{
			if(act == current || act.act.equals(Action.Act.Pass))// not start or passes
				continue;
			//try to execute action
			Action doAction = takeAction(act);
			if (doAction != null)
				return doAction;
		}
		// if we cant try to bind a pass
		for(Action act : plan)
		{
			if(!act.act.equals(Action.Act.Pass))// only passes
				continue;
			// bind Player 1 in any unbound passes
			if(act.boundPassPlayer == false)
			{
			ArrayList<Player> passers = getPassers(act.player2);
			for(Iterator<Player> iter = passers.iterator(); iter.hasNext();)
			{
				Player player = iter.next();
				if(current.hasEffect(new CondCanGetPuck(player, null)))
				{
					openConditions.addAll(act.bindPassFromPlayer(player));
					return action;
				}
			}
			// if no one of the "passers" could get the puck.
			// pick a defender and make him get passed???
			if(passers.size() != 0)
			{
				Player player = passers.get(new Random().nextInt(passers.size()));
				if(act.player2.getId() == 5)
					player = friendlyPlayers.get(2);
				else if(act.player2.getId() == 4)
					player = friendlyPlayers.get(new Random().nextInt(2)+1);
				else
					player = friendlyPlayers.get(1);
				openConditions.addAll(act.bindPassFromPlayer(player));
				return action;
			}
			return action; // tried to pass the goal keeper, bad idea
		}
		// bind from in any unbound passes
		if(act.boundPassFromPos == false)
		{
			Random rand = new Random();
			if(rand.nextInt(20) != 1)
				//if(act.player1.getId() == 1 || act.player1.getId() == 2)
				//	openConditions.addAll(act.bindPassFromPos(act.player1.path[100]));
				//else
					openConditions.addAll(act.bindPassFromPos(act.player1.getLocation()));
			else // prevents lock if no pass is possible from current location.
				openConditions.addAll(act.bindPassFromPos(act.player1.path[rand.nextInt(255)]));
			return action;
		}
		if(!PathClear(act.from, act.to))
		{
			System.out.println("Pass not clear, From: " + act.player1 + " To: " + act.player2);
			action.act = Action.Act.NewGoal;
			return action;
		}
		Action doAction = takeAction(act);
		if (doAction != null)
			return doAction;
		}
		// remove unnecessary goal?????
		// -------------------------------------------------
		return action; // should not arrive here!! (until finished)
	}
	private Action takeAction(Action act) 
	{	
		//Check if the actions preconditions and ordering constraints are satisfied
		boolean satisfied = true;
		for(Orderingconstraint order :  orderingConstraints)
		{
			if(order.after == act && order.before != current)
			{
				satisfied = false;
				break;
			}
		}
		if (satisfied) // maybe bind variables to the appropriate current values
		{
			for(Condition cond : act.preconditions)
			{
				if(!current.hasEffect(cond))
				{
					satisfied = false;
					break;
				}
			}
		}
		if(satisfied)
		{	
			// remove action and its links
			for (Iterator<CausalLink> iter = causalLinks.iterator();iter.hasNext();)
			{
				CausalLink link = iter.next();
				if(link.a == act)
				{
					openConditions.add(link.achives); // nytt
					iter.remove();
				}
				else if(link.b == act)
					iter.remove();
			}
			for (Iterator<Orderingconstraint> iter = orderingConstraints.iterator();iter.hasNext();)
			{
				Orderingconstraint constraint = iter.next();
				if(constraint.after == act || constraint.before == act)
					iter.remove();
			}
			plan.remove(act);
			//TEST
			if (act.act.equals(Action.Act.Move) && act.player1.getLocation().shortestVectorDistance(puck) < PlanningAI.CLUBREACH)
				act.act = Action.Act.MoveWithPuck;
			//END TEST
			return act; // the action should be executed.
		}
		return null; // the action should not be executed.
	}
	
	private ArrayList<Player> getPassers(Player player) {
		ArrayList<Player> players = new ArrayList<Player>();
		switch(player.getId())
		{
		case 0:
			break;
		case 1:
			players.add(friendlyPlayers.get(0));
			break;
		case 2:
			players.add(friendlyPlayers.get(0));
			break;
		case 3:
			players.add(friendlyPlayers.get(1));
			players.add(friendlyPlayers.get(4));
			players.add(friendlyPlayers.get(5));
			break;
		case 4:
			players.add(friendlyPlayers.get(1));
			players.add(friendlyPlayers.get(2));
			players.add(friendlyPlayers.get(3));
			players.add(friendlyPlayers.get(5));
			break;
		case 5:
			players.add(friendlyPlayers.get(2));
			players.add(friendlyPlayers.get(3));
			players.add(friendlyPlayers.get(4));
			break;
		default:
			break;
		}
		return players;
	}
	// check for cycles in the orderings
    private boolean consistant() {
		return true;
	}
	private void choseAction(Condition cond) {
		// check if there are any actions in the plan that has this effect
		boolean actionFromPlan = false;
		Action ChosenAction = null;
		
		if(cond.name.equals(Condition.Name.CanGetPuck))
		{
			if (((CondCanGetPuck)cond).player.canReachVector(puck))
			{
				current.effects.add(new CondCanGetPuck(((CondCanGetPuck)cond).player, current));
				ChosenAction = current;
				actionFromPlan = true;
			}
		}
		else
		{
			for(Action action : plan)
			{
				if(!action.act.equals(Action.Act.Shoot))
				{
					if(action.hasEffect(cond))
					{
						ChosenAction = action;
						actionFromPlan = true;
						break;
					}
				}
			}
		}
		//if not add an action to the plan
		if(!actionFromPlan)
		{
			if(cond.name.equals(Condition.Name.CanGetPuck))
			{
				// if this was not possible in start then we need to switch to defense mode.
			}
			else if(cond.name.equals(Condition.Name.HasPuck))
			{
				//chose collect puck if possible NOW 
				//else chose pass with PLAYER1 FROM and TO unbound!
				if(((CondHasPuck)cond).player.canReachVector(puck))
					ChosenAction = new Action(Action.Act.CollectPuck, ((CondHasPuck)cond).player);
				else
				{
					if(((CondHasPuck)cond).player.getId() < 3) // defender
						ChosenAction = new Action(Action.Act.Pass, null, ((CondHasPuck)cond).player, null, ((CondHasPuck)cond).player.path[100]);
					else // attacker 
						ChosenAction = new Action(Action.Act.Pass, null, ((CondHasPuck)cond).player, null, ((CondHasPuck)cond).player.getLocation());
				}
				// the to here (last variable) is temporary
				
				plan.add(ChosenAction);
				openConditions.addAll(ChosenAction.preconditions);
				orderingConstraints.add(new Orderingconstraint(current, ChosenAction));
				orderingConstraints.add(new Orderingconstraint(ChosenAction, shoot));
			}
			else if(cond.name.equals(Condition.Name.Location))
			{ 
				// maybe remove moveWithPuck and only use it in the background??
				//else chose Move
				ChosenAction = new Action(Action.Act.Move, ((CondLocation)cond).player, ((CondLocation)cond).point);
				

				plan.add(ChosenAction);
				openConditions.addAll(ChosenAction.preconditions);
				orderingConstraints.add(new Orderingconstraint(current, ChosenAction));
				orderingConstraints.add(new Orderingconstraint(ChosenAction, shoot));
			}
			else if(cond.name.equals(Condition.Name.PathClear))
			{
				// Check now and chose new goal if false
				// !!!! testing this:or ignore now and check when choosing actions!!!!,  or check when verifying current links.
				ChosenAction = current;
				current.effects.add(new CondPathClear(((CondPathClear)cond).from,  ((CondPathClear)cond).to, current));
			}
		}
		causalLinks.add(new CausalLink(ChosenAction,cond.onAction, cond));
		//if it cant be done try another action?????
	}
	public boolean PathClear(Vector from, Vector to)
	{
		Vector v = to.subtract(from);
		
		v.multiplyInPlace(0.02);
		Vector move = from.add(new Vector(0,0));
		for(int i = 0; i<50; i++)
		{
			move.addInPlace(v);
			
			for(Player player: opposingPlayers)
			{
				if(player.canReachVector(move));
					if((int)player.getLocation().shortestVectorDistance(move) < PlanningAI.CLUBREACH)
					{
						return false;
					}
			}
		}
		return true;
	}
	private void resolveThreats()
	{
		for(CausalLink link : causalLinks)
		{
			for(Action action : plan)
			{
				if(action != link.a && action != link.b)
				{
					for(Condition cond : action.effects)
					{
						if(cond.negates(link.achives))
						{
							if(!orderingConstraints.contains(new Orderingconstraint(action, link.a)) && !orderingConstraints.contains(new Orderingconstraint(link.b,action)))
							{
								if(link.a.act.equals(Action.Act.CollectPuck) || link.b.act.equals(Action.Act.CollectPuck) || action.equals(Action.Act.CollectPuck))
								{
									int i = 1;
								}
								if(link.a != current && !link.a.equals(Action.Act.CollectPuck)) // collectPuck must always happen first
									orderingConstraints.add(new Orderingconstraint(action, link.a));
								else if(link.b != shoot && !link.b.equals(Action.Act.CollectPuck))
									orderingConstraints.add(new Orderingconstraint(link.b, action));
								else
								{
									char a = 'c'; /// bad!!
									System.out.println("BAD!!!");
									// maybe remove the ordering constraint and the link between current and shoot, (add achieves to openConditions)
									// so as to make room for action in the plan.
								}
							}
						}
					}
				}
			}
		}
	}
}