import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;


public class RobotView extends JPanel{
	
	ArrayList<Harvester> robots = Boss.robots;
	ArrayList<BufferedImage> strawberryPics = MainUI.strawberryPics;
	ArrayList<BufferedImage> masks = null;
	ArrayList<ArrayList<ArrayList<Integer>>> squaresPerView = null;
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		
		if(Boss.nextStep2==true)
		{
			masks = new ArrayList<BufferedImage>();
			// Give the robot a random strawberry field view
			Random i = new Random();
			for(int j=0; j<robots.size(); j++)
			{
				robots.get(j).setView(i.nextInt(6));  // choose a number between 0-5
			}
			// Display each robot's view
			detectStrawberries();
			showViews(g);
		}
	}

	private void showViews(Graphics g) 
	{
		int step = 50;
		
		// Robot 1
		g.drawImage(strawberryPics.get(robots.get(0).getView()), 0, 0, null);
		g.drawImage(masks.get(0), 260, 0, null); //alpha mask
		//For each square we found, draw a line!
		g.setColor(Color.BLUE);
		if(squaresPerView.get(0)!=null)
		{
			for(int i=0; i<squaresPerView.get(0).size();i++)
			{
				
				//1: x   0: y
				g.drawLine(squaresPerView.get(0).get(i).get(1), squaresPerView.get(0).get(i).get(0),
						   squaresPerView.get(0).get(i).get(1)+step, squaresPerView.get(0).get(i).get(0));
				g.drawLine(squaresPerView.get(0).get(i).get(1)+step, squaresPerView.get(0).get(i).get(0),
						   squaresPerView.get(0).get(i).get(1)+step, squaresPerView.get(0).get(i).get(0)+step);
				g.drawLine(squaresPerView.get(0).get(i).get(1)+step, squaresPerView.get(0).get(i).get(0)+step,
						   squaresPerView.get(0).get(i).get(1), squaresPerView.get(0).get(i).get(0)+step);
				g.drawLine(squaresPerView.get(0).get(i).get(1), squaresPerView.get(0).get(i).get(0)+step,
						   squaresPerView.get(0).get(i).get(1), squaresPerView.get(0).get(i).get(0));
				
			}
		}

		//Robot 2
		int offsetX = 530;
		int offsetY = 0;
		g.drawImage(strawberryPics.get(robots.get(1).getView()), offsetX, offsetY, null);
		g.drawImage(masks.get(1), 790, 0, null);
		if(squaresPerView.get(1)!=null)
		{		
			for(int i=0; i<squaresPerView.get(1).size();i++)
			{
				
				//1: x   0: y
				g.drawLine(squaresPerView.get(1).get(i).get(1)+offsetX, squaresPerView.get(1).get(i).get(0),
						   squaresPerView.get(1).get(i).get(1)+step+offsetX, squaresPerView.get(1).get(i).get(0));
				g.drawLine(squaresPerView.get(1).get(i).get(1)+step+offsetX, squaresPerView.get(1).get(i).get(0),
						   squaresPerView.get(1).get(i).get(1)+step+offsetX, squaresPerView.get(1).get(i).get(0)+step);
				g.drawLine(squaresPerView.get(1).get(i).get(1)+step+offsetX, squaresPerView.get(1).get(i).get(0)+step,
						   squaresPerView.get(1).get(i).get(1)+offsetX, squaresPerView.get(1).get(i).get(0)+step);
				g.drawLine(squaresPerView.get(1).get(i).get(1)+offsetX, squaresPerView.get(1).get(i).get(0)+step,
						   squaresPerView.get(1).get(i).get(1)+offsetX, squaresPerView.get(1).get(i).get(0));
				
			}
		}
		
		//Robot 3
		offsetX = 0;
		offsetY = 270;
		g.drawImage(strawberryPics.get(robots.get(2).getView()), offsetX, offsetY, null);
		g.drawImage(masks.get(2), 260, 270, null);
		if(squaresPerView.get(2)!=null)
		{		
			for(int i=0; i<squaresPerView.get(2).size();i++)
			{
				
				//1: x   0: y
				g.drawLine(squaresPerView.get(2).get(i).get(1)+offsetX, squaresPerView.get(2).get(i).get(0)+offsetY,
						   squaresPerView.get(2).get(i).get(1)+step+offsetX, squaresPerView.get(2).get(i).get(0)+offsetY);
				g.drawLine(squaresPerView.get(2).get(i).get(1)+step+offsetX, squaresPerView.get(2).get(i).get(0)+offsetY,
						   squaresPerView.get(2).get(i).get(1)+step+offsetX, squaresPerView.get(2).get(i).get(0)+step+offsetY);
				g.drawLine(squaresPerView.get(2).get(i).get(1)+step+offsetX, squaresPerView.get(2).get(i).get(0)+step+offsetY,
						   squaresPerView.get(2).get(i).get(1)+offsetX, squaresPerView.get(2).get(i).get(0)+step+offsetY);
				g.drawLine(squaresPerView.get(2).get(i).get(1)+offsetX, squaresPerView.get(2).get(i).get(0)+step+offsetY,
						   squaresPerView.get(2).get(i).get(1)+offsetX, squaresPerView.get(2).get(i).get(0)+offsetY);
				
			}
		}
		
		//Robot 4
		offsetX = 530;
		offsetY = 270;
		g.drawImage(strawberryPics.get(robots.get(3).getView()), offsetX, offsetY, null);
		g.drawImage(masks.get(3), 790, 270, null);
		if(squaresPerView.get(3)!=null)
		{		
			for(int i=0; i<squaresPerView.get(3).size();i++)
			{
				
				//1: x   0: y
				g.drawLine(squaresPerView.get(3).get(i).get(1)+offsetX, squaresPerView.get(3).get(i).get(0)+offsetY,
						   squaresPerView.get(3).get(i).get(1)+step+offsetX, squaresPerView.get(3).get(i).get(0)+offsetY);
				g.drawLine(squaresPerView.get(3).get(i).get(1)+step+offsetX, squaresPerView.get(3).get(i).get(0)+offsetY,
						   squaresPerView.get(3).get(i).get(1)+step+offsetX, squaresPerView.get(3).get(i).get(0)+step+offsetY);
				g.drawLine(squaresPerView.get(3).get(i).get(1)+step+offsetX, squaresPerView.get(3).get(i).get(0)+step+offsetY,
						   squaresPerView.get(3).get(i).get(1)+offsetX, squaresPerView.get(3).get(i).get(0)+step+offsetY);
				g.drawLine(squaresPerView.get(3).get(i).get(1)+offsetX, squaresPerView.get(3).get(i).get(0)+step+offsetY,
						   squaresPerView.get(3).get(i).get(1)+offsetX, squaresPerView.get(3).get(i).get(0)+offsetY);
				
			}
		}
		
		
		Boss.nextStep2 = false;
	}

	private void detectStrawberries() {
		squaresPerView = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for(int i=0; i<robots.size();i++)
		{
			//System.out.println("robot view: "+robots.get(i).getView());
			Mat img = robots.get(i).detectColor();
			ArrayList<ArrayList<Integer>> coords = robots.get(i).computeSquares(); // Get the location of all squares for THIS ONE VIEW
			if(coords.size()==0)
				squaresPerView.add(null);
			else
				squaresPerView.add(coords);
			//System.out.println("Robot"+(i+1)+" found "+coords.size());
			//Convert mat into buffered image
			MatOfByte bytemat = new MatOfByte();
			Highgui.imencode(".jpg", img, bytemat);
			byte[] bytes = bytemat.toArray();
			InputStream in = new ByteArrayInputStream(bytes);
			try {
				masks.add(ImageIO.read(in));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
