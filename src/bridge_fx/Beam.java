package bridge_fx;

import tools.Vector;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
public class Beam{
	
	private Node init, end;
	private final double LEN, DIAM, DENS = 7850, Y_MOD = 5000000000.0, extensionFactor = 0.05;
	private double initTension = 0;
	private Line p = null;
	private boolean broken = false;
	
	public Beam(Node a, Node b, int crossSection){
		init = a;
		end = b;
		DIAM = Math.sqrt(crossSection);
		LEN = Math.hypot(end.getX()-init.getX(), end.getY()-init.getY());
		a.addBeam(this);
		b.addBeam(this);
		
		p = p==null?new Line(init.getX(), init.getY(), end.getX(), end.getY()):p;
		p.setStrokeWidth(DIAM);		
		p.setStroke(getColorUnderStress());
		
		/*
		 * allows user to break beams.
		 */
//		p.setOnMouseClicked(new EventHandler<MouseEvent>() {
//			public void handle(MouseEvent event) {
//				breakBeam();
//			}
//		});
	}
	
	
	/*
	 * returns the mass of the beam.
	 */
	public double getMass(){
		return LEN*DIAM*DIAM*DENS;
	}
	
	
	/*
	 * Returns a boolean value indicating of the beam is broken or not.
	 */
	public boolean isBroken() {
		return broken;
	}
	
	
	/*
	 * Returns the initial length of the beam.
	 */
	public double getInitialLength() {
		return LEN;
	}
	
	
	/*
	 * returns the current length of the beam.
	 */
	public double getCurrentLength(){
		return Math.hypot(end.getX()-init.getX(), end.getY()-init.getY());
	}
	
	
	/*
	 * Checks the extension of the beam, in absolute displacement. 
	 * Negative values signify compression.
	 */
	public double getExtension() {
		return Math.hypot(end.getX()-init.getX(), end.getY()-init.getY())-LEN;
	}
	
	
	/*
	 * Returns the deformation percentile, which means 
	 * the percent of deformation compared to original length.
	 * Negative values signify compression.
	 */
	public double getDeformPercent() {
		return (getExtension())/LEN;
	}
	
	
	/*
	 * Returns the load percentile before beam failure.
	 * Negative values signify compression.
	 */
	public double getLoadPercent() {
		return getDeformPercent()/extensionFactor;
	}
	
	
	/*
	 * Indicates of the beam is over-loaded.
	 */
	public boolean isOverloaded() {
		return Math.abs(getLoadPercent()) >= 1;
	}
	
	
	/*
	 * Breaks the beam.
	 */
	public void breakBeam() {
		broken = true;
//		p.setVisible(false);
//		init.removeBeam(this);
//		end.removeBeam(this);
	}
	
	
	/*
	 * Returns the desired integer for scaling.
	 * This is for handling colors.
	 */
	private int scaleNum(int min, int max, double var) {
		return Math.max(min, Math.min(max, (int)((min + max)/2.0 + var*0.5*(max - min))));
	}
	
	
	/*
	 * Returns the desired color under stress.
	 * For handling different materials.
	 */
	public Color getColorUnderStress() {
		return Color.rgb(scaleNum(0, 255, -getLoadPercent()), 
				scaleNum(0, 128, 1-Math.abs(getLoadPercent())), 
				scaleNum(0, 255, getLoadPercent()));
	}
	
	
	/*
	 * returns the Line object of this beam.
	 */
	public Line getShape() {
		return p;
	}
	
	
	/*
	 * get the force vector exerted to a given connected node by this beam.
	 */
	public Vector getForce(Node n){
		if(!(n.equals(init) || n.equals(end)))
			return new Vector(0, 0);
		
		double deformRate = (getExtension())/LEN, 
				magnitude = deformRate*DIAM*DIAM*Y_MOD;
		
//		if(isBroken() || isOverloaded()) {
//			breakBeam();
//			return new Vector(0,0);
//		}else{
		double centerOfMassX = (init.getX()+end.getX())/2, 
			   centerOfMassY = (init.getY()+end.getY())/2;
		return new Vector(centerOfMassX-n.getX(),centerOfMassY-n.getY())
				.getUnitVector().multiply(magnitude);
//		}
	}
	
	
	/*
	 * repaints the beam and updates the color. Blue for tension and red for compression.
	 */
	public void repaint() {
		p.setStartX(init.getX());
		p.setStartY(init.getY());
		p.setEndX(end.getX());
		p.setEndY(end.getY());
		p.setStroke(getColorUnderStress());
	}
	
}
