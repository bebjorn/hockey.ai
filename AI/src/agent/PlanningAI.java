package agent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import core.*;

/**
 * The class that plans and execute actions for the hockey game.
 * @author Björn Berntsson
 */
public class PlanningAI extends AIBase{
	
	public static final int CLUBREACH = 20; // TODO: set it
	public static final int PUCKRADIUS = 10; // TODO: set it
	private static final int epsPath = 5; // TODO: set it
	private static final int epsCor = 5;  // TODO: set it
	private static final int farFromLine = 5;  // TODO: set it
	private Team team;
	private TeamOfPlayers friendlyPlayers= new TeamOfPlayers();
	private TeamOfPlayers opposingPlayers= new TeamOfPlayers();
	private Puck puck;
	private ShotData  shotData = new ShotData();
	
	private Agent agent;
	Date d=new Date();
	/**
	 * A Planning AI that plans and execute actions for the hockey game.
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
	int phase = 0;
	int tmp = 0;
	Action act;
	
	@Override
	public void onNewState()  {
		
		//getPassOrShotPos(new Action(Act.Shoot, friendlyPlayers.get(4), puck, new Vector(150, 150)));
		
		// set current.effects
		agent.current.effects = getCurrentEffects();
		// make sure we have a goal
		if(mainState.equals(MainState.ATTACKING))
		{
			if(state.equals(State.PLANNING))
			{
				phase = 0;
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
				
				switch(phase)
				{
					case 0:
						int i = 0;
						for(; i < 255; i++)
						{
							if(act.player1.path[i].equals(act.to))
								break;
						}
						this.addOrder(new PrimitiveOrder(act.player1.getId(),200,i,0,0));
						phase++;
						break;
					case 1:
						Vector dist = act.player1.getLocation().subtract(act.to);
						if(dist.norm() < epsCor)
							state = State.PLANNING;
						break;
					default:
						System.out.println("Bad phase while moving!");
						break;
				}
			}
			else if(state.equals(State.MOVINGWITHPUCK))
			{
				// 1. åk "bakom" puck med klubban "ur vägen"
				// 2. Åk mot destination med liten "innåtvinkel"
				switch(phase)
				{
					case 0:
						break;
					case 1:
						// välj liten "inåttvinkel"
						break;
					case 2:
						int i = 0;
						for(; i < 255; i++)
						{
							if(act.player1.path[i].equals(act.to))
								break;
						}
						this.addOrder(new PrimitiveOrder(act.player1.getId(),180,i,0,0));
						phase++;
						break;
					case 3:
						Vector dist = act.player1.getLocation().subtract(act.to);
						if(dist.norm() < epsCor)
							state = State.PLANNING;
						break;
					default:
						System.out.println("Bad phase while moving!");
						break;
				}
			}
			else if(state.equals(State.COLLECTINGPUCK))
			{
				// 1. åk mot puck med liten "innåtvinkel"
				// TODO Om det finns tid, dra in pucken närmare vår egen linje så ingen motståndare når den.
				switch(phase)
				{
					case 0:
						// välj liten "inåttvinkel"
						break;
					case 1:
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
						this.addOrder(new PrimitiveOrder(act.player1.getId(),180,i,0,0));
						phase++;
						break;
					case 2:
						if(act.player1.getLocation().shortestVectorDistance(puck) < CLUBREACH)
							state = State.PLANNING;
						break;
					default:
						System.out.println("Bad phase while CollectingPuck!");
						break;
				}
			}
			else if(state.equals(State.PASSING))
			{
				// 1. Lägg puck tillrätta om det behövs 
				// 2. placera spelaren + vinkla motagare
				// 3. passa
				
				
				//puck.setState((int)act.player2.getLocation().getX(), (int)act.player2.getLocation().getY());
				
				state = State.PLANNING;
			}
			else if(state.equals(State.SHOOTING))
			{
				// 0. Lägg pucken tillrätta om det behövs??
				// 1. placera spelaren
				// 2. skjut
				switch(phase)
				{
					case 0:
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase+=2;
						else // need to move the puck.
						{
							if(act.player1.path[shotData.closestPos].subtract(puck).norm() < farFromLine)
							{
								// TODO: move puck away from line, closer to final destination
							}
							else
							{
								// TODO: simply move puck closer to the line
								// turn clockwise
								int dir = 1; //TODO What is clockWise + or -? different for the teams??
								if(shotData.clockwise)
									dir = -1;
								this.addOrder(new PrimitiveOrder(act.player1.getId(),100, shotData.closestPos, dir, 0));
							}
							phase++;
						}
						break;
					case 1:
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase++;
						else
						{
							// maby move back to phase 1 after a nr of turns
						}
						break;
					case 2:
						//shotData.calculateData(act, puck, team);
						//skall även rotera ifrån pucken till en bra vinkel att skuta från
						this.addOrder(new PrimitiveOrder(act.player1.getId(),50, shotData.pos, 80, 0));
						phase++;
						break;
					case 3:
						if(Math.abs(act.player1.getCurrentPos() - tmp) < epsPath)
							phase++;
						break;
					case 4:
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, 127, 0));
						phase++;
						break;
					case 5:
						act.from = null; // ugly way of detecting when we have a goal or not
						state = State.PLANNING;
						break;
					default:
						System.out.println("BAD!");
						break;
				}
			}
		}
		else if(mainState.equals(MainState.DEFENDING))
		{
			for(Iterator<Condition> iter = agent.current.effects.iterator(); iter.hasNext();)
			{
				Condition cond = iter.next();
				if(cond.name.equals(Condition.Name.CanGetPuck))
				{
					Player player = ((CondHasPuck)cond).player;
					
					act = new Action(Action.Act.CollectPuck, player);
					mainState = MainState.ATTACKING;
					state = State.COLLECTINGPUCK;
					return;
				}
			}
			boolean faceFront = false;
			if(puck.getY() <= 0) // 
				faceFront = true;
			
			friendlyPlayers.get(0);
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
		if(this.hasOrder())
		{
			try {
				this.send();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] arg) throws IOException{
		SocketAddress gameAddress = new InetSocketAddress("S2007", 60040);

		PlanningAI m = new PlanningAI(60090, gameAddress);

		PlanningAI d = new PlanningAI(60089, gameAddress);

	}
}
