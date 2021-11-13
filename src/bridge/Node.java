package bridge;
import java.awt.Color;
import java.util.ArrayList;
import tools.Vector;
import acm.graphics.*;
public class Node implements Runnable{
	private double x,y, Vx, Vy;
	private ArrayList<Beam> beamList = new ArrayList<Beam>();
	private boolean isFixed;
	private GOval icon;
	private boolean isAdded = false, isReal;
	public Node(double a, double b, boolean fix){
		x = a;
		y = b;
		Vx = 0;
		Vy = 0;
		isFixed = fix;
		isReal = true;
		icon = new GOval(x-3,y-3,6,6);
		icon.setFilled(true);
		icon.sendToFront();
		if(isFixed)
			icon.setFillColor(Color.BLACK);
		else
			icon.setFillColor(Color.WHITE);
	}
	
	public Node(){
		isReal = false;
	}
	
	public boolean isReal(){
		return isReal;
	}
	
	public void addBeam(Beam b){
		beamList.add(b);
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public void repaint(GCanvas c){
		icon.setLocation(x-3, 600-y-3);
		if(!isAdded){
			isAdded = true;
			c.add(icon);
		}
	}

	public void run() {
		double mass = 0;
		for(Beam b:beamList)
			mass += b.getMass();
		mass /= 2;
		
		Vector sum = new Vector(0,-9.8*mass);
		
		while(!Thread.currentThread().isInterrupted() && !isFixed){
			long t = System.currentTimeMillis()+0;
			while(System.currentTimeMillis()<=t){}
			
			sum.set(0, -20*mass);
			for(Beam b:beamList)
				sum.add(b.getForce(this));
			sum = sum.multiply(1/mass);
			sum.multiply(0.001*Math.max(0, Math.log10(sum.getMagnitude())));
			if(sum.getMagnitude()>0.01)
				sum = sum.getUnitVector().getBoundedVector(0.005,0.03);
			x += sum.getX();
			y += sum.getY();
			
		}
	}
}
