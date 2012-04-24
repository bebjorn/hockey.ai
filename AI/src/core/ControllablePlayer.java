package core;

import java.awt.geom.Point2D;
import java.io.IOException;
/**
 * Class for managing players in the own team
 * @author Anders Ryndel
 *
 */
public class ControllablePlayer extends Player {
	PrimitiveOrder order;
	
	private int targetPos=0;
	private int transSpeed=0;
	private int targetRot=0;
	private int rotSpeed=0;
	
	public int getTargetPos() {
		return targetPos;
	}
	public void setTargetPos(int targetPos) {
		this.targetPos = targetPos;
	}
	public int getTransSpeed() {
		return transSpeed;
	}
	public void setTransSpeed(int transSpeed) {
		this.transSpeed = transSpeed;
	}
	public int getTargetRot() {
		return targetRot;
	}
	public void setTargetRot(int targetRot) {
		this.targetRot = targetRot;
	}
	public int getRotSpeed() {
		return rotSpeed;
	}
	public void setRotSpeed(int rotSpeed) {
		this.rotSpeed = rotSpeed;
	}
	public ControllablePlayer(int i) {
		super(i);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Causes the player to turn to face the puck
	 */
	/**
	 * Causes the player to turn and move into position to stop and take control over the puck
	 */
	/*public PrimitiveOrder scootAndShoot(){
		int closePos=(int)path.getInterceptPosition(new Vector(puck.getX(),puck.getY()));
		if(Math.abs(getTargetPos()-closePos)>10){
			
			targetPos=closePos;
			rotSpeed=0;
			transSpeed=254;
		}else{
			return null;
		}
		/*System.out.println(getCurrentPos()-closePos);
		if(Math.abs(getCurrentPos()-closePos)<40){
			
			transSpeed=0;
			rotSpeed=125;
			targetRot=getCurrentRot()-20;
			if(targetRot<0){
				targetRot+=360;
			}
		}*/
		/*return getOrder();
	}*/
	/**
	 * If the player is in control of the puck, this method will try to properly align the player to the puck and then turn to shoot it in the given direction.
	 * @param angle - the angle in which the player attempts to shoot at.
	 */
	public void shootAtAngle(double angle){
		
	}
	/**
	 * If the player is in control of the puck, this method will try to properly align the player to the puck and then turn to shoot it in the given direction.
	 * @param target - the target which the player attempts to shoot at.
	 */
	public void shootAtPoint(Point2D target){
		
	}
	/**
	 * If the player is in control of the puck, this method will try to properly align the player to the puck as to prepare a shot in the given direction.
	 * @param angle - the angle in which the player attempts to aim at.
	 */
	public void aimAtAngle(double angle){
		
	}
	/**
	 * If the player is in control of the puck, this method will try to properly align the player to the puck as to prepare a shot in the given direction.
	 * @param target - the target which the player attempts to aim at.
	 */
	public void aimAtPoint(Point2D target){
		
	}
	/**
	 * Makes the player turn to face the given angle.
	 * @param angle - the angle at which the player will face.
	 */
	public void lookAtAngle(double angle){
	
	}
	/**
	 * Makes the player turn to face the given point.
	 * @param target - the point which the player will face.
	 */
	public void lookAtPoint(Point2D target){
		
	}
	/**
	 * If the player is in control of the puck, it will try to move it to the given position.
	 * @param pos - the position that the player will attempt to reach with the puck.
	 */
	public void moveWithPuck(int pos){
		
	}
	/**
	 * @return pos - the how far the player is slided in it's path
	 */
	
	/**
	*@return the current order of this player
	*/
	
	protected PrimitiveOrder getOrder(){
		return new PrimitiveOrder(id,transSpeed,targetPos,rotSpeed,targetRot);
	}
	
}

