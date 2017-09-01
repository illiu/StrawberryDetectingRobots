
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;

public class Draw extends JPanel{
	static int cellW = (int)Boss.grid_cell_w;
	static int cellH = (int)Boss.grid_cell_h;
	int rows = 0;
	int cols = 0;
	ArrayList<Harvester> robots = Boss.robots;
	BufferedImage img = null;
	ArrayList<ArrayList<Integer>> bushes = null;  // Need this to store random coords of our strawberry bushes (init once)
	BufferedImage fieldImg = new BufferedImage(Boss.canvasW,Boss.canvasH,BufferedImage.TYPE_INT_RGB);
	public void computeCells(int r, int c) // pretty useless code
	{
		rows = r;
		cols = c;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		this.setBackground(new Color(233,204,135));
		g.drawImage(fieldImg, 0, 0, null);
		Graphics newG = fieldImg.createGraphics();
		
		newG.setColor(new Color(233,204,135));
		newG.fillRect(0, 0, Boss.canvasW, Boss.canvasH);
		//newG.setBackground(new Color(233,204,135));
		
		try {
			img = ImageIO.read(new File("images/roboimgs.png"));
		} catch (IOException e) {
			System.out.println("unable to open image");
			e.printStackTrace();
		}

		drawField(newG);
		drawGrid(newG);
		// Create screenshot of the field
		
		
		if(Boss.nextStep==true)
		{
			drawRobots(g);
			
			for(int i=0; i<robots.size(); i++)
			{
				// compute gradient every 10 steps!
				if(Boss.stepsComplete%9==0)
				{
					robots.get(i).computeLocalGradient();
				}
				// figure out the best next step for each robot
				robots.get(i).followGradient();
			}
			Boss.stepsComplete++;
		}
		newG.dispose();
		
	}
	private void drawRobots(Graphics g) // Add the robots to the view
	{
		int middle = img.getWidth()/2;
		for(int i=0; i<robots.size(); i++)
		{
			// TO-DO: compensate for orientation. 
			// We do -middle to use coords of the center of the robot image
			//System.out.println(robots.get(i).getX());
			g.drawImage(img, robots.get(i).getX()-middle, robots.get(i).getY()-middle, null); 
		}
		Boss.nextStep = false; // next step complete
		
	}

	public void drawGrid(Graphics g) // Draw the grid in the view
	{
		g.setColor(Color.GRAY);
		for(int i=1; i<=rows; i++)
		{
			g.drawLine(0,cellH*i,Boss.getCanvasWidth(),cellH*i);
		}
		for(int i=1; i<=cols; i++)
		{
			g.drawLine(cellW*i, 0, cellW*i,Boss.getCanvasHeight());
		}
		
	}
	
	public void drawField(Graphics g) // Draw the strawberry field into the grid
	{
		g.setColor(new Color(0,120,60));

		if(Boss.initBush)
			bushes = new ArrayList<ArrayList<Integer>>();
		int subcol=0;
		
		// CODE FOR DRAWING THE FIELD
		if(Boss.grid_cell_w <= 100) // If cell width is small, put one row of strawberries
		{
			//  divide the cell into 4's
			subcol = (int) (Boss.grid_cell_w/4);
			// x||xx||xx||x draw rows of strawberries at 1 & 4
			if(Boss.initBush)
			{
				for(int c = 0; c<=cols; c++)
				{
					ArrayList<Integer> coords = new ArrayList<Integer>();
					for(int y=0; y<(Boss.getCanvasHeight()-5); y+=5)
					{
						//System.out.println("entering here");
						Random n = new Random(); 
						// (max - min) + min
						//int temp = n.nextInt((c+1)*subcol)-(c+1)*subcol; //Choose a random # between -subcol and 0
						int max = (c*4)*subcol;
						int min = (c*4-1)*subcol;
						int temp=0;
						if(c==0)
							temp = n.nextInt(subcol)-subcol;
						else
							temp = n.nextInt(max-min+1)+min;
						coords.add(temp); // Add x coord into our bushes
					}
					bushes.add(coords);
					
				}
				
			}
		}
		// Draw the bushes
		for(int i=0; i<bushes.size(); i++)
		{
			int y=0;
			for(int j=0; j<bushes.get(i).size(); j++)
			{
				g.fillOval(bushes.get(i).get(j), y, subcol, subcol);
				y+=5;
			}
		}
		


		if(Boss.initBush)
		{
			// Save screenshot of field 
			try {
				ImageIO.write(fieldImg, "PNG", new File("C:/Users/Ina/Programming/workspace/StrawberryRobots/images/fieldView.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Boss.wholeField = Highgui.imread("images/fieldView.png"); // Create a matrix out of the field
			Boss.gradientMat = new Mat();
			Boss.gradientMat.create(Boss.wholeField.rows(), Boss.wholeField.cols(), 1);
			Boss.gradientMat.setTo(Scalar.all(100)); // Init everything in the gradient to a large number
			//System.out.println(Boss.gradientMat.get(450, 450)[0]);
		}
		Boss.initBush = false;
		//TO-DO else put multiple 
	}
}
