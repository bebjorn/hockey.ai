package core;


import java.awt.geom.Point2D;
import java.util.ArrayList;

import util.Vector;

public class PlayerPath {
	double[] length;
	double totalLength;
	Vector[] points;
	Vector[] relative;
	
	public static final double CLUB_LENGTH = 42;

	PlayerPath(Vector[] v) {
		points = v;
		relative = new Vector[v.length];
		length = new double[points.length];
		length[0] = 0;
		for (int i = 1; i < v.length; i++) {
			relative[i - 1] = (points[i].subtract(points[i-1]));
			length[i] = length[i - 1] + relative[i - 1].norm();
			//System.out.println(length[i]);
		}
		totalLength = length[length.length - 1];
	}

	public Vector getCoordinate(int pos) {
		if (pos > 255 || pos < 0) {
			return null;
		}
		float p = pos / 256f;
		int q = 0;
		while (length[q] <= p * totalLength) {
			
			q+=1;
			if (q == length.length) {
				break;
			}
		}
		q -= 1;
		//System.out.println(p * totalLength+"\t"+length.length+"\t"+q);
		double f = (p * totalLength - length[q]) / (length[q + 1] - length[q]);
		
				
		return points[q].add(relative[q].multiply(f));
	}
	double getInterceptPosition(Vector interceptor){
		return getInterceptPosition(interceptor, new Vector(0, 0));
	}
	Vector getInterceptPoint(Vector interceptor) {
		return getInterceptPoint(interceptor, new Vector(0, 0));
	}
	double getInterceptPosition(Vector interceptor, Vector displacement){
		
		double min = Double.MAX_VALUE;
		Vector[] dispPoints;
		if (displacement.getX() != 0 || displacement.getY() != 0) {
			dispPoints=new Vector[points.length];
			for (int i = 0; i < points.length; i++) {
				dispPoints[i] = points[i].add(displacement);
			}
		}else{
			dispPoints=points;
		}
		double minPos=0;
		for (int i = 0; i < dispPoints.length; i++) {
			double temp=(dispPoints[i].subtract(interceptor)).norm();
			
			if(temp<min){
				min=temp;
				minPos=length[i];
			}
		}
		
		
		
		for(int i = 0; i < dispPoints.length-1; i++){
			Vector relativeInterceptor=interceptor.subtract(dispPoints[i]);
			//System.out.println("relInt "+relativeInterceptor);
			//System.out.println("rel "+relative[i]);
			
			//System.out.println("dot "+a);
			double f=relativeInterceptor.dot(relative[i])/relative[i].normSquared();
			//System.out.println(f);
			if(f>0&&f<1){
				Vector p=dispPoints[i].add(
							relative[i].multiply(f)
						);
				double temp=(interceptor.subtract(
							p
						)).norm();
				//System.out.println(f);
				if(temp<min){
					min=temp;
					minPos=255*(length[i]+f*(length[i+1]-length[i]))/totalLength;
					
					//System.out.println(f+" "+length[i]);
				}
				
			}
		}
		//System.out.println(minPos);
		return minPos;
	}
	Vector getInterceptPoint(Vector interceptor, Vector displacement) {
		double min = Double.MAX_VALUE;
		Vector[] dispPoints;
		if (displacement.getX() != 0 || displacement.getY() != 0) {
			dispPoints=new Vector[points.length];
			for (int i = 0; i < points.length; i++) {
				dispPoints[i] = points[i].add(displacement);
			}
		}else{
			dispPoints=points;
		}
		Vector close = dispPoints[0];
		for (int i = 0; i < dispPoints.length; i++) {
			double temp=(dispPoints[i].subtract(interceptor)).norm();
			if(temp<min){
				min=temp;
				close=dispPoints[i];
			}
		}
		for(int i = 0; i < dispPoints.length; i++){
			double f=(interceptor.subtract(dispPoints[i])).dot(relative[i])/(interceptor.norm()*relative[i].norm());
			if(f>0&&f<1){
				Vector p=dispPoints[i].add(
							relative[i].multiply(f)
						);
				double temp=(interceptor.subtract(
							p
						)).norm();
				if(temp<min){
					min=temp;
					close=p;
				}
				
			}
		}
		return close;
	}
	
	public boolean canPlayerReachPuck(Puck p) {
		double dist=50;
		Vector w;
		Vector v = new Vector(p.getX(),p.getY());
		
		for(int i=0; i<relative.length; i++) {
			
			w = v.subtract(points[i]);
			dist=relative[i].shortestVectorDistance(w);
			if (dist>CLUB_LENGTH){
				return true;
			}
		}
		return false;
	}

	//double getInterceptLine(Line2D interceptor) {
	//	return getInterceptLine(interceptor, new Point2D.Double(0, 0));
	//}

	//double getInterceptLine(Line2D interceptor, Point2D displacement) {
	//	;
	//}
}
