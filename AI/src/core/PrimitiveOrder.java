package core;

public class PrimitiveOrder {
	int playerId;
	int translationSpeed;
	int transDestination;
	int rotationSpeed;
	int rotationDestination;
	
	
	
	
	public PrimitiveOrder(int id,int transSpeed,int transDest,int rotSpeed,int rotDest){
		playerId=id;
		translationSpeed=transSpeed;
		transDestination=transDest;
		rotationSpeed=rotSpeed;
		rotationDestination=rotDest;
		
	}
	
	int[] getIntArray(){
		int[] r={playerId,
		translationSpeed,
		transDestination,
		rotationSpeed,
		rotationDestination};
		return r;
	}

	public void setTranslation(int translationSpeed,int transDestination) {
		this.translationSpeed = translationSpeed;
		this.transDestination=transDestination;
	}

	public void setTransDestination(int transDestination) {
		this.transDestination = transDestination;
	}

	public void setRotationSpeed(int rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public void setRotationDestination(int rotationDestination) {
		this.rotationDestination = rotationDestination;
	}
	public String toString(){
		return playerId+" "+translationSpeed+" "+transDestination+" "+rotationSpeed+" "+rotationDestination;
	}
	
}
