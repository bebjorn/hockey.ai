package agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import AI.SimpleAI;
import agent.*;
import core.*;

public class PlanningAI extends AIBase{
	public static final int CLUBREACH = 20;
	private Team team;
	private TeamOfPlayers friendlyPlayers= new TeamOfPlayers();
	private TeamOfPlayers opposingPlayers= new TeamOfPlayers();
	private Puck puck;
	private int gameTime=0;
	
	private Agent agent;
	Date d=new Date();
	/**
	 * Creates and initaializes basic AI functionality and connects it to the game at the selected adress
	 * @param gameAddress 
	 * @param port 
	 * @param localPort
	 * @param gameAddress
	 * @throws IOException
	 */
	public PlanningAI(int port, SocketAddress gameAddress) throws IOException{
		super(port, gameAddress);
		
		puck=new Puck();
		//puck.setState(-240,5);//(-140, 90);(60, 10);//
		team = Team.HOME;
		Team otherTeam;
		if(team.equals(Team.HOME)){
			otherTeam=Team.AWAY;
		}else{
			otherTeam=Team.HOME;
		}
		for(int i=0;i<6;i++){
			friendlyPlayers.add(new ControllablePlayer(i));
			opposingPlayers.add(new Player(i,otherTeam));
		}
		opposingPlayers.get(0).setState(110, 0);
		opposingPlayers.get(2).setState(80, 0);
		/*Table table = new Table(getFriendlyPlayers(), getOpposingPlayers(), getPuck());
		frame = new Frame();
		frame.add(table);*/
		agent = new Agent(puck, friendlyPlayers, opposingPlayers);
	}
	public Puck getPuck(){
		return puck;
	}
	public Team getTeam(){
		return team;
	}
	public TeamOfPlayers getFriendlyPlayers(){
		return friendlyPlayers;
	}
	public TeamOfPlayers getOpposingPlayers(){
		return opposingPlayers;
	}
	
	private ArrayList<Condition> getCurrentEffects()
	{
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for(Condition cond : agent.current.effects)
		{
			if(cond.name.equals(Condition.Name.PathClear))
			{
				//if path is still clear add
				//if(agent.PathClear(((CondPathClear)cond).from, ((CondPathClear)cond).to))
					conditions.add(cond);
				//else // debug
					//System.out.println("Path was not clear From: " + ((CondPathClear)cond).from +" To: " + ((CondPathClear)cond).to);
			}
			/*if(cond.name.equals(Condition.Name.HasPuck))
			{
				// if still has add
				conditions.add(cond);
			}*/
			/*if(cond.name.equals(Condition.Name.CanGetPuck))
			{
				if(((CondCanGetPuck)cond).player.canReachVector(getPuck()))
					conditions.add(cond);
			}*/
		}
		
		for(Player player : friendlyPlayers)
		{
			conditions.add(new CondLocation(player, player.getLocation(),agent.current));
			
			if((int)player.getLocation().shortestVectorDistance(getPuck()) < CLUBREACH) 
			{
				conditions.add(new CondHasPuck(player,agent.current));
			}
			if(player.canReachVector(getPuck()))
			{
				conditions.add(new CondCanGetPuck(player, agent.current));
			}
		}
		return conditions;
	}
	private Action pickShot()
	{
		Action act = null;
		Random rand = new Random();
		if(puck.getY() >= 0)
		{
			
			if(rand.nextInt(2) == 1)
			{
				act = new Action(Action.Act.Shoot, friendlyPlayers.get(4), friendlyPlayers.get(4).path[rand.nextInt(256)], friendlyPlayers.get(5).path[180]);
				return act;
			}
			else
			{
				act = new Action(Action.Act.Shoot, friendlyPlayers.get(3), friendlyPlayers.get(3).path[rand.nextInt(150)], friendlyPlayers.get(5).path[255]);
				return act;
			}
				
			//if path is clear player 4 or 3 shoot
		}
		else
		{
			if(rand.nextInt(2) == 1)
			{
				act = new Action(Action.Act.Shoot, friendlyPlayers.get(4), friendlyPlayers.get(4).path[rand.nextInt(150)], friendlyPlayers.get(5).path[180]);
				return act;
			}
			else
			{
				act = new Action(Action.Act.Shoot, friendlyPlayers.get(5), friendlyPlayers.get(5).path[30], friendlyPlayers.get(3).path[255]);
				return act;
			}
			//if path is clear player 4 or 5 shoot
		}
		/*act = new Action(Action.Act.Shoot, friendlyPlayers.get(3), friendlyPlayers.get(3).path[100], friendlyPlayers.get(4).path[255]);
		return act;//.preconditions;*/
	}
	
