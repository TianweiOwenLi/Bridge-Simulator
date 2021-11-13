package tools;

public class Vector {
	
	private double dx, dy;
	
	public Vector(double x, double y){
		dx = x;
		dy = y;
	}
	
	public double getX(){
		return dx;
	}
	
	public double getY(){
		return dy;
	}
	
	public double getMagnitude(){
		return Math.hypot(dx, dy);
	}
	
	public Vector getUnitVector(){
		return new Vector(dx/getMagnitude(),dy/getMagnitude());
	}
	
	public Vector getSigmoidVector(){
		return new Vector(Math.signum(dx)*(-0.007+1/(1+Math.pow(Math.E, (-1)*(10*Math.abs(dx)-5)))),
				Math.signum(dy)*(-0.007+1/(1+Math.pow(Math.E, (-1)*(10*Math.abs(dy)-5)))));
	}
	
	public Vector getBoundedVector(double valx, double valy){
		return new Vector(Math.signum(dx)*Math.min(Math.abs(dx), Math.abs(valx)),Math.signum(dy)*Math.min(Math.abs(dy), Math.abs(valy)));
	}
	
	public void add(Vector v){
		dx += v.getX(); 
		dy += v.getY();
	}
	
	public void set(double x, double y){
		dx = x;
		dy = y;
	}
	
	public Vector multiply(double n){
		dx *= n;
		dy *= n;
		return this;
	}

}
