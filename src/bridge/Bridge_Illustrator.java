package bridge;
import javax.swing.*;
import acm.graphics.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class Bridge_Illustrator extends JFrame implements Runnable{
	
	static Bridge_Illustrator BI = new Bridge_Illustrator();
	
	static Node[][] nodeField = new Node[16][12];
	
	static ArrayList<Node> nodeList = new ArrayList<Node>();
	
	static ArrayList<Beam> beamList = new ArrayList<Beam>();
	
	static GCanvas c = new GCanvas();
	
	static GLine fakeBeam = new GLine(50,50,100,100);
	
	static JButton start = new JButton("Start");
	
	public static void init(){
		
		for(Node[] n: nodeField)
			Arrays.fill(n, new Node());
		
		//	Initialize the window for Bridge simulation.
		BI.setVisible(true);
		BI.setSize(800, 700);
		BI.setDefaultCloseOperation(EXIT_ON_CLOSE);
		BI.add(c);
		start.setBounds(20,530,80,40);
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				runSimu();
			}
		});
		c.add(start);
		
		fakeBeam.setVisible(false);
		c.add(fakeBeam);
		c.requestFocus();
		c.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				
			}

			public void mousePressed(MouseEvent e) {
				fakeBeam.setStartPoint(50*Math.round(e.getX()/50.0), 50*Math.round(e.getY()/50.0));
				fakeBeam.setEndPoint(50*Math.round(e.getX()/50.0), 50*Math.round(e.getY()/50.0));
				fakeBeam.setVisible(true);
			}

			public void mouseReleased(MouseEvent e) {
				fakeBeam.setVisible(false);
				fakeBeam.setEndPoint(50*Math.round(e.getX()/50.0), 50*Math.round(e.getY()/50.0));
				if(Math.hypot(fakeBeam.getEndPoint().getX()-fakeBeam.getStartPoint().getX(), fakeBeam.getEndPoint().getY()-fakeBeam.getStartPoint().getY()) > 25){
					Node n1 = createNewNode(fakeBeam.getStartPoint().getX(),600-fakeBeam.getStartPoint().getY()),
							n2 = createNewNode(fakeBeam.getEndPoint().getX(),600-fakeBeam.getEndPoint().getY());
					beamList.add(new Beam(n1,n2));
					printBridge();
					printBridge();
				}
			}

			public void mouseEntered(MouseEvent e) {
				
			}

			public void mouseExited(MouseEvent e) {
				
			}
			
			
			
		});
		c.addMouseMotionListener(new MouseMotionListener(){

			public void mouseDragged(MouseEvent e) {
				fakeBeam.setEndPoint(e.getX(), e.getY());
			}

			public void mouseMoved(MouseEvent e) {
				
			}
			
		});
		
		//	Add dots to the canvas.
		for(int i=1;i<16;i++)
			for(int j=1;j<12;j++)
				c.add(new GOval(i*50,j*50,1,1));
		
		//	Generate the floor of bridge.
		createFloor();
	}
	
	//	Every bridge needs to have a floor which people and vehicles pass over. The method auto-generates one.
	public static void createFloor(){
		//	instantiate two fixed end nodes.
		Node n1 = new Node(100,300,true), n2 = new Node(700,300,true);
		
		//	Add the first end node
		nodeList.add(n1);
		for(int i=200;i<700;i+=100)
			nodeList.add(new Node(i,300,false));
		
		//	Add another end node
		nodeList.add(n2);
		
		fieldNode();
		
		//	Add beam based on nodes
		for(int i=0;i<nodeList.size()-1;i++)
			beamList.add(new Beam(nodeList.get(i),nodeList.get(i+1)));
	}
	
	//	This method adds all of the current nodes in the nodeList to the 2-Darray nodeField, based on their x and y axis.
	public static void fieldNode(){
		for(Node n:nodeList)
			nodeField[(int)Math.round(n.getX()/50.00)][(int)Math.round(n.getY()/50.00)] = n;
	}
	
	//	This method takes in an input of coordinates, and creates a node at the nearest multiples of 50.
	public static Node createNewNode(double x, double y){
		int a = (int)Math.round(x/50.0), b = (int)Math.round(y/50.0);
		if(!nodeField[a][b].isReal()){
			nodeField[a][b] = new Node(a*50,b*50,false);
			nodeList.add(nodeField[a][b]);
		}
		return nodeField[a][b];
	}
	
	//	This method starts threads of all Node objects, thus begin to run the simulation.
	public static void runSimu(){
		if(nodeList.size() != 0)
			for(Node n:nodeList)
				if(n.isReal())
					new Thread(n).start();
			
//		new Thread(BI).start();
	}
	
	//	Each time this method is called, all parts of the bridges are repainted with updated position.
	public static void printBridge(){
		if(beamList.size() != 0)
			for(Beam b:beamList)
				b.repaint(c);
				
		if(nodeList.size() != 0)
			for(Node n:nodeList)
				n.repaint(c);
		
		new Thread(BI).start();
	}
	
	/*	
	 * 	This thread is mainly used to repaint the bridge. Note that this method updates at a much slower frequency than the physics simulation threads
	 * 	because first of all, graphics do not need an extremely high rate of update to appear decent: 
	 * 	and secondly, high refreshing rate of graphics will put too much stress on the computer.
	 * 	On the other hand, physics simulation needs a high rate of refreshing in order to accurately approximate the structural pressure of bridge.
	 */
	public void run(){
		while(!Thread.currentThread().isInterrupted()){
			long t = System.currentTimeMillis()+100;
			while(System.currentTimeMillis()<=t){}
			printBridge();
		}
	}

	public static void main(String[] args) {
		init();
		printBridge();
	}

}
