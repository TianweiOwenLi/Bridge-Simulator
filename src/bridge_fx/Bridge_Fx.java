/**
 * Code developed by Owen Li. All rights reserved.
 * 
 * This sample code is intended to serve as a tutorial for JavaFx animation.
 * When executed, the program allows user to choose an image of type .jpg, 
 * .jpeg or .png from the computer storage. Then a window will pop up and 
 * display the pixelized image.
 */

package bridge_fx;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.event.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;


public class Bridge_Fx extends Application implements Runnable{
	
	static Stage stage;
	
	static Scene scene;
	
	static Group root;
	
	static Bridge_Fx BI = new Bridge_Fx();
	
	static Node[][] nodeField = new Node[16][12];
	
	static ArrayList<Node> nodeList = new ArrayList<Node>();
	
	static ArrayList<Beam> beamList = new ArrayList<Beam>();
		
	static Line fakeBeam = new Line(50,50,100,100);
	
	static Button start = new Button("Start");
	
	static Label beamThickness = new Label("Beam thickness: ");
	
	static TextField btInput = new TextField("16");
	
	static int thickness = 16;
	
	public void start(Stage primaryStage){
		
		stage = primaryStage;
		
		
		root = new Group();
		
		/*
		 * Scene setting.
		 */
		scene = new Scene(root, 800, 700);
		primaryStage.setTitle("Bridge Simulator");
		primaryStage.setScene(scene);
		primaryStage.setHeight(700);
		primaryStage.setWidth(800);
		primaryStage.setResizable(false);
		
		initt();
		
		primaryStage.show();
	}
	
	public static void initt(){
		
		for(Node[] n: nodeField)
			Arrays.fill(n, new Node());
		

		start.resizeRelocate(50, 600, 200, 150);
		start.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				runSimu();
			}
			
		});
		root.getChildren().add(start);
		
		beamThickness.resizeRelocate(300, 600, 400, 150);
		root.getChildren().add(beamThickness);
		
		btInput.resizeRelocate(600, 600, 150, 100);
		root.getChildren().add(btInput);
		
		fakeBeam.setStrokeWidth(4.5);
		fakeBeam.setVisible(false);
		root.getChildren().add(fakeBeam);
		stage.requestFocus();
		
		/*
		 * These methods control the dragged fake beam.
		 */
		
		scene.setOnMouseDragged(new EventHandler<MouseEvent>(){	
			public void handle(MouseEvent e) {
				if(start.isVisible()) {
					fakeBeam.setEndX(e.getSceneX());
					fakeBeam.setEndY(e.getSceneY());
				}
			}
		});
		
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent e) {
				if(start.isVisible()) {
					fakeBeam.setVisible(true);
					fakeBeam.setStrokeWidth(Math.sqrt(new Integer(btInput.getText())));
					thickness = new Integer(btInput.getText());
					fakeBeam.setStartX(50*Math.round(e.getX()/50.0));
					fakeBeam.setStartY(50*Math.round(e.getY()/50.0));
					fakeBeam.setEndX(e.getSceneX());
					fakeBeam.setEndY(e.getSceneY());
				}
			}
		});
		
		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
			
			public void handle(MouseEvent e) {
				if(start.isVisible()) {
					fakeBeam.setVisible(false);
					fakeBeam.setEndX(50*Math.round(e.getX()/50.0));
					fakeBeam.setEndY(50*Math.round(e.getY()/50.0));
					if(Math.hypot(fakeBeam.getEndX()-fakeBeam.getStartX(), fakeBeam.getEndY()-fakeBeam.getStartY()) > 25){
						Node n1 = createNewNode(fakeBeam.getStartX(),fakeBeam.getStartY()),
							 n2 = createNewNode(fakeBeam.getEndX(),fakeBeam.getEndY());
						beamList.add(new Beam(n1, n2, thickness));
						paintBeams();
						paintNodes();
					}
				}
			}
		});
		

		
		//	Add dots to the canvas.
		for(int i=1;i<16;i++)
			for(int j=1;j<12;j++)
				root.getChildren().add(new Circle(i*50,j*50,1));
		
		//	Generate the floor of bridge.
		createFloor();
		
		start.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				runSimu();
			}
		});
	}
	
	
	//	Every bridge needs to have fixed points.
	public static void createFloor(){
		//	instantiate two fixed end nodes.
		nodeList.add(new Node(700,150,true));
		nodeList.add(new Node(700,350,true));
		
		fieldNode();
		paintBeams();
		paintNodes();
	}
	
	//	This method adds all of the current nodes in the nodeList to the 2-D 
	// array nodeField, based on their x and y axis.
	public static void fieldNode(){
		for(Node n:nodeList)
			nodeField[(int)Math.round(n.getX()/50.00)][(int)Math.round(n.getY()/50.00)] = n;
	}
	
	
	/*
	 * This method takes in an input of coordinates, and creates a node at the nearest multiples of 50.
	 * If a node is already created, then nothing is done.
	 */
	public static Node createNewNode(double x, double y){
		int a = (int)Math.round(x/50.0), b = (int)Math.round(y/50.0);
		if(!nodeField[a][b].isReal()){
			nodeField[a][b] = new Node(a*50,b*50,false);
			nodeList.add(nodeField[a][b]);
		}
		return nodeField[a][b];
	}
	
	public static void paintNodes() {
		for(Node n:nodeList) {
			Circle icon = n.getIcon();
			icon.toFront();
			if(!root.getChildren().contains(icon))
				root.getChildren().add(icon);
		}
	}
	
	public static void paintBeams() {
		for(Beam b:beamList) {
			Line shape = b.getShape();
			shape.toBack();
			if(!root.getChildren().contains(shape)) {
				root.getChildren().add(shape);
			}
		}
	}
	
	//	This method starts threads of all Node objects, thus begin to run the simulation.
	public static void runSimu(){
		if(nodeList.size() != 0)
			for(Node n:nodeList)
				if(n.isReal()) 
					new Thread(n).start();
		
		new Thread(BI).start();
		start.setVisible(false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * 
	 * Updates the graphics for each refresh.
	 */
	public void run() {
		do {
			for(Node n:nodeList)
				n.repaint();
			for(Beam b:beamList)
				b.repaint();
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			for(Beam b:beamList)
				if(b.isBroken())
					Thread.currentThread().interrupt();
			
		}while(true);
	}
	
	
	/*
	 * Main method.
	 */
	public static void main(String[] args){
		launch(args);
	}
	
}