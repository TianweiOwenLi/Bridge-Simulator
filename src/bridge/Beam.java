package bridge;

import tools.Vector;

import java.awt.Color;

import acm.graphics.*;
public class Beam {
	
	private Node init, end;
	private final double LENGTH, DIAM = 2, DENS = 80, Y_MOD = 250000000, extensionFactor = 0.01;
	private double initTension = 0;
	private GPolygon p = null;
	private boolean broken = false, isAdded ;
	
	public Beam(Node a, Node b){
		init = a;
		end = b;
		LENGTH = Math.hypot(end.getX()-init.getX(), end.getY()-init.getY());
		a.addBeam(this);
		b.addBeam(this);
		
		p = drawBeam();
	}
	
	/*
	 * help draw the beam on the canvas. Returns a GPolygon object that represents the beam.
	 */
	public GPolygon drawBeam(){
		p = p==null?new GPolygon():p;
		double x1 = init.getX(), x2 = end.getX(), y1 = 600 - init.getY(), y2 = 600 - end.getY();
		double dx = 2*DIAM*(y2-y1)/getCurrentLength(), dy = 2*DIAM*(x2-x1)/getCurrentLength();
		p.addVertex(x1+dx,y1-dy);
		p.addVertex(x1-dx,y1+dy);
		p.addVertex(x2-dx,y2+dy);
		p.addVertex(x2+dx,y2-dy);
		p.sendToBack();
		return p;
	}
	
	
	/*
	 * returns the mass of the beam.
	 */
	public double getMass(){
		return LENGTH*DIAM*DIAM*DENS;
	}
	
	
	/*
	 * returns the length of the beam.
	 */
	public double getCurrentLength(){
		return Math.hypot(end.getX()-init.getX(), end.getY()-init.getY());
	}
	
	
	/*
	 * 
	 */
	public Vector getForce(Node n){
		double deltaL = Math.hypot(end.getX()-init.getX(), end.getY()-init.getY())-LENGTH;
		double deformRate = (deltaL)/LENGTH, magnitude = deformRate*DIAM*DIAM*Y_MOD;
		
		if(Math.abs(deformRate) > extensionFactor){
			broken = true;
		}
		
		if(broken)
			return new Vector(0,0);
		else{
			double midX = (init.getX()+end.getX())/2, midY = (init.getY()+end.getY())/2;
			Vector force = new Vector(midX-n.getX(),midY-n.getY()).getUnitVector();
			force.multiply(magnitude);
			return force;
		}
	}
	
	
	/*
	 * Repaint the bridge on canvas.
	 */
	public void repaint(GCanvas c){
//		c.remove(p);
		double deformRate = (Math.hypot(end.getX()-init.getX(), end.getY()-init.getY())-LENGTH)/LENGTH;
		if(!broken){
//			p = drawBeam();
			if(!isAdded)
				c.add(p);
			p.setFilled(true);
			p.sendToBack();
			p.setFillColor(new Color((float)Math.min(1,1-deformRate*0.9/extensionFactor),
					(float)(1-Math.abs(deformRate)*0.9/extensionFactor),
					(float)Math.min(1, 1+deformRate*0.9/extensionFactor)));
		}
	}
}