	enum MainState{ATTACKING, DEFENDING;}
	enum State {PLANNING, SHOOTING, PASSING, COLLECTINGPUCK, MOVING, MOVINGWITHPUCK;}
	MainState mainState = MainState.ATTACKING;
	State state = State.PLANNING;
	Action act;
	
	public void onNewState() {
		
		//getPassOrShotPos(new Action(Act.Shoot, friendlyPlayers.get(4), puck, new Vector(150, 150)));
		
		// set current.effects
		agent.current.effects = getCurrentEffects();
		// make sure we have a goal
		if(mainState.equals(MainState.ATTACKING))
		{
			if(state.equals(State.PLANNING))
			{
				act = new Action(Action.Act.NoOp);
				if(agent.shoot.from == null)
				{
					agent.setNewGoal(pickShot());
					agent.current.effects = getCurrentEffects();
				}
				// find a action
				while (act.act.equals(Action.Act.NoOp))
				{
					act = agent.Continuous_Pop_Agent();
					if (act == null) // if null is returned go back to defending
					{
						agent.reset();
						mainState = MainState.DEFENDING;
						state = State.PLANNING;
						return;
					}
					else if(act.act.equals(Action.Act.NewGoal))// try a new goal.
					{
						agent.reset();
						state = State.PLANNING;
						return;
					}
				}
				if(act.act.equals(Action.Act.Move))
					state = State.MOVING;
				else if(act.act.equals(Action.Act.MoveWithPuck))
					state = State.MOVINGWITHPUCK;
				else if(act.act.equals(Action.Act.CollectPuck))
					state = State.COLLECTINGPUCK;
				else if(act.act.equals(Action.Act.Pass))
					state = State.PASSING;
				else if(act.act.equals(Action.Act.Shoot))
					state = State.SHOOTING;
			}
			if(state.equals(State.MOVING))
			{
				// 1. åk till destination
				int i = 0;
				for(; i < 255; i++)
				{
					if(act.player1.path[i].equals(act.to))
						break;
				}
				act.player1.setState(i, 0);
				state = State.PLANNING;
			}
			else if(state.equals(State.MOVINGWITHPUCK))
			{
				
				// 1. lägg puck tillrätta??
				// 2. Åk mot destination med liten "innåtvinkel"
				int i = 0;
				for(; i < 256; i++)
				{
					if(act.player1.path[i].equals(act.to))
						break;
				}
				act.player1.setState(i, 0);
				
				//puck.setState((int)act.player1.getLocation().getX(), (int)act.player1.getLocation().getY());
				state = State.PLANNING;
			}
			else if(state.equals(State.COLLECTINGPUCK))
			{
				// 1. åk mot puck med liten "innåtvinkel"
				int i = 1;
				double xDiff = Math.abs((act.player1.path[0].getX()) - puck.getX());
				double yDiff = Math.abs((act.player1.path[0].getY()) - puck.getY());
				double oldDiff = yDiff+xDiff;
				double diff = 0;
				for(; i < 255; i++)
				{	
					xDiff = Math.abs((act.player1.path[i].getX()) - puck.getX());
					yDiff = Math.abs((act.player1.path[i].getY()) - puck.getY());
					diff = yDiff+xDiff;
					if(diff >= oldDiff)
					{
						i--;
						break;
					}
					oldDiff = diff;
				}
				
				act.player1.setState(i, 0);
				state = State.PLANNING;
			}
			else if(state.equals(State.PASSING))
			{
				// 1. Lägg puck tillrätta om det behövs + vinkla motagare
				// 2. placera spelaren
				// 3. passa
				
				// 2:
				act.player1.setState(getPassOrShotPos(act),0);
				// 3: 
				act.player1.setState(act.player1.getCurrentPos(), 100);
				//puck.setState((int)act.player2.getLocation().getX(), (int)act.player2.getLocation().getY());
				
				state = State.PLANNING;
			}
			else if(state.equals(State.SHOOTING))
			{
				// 1. Lägg pucken tillrätta om det behövs??
				// 2. placera spelaren
				// 2. skut
				//puck.setState((int)act.to.getX(), (int)act.to.getY()); // in goal
				act.from = null; // ugly fix for detecting when we have a goal or not
				state = State.PLANNING;
			}
			try {Thread.sleep(1000);} catch(InterruptedException e) {}
		}
		
		else if(mainState.equals(MainState.DEFENDING))
		{
		}
		//ACT and update frame with action
		/*if(act.act == Action.Act.Move)
		{
			int i = 0;
			for(; i < 255; i++)
			{
				if(act.player1.path[i].equals(act.to))
					break;
			}
			act.player1.setState(i, 0);
		}
		// more complex comand. Move player behind puck whitout disturbing puck
		// then with a slight "invard" angle move to the puck
		else if(act.act == Action.Act.CollectPuck)
		{
			int i = 1;
			double xDiff = Math.abs((act.player1.path[0].getX()) - puck.getX());
			double yDiff = Math.abs((act.player1.path[0].getY()) - puck.getY());
			double oldDiff = yDiff+xDiff;
			double diff = 0;
			for(; i < 255; i++)
			{	
				xDiff = Math.abs((act.player1.path[i].getX()) - puck.getX());
				yDiff = Math.abs((act.player1.path[i].getY()) - puck.getY());
				diff = yDiff+xDiff;
				if(diff >= oldDiff)
				{
					i--;
					break;
				}
				oldDiff = diff;
			}
			act.player1.setState(i, 0);
		}
		else if(act.act == Action.Act.Shoot)
		{
			puck.setState((int)act.to.getX(), (int)act.to.getY()); // in goal
			act.from = null;
		}
		else if(act.act == Action.Act.Pass)
		{
			puck.setState((int)act.player2.getLocation().getX(), (int)act.player2.getLocation().getY());
		}
		else if(act.act == Action.Act.MoveWithPuck)
		{	
			int i = 0;
			for(; i < 256; i++)
			{
				if(act.player1.path[i].equals(act.to))
					break;
			}
			act.player1.setState(i, 0);
			puck.setState((int)act.player1.getLocation().getX(), (int)act.player1.getLocation().getY());
		}*/
		//frame.update(frame.getGraphics());
	}
	private int getPassOrShotPos(Action act)
	{
		int i = 1;
		Vector from = puck.subtract(act.to.subtract(act.from).multiply(/*TODO: puck radie*  */10/act.to.subtract(act.from).norm()));
		double xDiff = Math.abs((act.player1.path[0].getX()) - from.getX()); // -puck
		double yDiff = Math.abs((act.player1.path[0].getY()) - from.getY()); // -puck
		double oldDiff = yDiff+xDiff;
		double diff = 0;
		for(; i < 255; i++)
		{	
			xDiff = Math.abs(act.player1.path[i].getX() - from.getX()); // -puck
			yDiff = Math.abs(act.player1.path[i].getY() - from.getY()); // -puck
			diff = yDiff+xDiff;
			if(diff >= oldDiff)
			{
				i--;
				break;
			}
			oldDiff = diff;
		}
		System.out.println("First: " + i);
		
		// Variables to account for:
		// different teams
		// different players
		// puck.y over or under player path[i].y
		// moving puck in positive or negative x direction
		// (club is located slightly in front of player base)
		
		// - if the pass is close to 90 degrees in respect to the player path we may have to move the puck closer to the player path.
		// - if the distance from the puck to the path[i] is very small and we do not wish to pass close to 90 degrees
		//		we may have to move the puck slightly away from the path[i]
		
		// when oldDiff is smaller then 0 we need to inc i if home team and dec i if away team. 
		int traverse = 0;
		
		from = puck.subtract(act.to.subtract(act.from).multiply(/*TODO: puck radie*  */5/act.to.subtract(act.from).norm()));
		double far2 = act.to.subtract(from).normSquared();
		double close2 = from.subtract(act.player1.path[i]).normSquared();
		double hyp2 = act.to.subtract(act.player1.path[i]).normSquared();
		oldDiff = far2+close2-hyp2;
		if(team == Team.HOME)
			traverse = (oldDiff < 0)? 1 : -1;
		else
			traverse = (oldDiff < 0)? -1 : 1;
		oldDiff = Math.abs(oldDiff);
		for(i = i+traverse; i > 0 && i < 255; i+=traverse)
		{	
			far2 = act.to.subtract(from).normSquared();
			close2 = from.subtract(act.player1.path[i]).normSquared();
			hyp2 = act.to.subtract(act.player1.path[i]).normSquared();
			diff = Math.abs(far2+close2-hyp2);
			if(diff >= oldDiff)
			{
				i-=traverse;
				break;
			}
			oldDiff = diff;
		}
		System.out.println("Second: " + i);
			
		return gameTime;
	}
	public static void main(String[] arg) throws IOException {
		SocketAddress gameAddress = new InetSocketAddress("S2007", 60040);

		PlanningAI m = new PlanningAI(60090, gameAddress);

		PlanningAI d = new PlanningAI(60089, gameAddress);

	}
	
}
