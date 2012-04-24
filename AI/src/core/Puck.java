package core;

public class Puck extends Vector{
	public Puck() {
		super(0, 0);
		// TODO Auto-generated constructor stub
	}
	
	void setState(int x,int y){
		this.setX(x);
		setY(y);
	}
	
	public String toString(){
		return "X:"+this.getX()+ " Y:" + this.getY();
	}
}
