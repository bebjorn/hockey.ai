package core;

import java.awt.geom.Point2D;

public class Vector {
	double x;
	double y;
	public Vector(double x,double y){
		this.x=x;
		this.y=y;
	}
	public void addInPlace(Vector v){
		x+=v.getX();
		y+=v.getY();
	}
	public void subtractInPlace(Vector v){
		x-=v.getX();
		y-=v.getY();
	}
	public Vector add(Vector v){
		return new Vector(x+v.getX(),y+v.getY());
	}
	public Vector subtract(Vector v){
		return new Vector(x-v.getX(),y-v.getY());
	}
	public double normSquared(){
		return dot(this);
	}
	public double norm(){
		return (float) Math.sqrt(normSquared());
	}
	public double dot(Vector v){
		return x*v.getX()+y*v.getY();
	}
	public double getX() {
		return x;
	}
	public void setX(double d) {
		this.x = d;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public void multiplyInPlace(double m){
		x*=m;
		y*=m;
	}
	public Vector multiply(double m){
		return new Vector(x*m,y*m);
	}
	public String toString(){
		return "x: "+x+"\t y: "+y;
	}
	public double shortestVectorDistance(Vector v){
		//Vector v = new Vector(p.getX(),p.getY());
		//Vector w = this.multiply((this.dot(v)/this.norm()));
		return (v.subtract(this)).norm();		
	}
}