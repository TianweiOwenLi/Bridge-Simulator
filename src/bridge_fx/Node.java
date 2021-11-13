package bridge_fx;
import java.util.ArrayList;
import tools.Vector;
import javafx.scene.shape.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


public class Node implements Runnable{
	
	private double x,y, radius = 6.0;
	private ArrayList<Beam> beamList = new ArrayList<Beam>();
	private boolean isFixed, isReal = true;
	private Circle icon;
	
	public Node(double a, double b, boolean fix){
		x = a;
		y = b;
		isFixed = fix;
		icon = new Circle(x, y, radius);
		icon.setStroke(Color.BLACK);
		icon.setFill(isFixed?Color.BLACK:Color.WHITE);
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
	
	public void removeBeam(Beam b) {
		beamList.remove(b);
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public void repaint(){
		icon.relocate(x-radius, y-radius);
	}
	
	public Circle getIcon() {
		return icon;
	}
	
	
	/*
	 * destroys this node.
	 */
	public void breakNode() {
//		for(Beam b:beamList)
//			b.breakBeam();
		icon.setVisible(false);
	}
	
	
	/*
	 * returns the mass of this node.
	 */
	public double getMass() {
		double mass = 0;
		for(Beam b:beamList)
			mass += b.getMass();
		return mass / 2;
	}

	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		while(!Thread.currentThread().isInterrupted() && !isFixed && icon.isVisible()){
			
			/*
			 * this delay helps slowing down the program, for the ease of observation.
			 */
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			/*
			 * if no beam is attached, this node does not exist.
			 */
			if(beamList.size() == 0)
				breakNode();
			
			/*
			 * Add the forces together and calculate acceleration. 
			 * This is put into log scale, and has a constraint to prevent bugs.
			 */
			Vector sum = new Vector(0,9.8*getMass());
			for(Beam b:beamList) 
				sum.add(b.getForce(this));
			Vector acceleration = sum.multiply(1/getMass());
			acceleration = acceleration.multiply(0.1*Math.max(0, 
					Math.log10(acceleration.getMagnitude()/2))).getBoundedVector(0.02,0.02);
			
			// Update node location according to acceleration.
			x += acceleration.getX();
			y += acceleration.getY();			
		}
	}
}
