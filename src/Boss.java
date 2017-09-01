import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class Boss {
	static int canvasW = 0;
	static int canvasH = 0;
	static double grid_cell_w = 0;
	static double grid_cell_h = 0;
	// Choose a good 'n' according to our dimensions
	int n = 2; // temporary
	int total_robots = 4; // # of harvesting robots
	int cols = 0; // # of cols in our grid
	int rows = 0; // # of rows in our grid
	// Robots Array
	static ArrayList<Harvester> robots = null;
	boolean begin = true;
	static boolean nextStep = false;
	static boolean nextStep2 = false;
	static boolean initBush = true;
	static boolean complete = false;
	static int random = 0;
	static Mat wholeField = null;
	static Mat gradientMat = null;
	static int stepsComplete = 0;

	static BufferedWriter outputWriter = null;
	public void start(double w, double h)
	{
		//Figure out what our canvas size should be
		canvasSize(w,h);
		
		// Width and Height of each cell
		grid_cell_w = w/(total_robots*n);
		grid_cell_h = h/(total_robots*n);
		System.out.println("Size of each cell: "+grid_cell_w+"ft x "+grid_cell_h+"ft");
		
		// Compute the number of columns and rows for our grid
		cols = (int) (w/grid_cell_w);
		rows = (int) (h/grid_cell_h);
		//System.out.println("Number of rows: "+rows+", Number of columns:"+cols);
		
		// Create the frame where our drawing will go (UI purposes only)
		JFrame frame = new JFrame("Strawberry Field");
		setUpFrame(frame,1);
		JFrame frame2 = new JFrame("Robot View");
		setUpFrame(frame2,2);
		frame2.setVisible(true);
		

		//Assignment Problem: Set up harvesters
		robots = new ArrayList<Harvester>();
		setUpHarvesters(robots);
		
		// Setup our Robot's View
		RobotView robotView = new RobotView();
		frame2.add(robotView);
		frame2.setVisible(true);
		
		// Compute and Setup the grid
		Draw fieldView = new Draw();
		fieldView.computeCells(rows, cols);
		fieldView.setFocusable(true);
		updateField(frame,fieldView,robotView);
		

		
		
		
	}
	
	private void setUpHarvesters(ArrayList<Harvester> robots) {
		int w = canvasW/cols;
		int h = canvasH/rows;
		
		// Create n# of robots
		for(int i = 0; i<total_robots; i++)
		{
			robots.add(new Harvester());
		}
		
		//This here is somewhat hardcoded for only 4 robots
		//Col 1
		robots.get(0).setX(w/2);
		robots.get(0).setY(0);
		robots.get(0).setO(1);
		robots.get(0).init(w/2, 0);
		//Col 3
		robots.get(1).setX((w/2)+(2*w));
		robots.get(1).setY(0);
		robots.get(0).setO(1);
		robots.get(1).init((w/2)+(2*w), 0);
		//Col 5
		robots.get(2).setX((w/2)+(4*w));
		robots.get(2).setY(0);
		robots.get(0).setO(1);
		robots.get(2).init((w/2)+(4*w), 0);
		//Col 7
		robots.get(3).setX((w/2)+(6*w));
		robots.get(3).setY(0);
		robots.get(0).setO(1);
		robots.get(3).init((w/2)+(6*w), 0);
		/*
		// Set robot 1 to the center of top-left square
		robots.get(0).setX(w/2);
		robots.get(0).setY(h/2);
		robots.get(0).setO(1);
		// Set robot 2 to the center of top-right square
		robots.get(1).setX(canvasW - (w/2));
		robots.get(1).setY(h/2);
		robots.get(0).setO(1);
		// Set robot 3 to the center of bottom-left square
		robots.get(2).setX(w/2);
		robots.get(2).setY(canvasH - (h/2));
		robots.get(0).setO(2);
		// Set robot 4 to the center of bottom-right square
		robots.get(3).setX(canvasW - (w/2));
		robots.get(3).setY(canvasH - (h/2));
		robots.get(0).setO(2);
		*/
		// Just showing the user initial positions of each robot
		/*
		for(int i=0; i<robots.size(); i++)
		{
			System.out.println("Robot "+i+" has been assigned ("+robots.get(i).getX()+","+robots.get(i).getY()+")");
		}
		*/
	}

	private void updateField(JFrame frame, Draw field, RobotView robotView) {
		int w = canvasW/cols;
		int h = canvasH/rows;
		frame.add(field);
		frame.setVisible(true);
		field.addKeyListener(new KeyListener()
		{

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar()=='n')
				{
					if(begin)
					{
						System.out.println("Starting...");
						begin = false;
					}
					//System.out.println(stepsComplete);
					if(stepsComplete<(canvasH-5))
					{
						nextStep = true;
						nextStep2 = true;
						field.repaint();
						if(stepsComplete%(grid_cell_h/2)==0)
							robotView.repaint(); // Commented out for now -- doing path planning instead
					}
					else
					{
						// HARDCODED
						if(robots.get(0).getO()==0)
						{
							complete = true;
						}
						if(!complete)
						{
							robots.get(0).setO(0);
							robots.get(1).setO(0);
							robots.get(2).setO(0);
							robots.get(3).setO(0); // Time to move up
							robots.get(0).setX(w+w/2);
							robots.get(1).setX((w/2)+(3*w));
							robots.get(2).setX((w/2)+(5*w));
							robots.get(3).setX((w/2)+(7*w));
							robots.get(0).setY(canvasH);
							robots.get(1).setY(canvasH);
							robots.get(2).setY(canvasH);
							robots.get(3).setY(canvasH);
							stepsComplete=0;
						}
						else
						{
							System.out.println("Complete!!");
							// Save our gradient matrix!
							Highgui.imwrite("C:/Users/Ina/Programming/workspace/StrawberryRobots/images/gradient.png", gradientMat);
						}
		
					}
					
				}
				else
				{
					nextStep = false;
					nextStep2 = false;
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			
		});
		
	}

	public void setUpFrame(JFrame f, int frameNo)
	{
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if(frameNo==1)
		{
			f.setSize(canvasW+15, canvasH+38);
			f.setResizable(true);
		}
		else if(frameNo==2)
		{
			f.setSize(1040, 540);
			f.setResizable(false);
		}
	}
	
	
	/* Canvas Stuff */
	public void canvasSize(double w, double h)
	{
		int max = 0;
		if(w<h)
			max = (int) h;
		else
			max = (int) w;
		
		if(max<=200)
		{
			canvasW = (int) (w*5);
			canvasH = (int) (h*5);
		}
		else if(max<=1000)
		{
			canvasW = (int) w;
			canvasH = (int) h;
		}
		else if(max<=2000)
		{
			canvasW = (int) (w*0.5);
			canvasH = (int) (h*0.5);
		}
		else
		{
			canvasW = (int) (w*0.3);
			canvasH = (int) (h*0.3);
		}
	}
	public static int getCanvasWidth()
	{
		return canvasW;
	}
	public static int getCanvasHeight()
	{
		return canvasH;
	}
	public static void write (String filename, int[]x) throws IOException{
		  outputWriter = new BufferedWriter(new FileWriter(filename));
		  for (int i = 0; i < x.length; i++) {
		    // Maybe:
		    outputWriter.write(x[i]+"");
		    // Or:
		    outputWriter.write(Integer.toString(x[i]));
		    outputWriter.newLine();
		  }
		  outputWriter.flush();  
		  outputWriter.close();  
		}
}
