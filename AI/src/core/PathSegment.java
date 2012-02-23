//package core;
//
//import java.awt.Point;
//import java.awt.geom.Line2D;
//import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;
//import java.util.List;
//
//import org.jscience.mathematics.structure.Field;
//import org.jscience.mathematics.vector.DenseVector;
//import org.jscience.mathematics.vector.Vector;
//
//public class PathSegment {
//	public PathSegment(double a,double b,double c,double d){
//		super(a,b,c,d);
//	}
//	public PathSegment(Point2D point2d, Point2D point2d2) {
//		super(point2d, point2d2);
//		// TODO Auto-generated constructor stub
//	}
//	double getLength(){
//		return getP1().distance(getP2());
//	}
//	Point2D getFractionPoint(double frac){
//		Point2D p=new Point2D.Double((frac/getLength())*(getX2()-getX1()), frac*(getY2()-getY1()));
//		return new Point2D.Double(getX1()+p.getX(),getY1()+p.getY());
//	}
//	PathSegment displace(Point2D p){
//		return new PathSegment(getX1()+p.getX(),getY1()+p.getY(),getX2()+p.getX(),getY2()+p.getY());
//	}
//	@Override
//	public Object plus(Object arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public List asList() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public DenseVector copy() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public DenseVector getSubVector(List arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public DenseVector opposite() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public DenseVector plus(Vector arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public DenseVector times(Field arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public Field get(int arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public int getDimension() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	@Override
//	public Field times(Vector arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//
//
//}
