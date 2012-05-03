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
	private static final int epsRot = 5;  // TODO: set it
	private static final int farFromLine = 5;  // TODO: set it
	private Team team;
	private TeamOfPlayers friendlyPlayers= new TeamOfPlayers();
	private TeamOfPlayers opposingPlayers= new TeamOfPlayers();
	private Puck puck;
	private ShotData  shotData = new ShotData();
	private int moveDest = 0; // saves the destination of move orders
	private int angleDest = 0; // saves the angleDestination of rotaion orders
	private int phaseStuckCount = 0;
	
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
				switch(phase)
				{
					case 0:
						//moving left, puck on the right
						
						if (act.to.getX() < act.from.getX() && act.from.getX() < puck.getX())
						{
							angleDest = team.equals(Team.HOME) ? 128 : 0;
							this.addOrder(new PrimitiveOrder(act.player1.getId(),180,angleDest,0,0));
							phase++;
						}
						else if(act.to.getX() > act.from.getX() && act.from.getX() > puck.getX())
						{
							angleDest = team.equals(Team.HOME) ? 0 : 128;
							this.addOrder(new PrimitiveOrder(act.player1.getId(),180,angleDest,0,0));
							phase++;
						}
						else // we do not need to go behind puck
						{
							phase+=4;
						}
						break;
					case 1:
						if(Math.abs(act.player1.getCurrentRot()-angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;
					case 2:
						boolean left = false; // moving "right" : towards away team
						if (act.to.getX() < act.from.getX()) // moving "left"
						{
							left = true;
						}
						int i = 0;
						for(; i < 255; i++)
						{
							if(act.player1.path[i].equals(act.from))
								break;
						}
						if(team.equals(Team.HOME))
						{
							i = left ? i-10: i+10;
						}
						else
							i = left ? i+10: i-10;
						i = i<0 ? 0:i;
						i = i>255 ? 255:i;
						moveDest = i;
						this.addOrder(new PrimitiveOrder(act.player1.getId(),180,i,0,0));// move behind
						phase++;
						break;
					case 3:
						if(Math.abs(act.player1.getCurrentPos()-moveDest) < epsPath)
							phase++;
					case 4: //TODO: may want to and or remove a few degrees based on left or right traveling to get an inward angle
						angleDest = 64;
						if(act.player1.getLocation().getY() < puck.getY())// puck below player
							if(team.equals(Team.HOME))
								angleDest = 192;
						if(act.player1.getLocation().getY() >= puck.getY())// puck above player
							if(team.equals(Team.AWAY))
								angleDest = 192;
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0,0,60,angleDest));// rotate into pos
						phase++;
						break;
					case 5:
						if(Math.abs(act.player1.getCurrentRot()-angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;
					case 6:
						int j = 0;
						for(; j < 255; j++)
						{
							if(act.player1.path[j].equals(act.to))
								break;
						}
						this.addOrder(new PrimitiveOrder(act.player1.getId(),180,j,0,0));
						phase++;
						break;
					case 7:
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
				// TODO: Om det finns tid, dra in pucken närmare vår egen linje så ingen motståndare når den.
				switch(phase)
				{
					case 0: //TODO: may want to and or remove a few degrees based on left or right traveling
						angleDest = 64;
						if(act.player1.getLocation().getY() < puck.getY())// puck below player
							if(team.equals(Team.HOME))
								angleDest = 192;
						if(act.player1.getLocation().getY() >= puck.getY())// puck above player
							if(team.equals(Team.AWAY))
								angleDest = 192;
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0,0,60,angleDest));// rotate into pos
						phase++;
						break;
					case 1:
						if(Math.abs(act.player1.getCurrentRot()-angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;
					case 2:
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
					case 3:
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
				switch(phase)
				{
					case 0: // check if the puck has a good position
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase+=2;
						else // need to move the puck.
						{
							// TODO: check this part, maybe also use player.y < 0 to set dir ??
							// move puck away from line, closer to final destination
							if(act.player1.path[shotData.closestPos].subtract(puck).norm() < farFromLine)
							{
								
								int dir = 0;
								if(act.player1.getLocation().getX() > puck.getX()) // player is to the right of puck;
								{
									dir = -1;
									angleDest = team.equals(Team.HOME) ? 123 : 250;
								}
								else
								{
									dir = 1;
									angleDest = team.equals(Team.HOME) ? 5 : 133;
								}
								// TODO: may need to increase speed for short rotations
								this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, dir*20, angleDest));
							}
							else
							{
								// TODO: move puck closer to the line
								// turn clockwise
								int dir = 1;
								if(act.player1.getLocation().getX() > puck.getX()) // player is to the right of puck;
								{
									if(act.player1.getLocation().getY() > puck.getY())//puck above player
									{
										dir = 1;
										angleDest = team.equals(Team.HOME) ? 96 : 224;
									}
									else
									{
										dir = -1;
										angleDest = team.equals(Team.HOME) ? 160 : 32;
									}
								}
								else
								{
									if(act.player1.getLocation().getY() > puck.getY())
									{
										dir = -1;
										angleDest = team.equals(Team.HOME) ? 32 : 160;
									}
									else
									{
										dir = 1;
										angleDest = team.equals(Team.HOME) ? 224 : 96;
									}
								}
								
								this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, dir*70, angleDest));
							}
							phase++;
						}
						break;
					/*WE MAY NEED THIS case 1: 
						if(Math.abs(act.player1.getCurrentRot()-angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;*/
					case 1:
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase++;
						else
						{
							phaseStuckCount++;
							if (phaseStuckCount > 50)
							{
								phaseStuckCount = 0;
								state = State.PLANNING; // TODO: plan or go to phase1 ???
							}
						}
						break;
					case 2: //skall även rotera ifrån pucken till en bra vinkel att skjuta ifrån
						//shotData.calculateData(act, puck, team);
						int dir;
						if(act.player1.getCurrentRot() > shotData.fromAngle)
						{
							if(act.player1.getCurrentRot()-shotData.fromAngle > 128)
								dir = 1; //anti-clock
							else
								dir = -1; // clock
						}
						else
						{
							if(shotData.fromAngle - act.player1.getCurrentRot() > 128)
								dir = -1; //anti-clock
							else
								dir = 1; // clock
						}
						int i = 0;
						for(; i < act.player2.path.length; i++)
						{
							if(act.player1.path[i].equals(act.to))
								break;
						}
						i = i < 5 ? 0: i-5;
						
						this.addOrder(new PrimitiveOrder(act.player2.getId(), 200, i, 120, 0));
						this.addOrder(new PrimitiveOrder(act.player1.getId(),50, shotData.pos, dir*127, shotData.fromAngle));
						phase++;
						break;
					case 3:
						if(Math.abs(act.player1.getCurrentPos() - tmp) < epsPath)
							phase++;
						break;
					case 4:
						int rotDest = shotData.fromAngle + shotData.clockwise*-32; // almost one complete turn
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, shotData.clockwise*127, rotDest));
						phase++;
						break;
					case 5: // wait a few turns before trying to plan more
						phaseStuckCount++;
						if(phaseStuckCount > 10) // TODO: Tune this constant or use some time constant instead
						{
							act.from = null; // ugly way of detecting when we have a goal(for the plan) or not
							state = State.PLANNING;
							phaseStuckCount = 0;
						}
						break;
					default:
						System.out.println("BAD!");
						break;
				}
			}
			else if(state.equals(State.SHOOTING))
			{
				boolean stop = true;
				for(Iterator<Condition> iter = agent.current.effects.iterator(); iter.hasNext();)
				{
					Condition cond = iter.next();
					if(cond.name.equals(Condition.Name.CanGetPuck) && ((CondCanGetPuck)cond).player.equals(act.player1))
					{						
						stop = false;
						break;
					}
				}
				if (stop)
				{
					mainState = MainState.DEFENDING;
				}
				switch(phase)
				{
					case 0: // check if the puck has a good position
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase+=2;
						else // need to move the puck.
						{
							// TODO: check this part, maybe also use player.y < 0 to set dir ??
							// move puck away from line, closer to final destination
							if(act.player1.path[shotData.closestPos].subtract(puck).norm() < farFromLine)
							{
								
								int dir = 0;
								if(act.player1.getLocation().getX() > puck.getX()) // player is to the right of puck;
								{
									dir = -1;
									angleDest = team.equals(Team.HOME) ? 123 : 250;
								}
								else
								{
									dir = 1;
									angleDest = team.equals(Team.HOME) ? 5 : 133;
								}
								// TODO: may need to increase speed for short rotations
								this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, dir*20, angleDest));
							}
							else
							{
								// TODO: move puck closer to the line
								// turn clockwise
								int dir = 1;
								if(act.player1.getLocation().getX() > puck.getX()) // player is to the right of puck;
								{
									if(act.player1.getLocation().getY() > puck.getY())//puck above player
									{
										dir = 1;
										angleDest = team.equals(Team.HOME) ? 96 : 224;
									}
									else
									{
										dir = -1;
										angleDest = team.equals(Team.HOME) ? 160 : 32;
									}
								}
								else
								{
									if(act.player1.getLocation().getY() > puck.getY())
									{
										dir = -1;
										angleDest = team.equals(Team.HOME) ? 32 : 160;
									}
									else
									{
										dir = 1;
										angleDest = team.equals(Team.HOME) ? 224 : 96;
									}
								}
								
								this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, dir*70, angleDest));
							}
							phase++;
						}
						break;
					/*WE MAY NEED THIS case 1: 
						if(Math.abs(act.player1.getCurrentRot()-angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;*/
					case 1:
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase++;
						else
						{
							phaseStuckCount++;
							if (phaseStuckCount > 50)
							{
								phaseStuckCount = 0;
								state = State.PLANNING; // TODO: plan or go to phase1 ???
							}
						}
						break;
					case 2: //skall även rotera ifrån pucken till en bra vinkel att skjuta ifrån
						//shotData.calculateData(act, puck, team);
						int dir;
						if(act.player1.getCurrentRot() > shotData.fromAngle)
						{
							if(act.player1.getCurrentRot()-shotData.fromAngle > 128)
								dir = 1; //anti-clock
							else
								dir = -1; // clock
						}
						else
						{
							if(shotData.fromAngle - act.player1.getCurrentRot() > 128)
								dir = -1; //anti-clock
							else
								dir = 1; // clock
						}
						this.addOrder(new PrimitiveOrder(act.player1.getId(),50, shotData.pos, dir*127, shotData.fromAngle));
						phase++;
						break;
					case 3:
						if(Math.abs(act.player1.getCurrentPos() - tmp) < epsPath)
							phase++;
						break;
					case 4:
						int rotDest = shotData.fromAngle + shotData.clockwise*-32; // almost one complete turn
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, shotData.clockwise*127, rotDest));
						phase++;
						break;
					case 5: // wait a few turns before trying to plan more
						phaseStuckCount++;
						if(phaseStuckCount > 10) // TODO: Tune this constant or use some time constant instead
						{
							act.from = null; // ugly way of detecting when we have a goal(for the plan) or not
							state = State.PLANNING;
							phaseStuckCount = 0;
						}
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
