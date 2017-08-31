import java.awt.Color;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;


public class Harvester {
	
	private int x;
	private int y;
	private int initX;
	private int initY;
	private int orientation; // Let 1:Down, 2:Right, 3:Up, 4:Left
	private int fruitPicked;
	private int view;
	private Mat mask;
	private ArrayList<ArrayList<Integer>> allSquares;
	
	Harvester(){
		x = -1;
		y = -1;
		initX = -1;
		initY = -1;
		orientation = 1; // 1: Down, 0: Up
		fruitPicked = 0;
		view = -1;
		mask = new Mat();
		mask.create(250, 250,3);
		allSquares = new ArrayList<ArrayList<Integer>>();
	}
	
	void addFruit(int n)
	{
		fruitPicked += n;
	}
	void emptyBasket()
	{
		fruitPicked = 0;
	}
	void init(int x, int y)
	{
		initX = x;
		initY = y;
	}	
	void setX(int px)
	{
		x = px;
	}
	int getX()
	{
		return x;
	}
	void setY(int py)
	{
		y = py;
	}
	int getY()
	{
		return y;
	}
	void setO(int o)
	{
		orientation = o;
	}
	int getO()
	{
		return orientation;
	}
	void setView(int i)
	{
		view = i;
	}
	int getView()
	{
		return view;
	}
	Mat detectColor() // Create a binary mask by detecting a variance of red
	{
		//System.out.println("robotx view: "+view);
		Mat img = MainUI.strawberryMats.get(view);
	   // double r = 255.0;
	   // double g = 0.0;
	   // double b = 0.0;
	    for(int row=0; row<mask.rows(); row++)
	    {
	    	for(int col=0; col<mask.cols(); col++)
	    	{
	    		int rv = (int) img.get(row, col)[2];
	    		int gv = (int) img.get(row, col)[1];
	    		int bv = (int) img.get(row, col)[0];
	    		if(rv > 137 && gv<80 && bv<80)
	    		{
	    			mask.put(row, col, new double[]{255,255,255}); //Make it white
	    		}
	    		else
	    			mask.put(row, col, new double[]{0,0,0}); //Make it black
	    	}
	    }
	    //System.out.println(mask.channels());
	    mask = nearestNeighbor(mask);
		return mask;
	}
	Mat nearestNeighbor(Mat mask) // Use NN to fill in holes of the binary mask
	{
		// Grab 10x10 submasks. middle point -5 +5 on both sides
		Mat copy = mask.clone();
		for(int r=5; r<(mask.rows()-5); r++)
		{
			for(int c=5; c<(mask.cols()-5); c++)
			{
				Mat block = mask.submat(r-5,r+5,c-5,c+5);
				// If most of your neighbors are white, you should be white too
				double sum = 0;
				for(int i=0; i<block.rows(); i++)
				{
					for(int j=0; j<block.cols(); j++)
					{
						if(block.get(i, j)[0]==255.0)
						{
							sum+=1.0;
						}
					}
				}					

				double p = (sum)/100.0;
				//System.out.println("sum="+sum+" p="+p);
				if(p>0.3)
				{
					copy.put(r, c, new double[]{255,255,255});
				}
				else
					copy.put(r, c, new double[]{0,0,0});
			}
		}
		
		return copy;
	}
	ArrayList<ArrayList<Integer>> computeSquares() // Loop through img and figure out where the strawberries are
	{
		
		// Look for coords
		ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
		int sSize = 65;
		double p = 0;
		double total = sSize*sSize;
		for(int row=0; row<(mask.rows()); row+=5)
		{
			for(int col=0; col<(mask.cols()); col+=5)
			{
				//System.out.println("row = "+row+" col= "+col);
				if((row+sSize)<=mask.rows() && (col+sSize)<=mask.cols()) // Must make sure we can stay within bounds
				{
					// Compute probability
					Mat block = mask.submat(row, row+sSize, col, col+sSize);
					double sum = 0;
					//System.out.println(block.dump());
					for(int i=0; i<block.rows(); i++)
					{
						for(int j=0; j<block.cols(); j++)
						{
							//System.out.println(block.get(i, j)[0]);
							if(block.get(i, j)[0]==255.0)
								sum += 1.0;
						}
					}
					p = sum/total;
					//System.out.println("sum="+sum+" total="+total+" p="+probability);
					if(p>0.35)
					{
						//System.out.println("Found a high probability");
						ArrayList<Integer> coords = new ArrayList<Integer>();
						// 0: row start, 1: row end, 2:col start, 3: col end
						coords.add(row);
						coords.add(col);
						temp.add(coords);
						//System.out.println(allSquares.size());
						//System.out.println("("+row+","+col+") = ("+coords.get(0)+","+coords.get(1)+")");
						row +=sSize;
						col +=sSize;
					}
				}
				
			}
		}
		return temp;
	}
	void computeLocalGradient()
	{
		//System.out.println("size of wholeField: "+ Boss.wholeField.rows() + " x " + Boss.wholeField.cols());
		// Assume we can sense a maximum of 10 pixels ahead of the robot
		int maxRange = 10;
		
		if(orientation==1)
		{
			// Take a submatrix from out wholeField out using the center of the robot
			Mat sensorView = new Mat();
			int rStart = y;
			int rEnd = y+maxRange;
			int cStart = 0;
			if(x-((int)Boss.grid_cell_w/2)>0)
				cStart = x-((int)Boss.grid_cell_w/2);
			int cEnd = Boss.canvasW;
			if(x+((int)Boss.grid_cell_w/2)<Boss.canvasW)
				cEnd = x+((int)Boss.grid_cell_w/2);
			//System.out.println("cstart & end = "+cStart+" "+ cEnd);
			sensorView = Boss.wholeField.submat(rStart,rEnd,cStart,cEnd);
			// Set our start position in our gradient as 0
			// DEBUGGING MATRIX
			Mat tempM = new Mat(); 
			tempM.create(Boss.gradientMat.rows(),Boss.gradientMat.cols(), 1);
			tempM.setTo(Scalar.all(100));
			// delete above when done
	
			// Keep looping through our submatrix from left to right, then right to left
			// and add our distance from the obstacles into Boss.gradient
			Boss.gradientMat.put(y, x, 0); // Setting our initial position to 0
			for(int stepsForward=0; stepsForward<maxRange; stepsForward++) // Look at one step forward
			{
				// NOTE: since sensorView is a submatrix, we need to figure out a way to map leftObsCoord(from our full map) to our submatrix
				// Our offset = cStart
				int leftObsCoord = x-cStart; // WITH RESPECT TO OUR SENSOR VIEW!!!
				//System.out.println("lefObs="+leftObsCoord);
				//System.out.println(y+stepsForward);
				//System.out.println("test="+sensorView.get(stepsForward, leftObsCoord)[0]); 
				
				while(sensorView.get(stepsForward, leftObsCoord)[0]!=60 &&
					  sensorView.get(stepsForward, leftObsCoord)[1]!=120 &&
					  sensorView.get(stepsForward, leftObsCoord)[2]!=0 && leftObsCoord>1)
				{
					leftObsCoord--;
				}
				// Look right until you hit an obstacle
				// Offset cEnd
				int rightObsCoord = cEnd-x; // WITH RESPECT TO OUR SENSOR VIEW!!!
				while(sensorView.get(stepsForward, rightObsCoord)[0]!=60 &&
					  sensorView.get(stepsForward, rightObsCoord)[1]!=120 &&
					  sensorView.get(stepsForward, rightObsCoord)[2]!=0 && rightObsCoord<sensorView.cols()-1)
				{
					rightObsCoord++;
				}
				//System.out.println(leftObsCoord + " " + rightObsCoord);
				
				// Now that we have found the obstacle in the next step, we want to sum up the "gradients"
				// Our obstacle is located at (y+stepsForward (row) ,leftObsCoord (col))
				// So use our gradient matrix, set that value at the obstacle location to 100 and -1 as we get further away
				Boss.gradientMat.put(y+stepsForward, cStart+leftObsCoord, 100);
				
				for(int i=cStart+leftObsCoord+1;i<cStart+rightObsCoord;i++) // As we move away from our obstacle from left to right, subtract 1 
				{
					int prevMagnitude = (int) Boss.gradientMat.get(y+stepsForward, i-1)[0];
					Boss.gradientMat.put(y+stepsForward, i, prevMagnitude-1);
					tempM.put(y+stepsForward, i, prevMagnitude-1);
					//System.out.println(Boss.gradientMat.get(y+stepsForward, i)[0]);
				}
				
				// Our right obstacle is located at (y+stepsForward (row) ,rightObsCoord (col))
				Boss.gradientMat.put(y+stepsForward, cStart+rightObsCoord, 100);
				// Now do the same but starting from the right obstacle towards the left (using negative values)
				
				int prevMagnitude = 100;
				for(int i=cStart+rightObsCoord-1;i>cStart+leftObsCoord;i--) // As we move away from our obstacle from left to right, subtract 1 
				{
					//int prevMagnitude = (int) Boss.gradientMat.get(y+stepsForward, i+1)[0];
					prevMagnitude -= 1;
					int currMagnitude = (int) Boss.gradientMat.get(y+stepsForward, i)[0];
					int temp = Math.abs(currMagnitude - prevMagnitude);
					Boss.gradientMat.put(y+stepsForward, i, temp);
		
					//System.out.println(Boss.gradientMat.get(y+stepsForward, i)[0]);
				}
		
	
			}
		//Mat temp = tempM.submat(rStart,rEnd,cStart,cEnd); // This is our left to right obstacle matrix only
		//System.out.println(temp.dump());
		//Mat temp2 = Boss.gradientMat.submat(rStart,rEnd,cStart,cEnd);
		//System.out.println("Gradient for next 10 steps=\n"+temp2.dump());
		}
		
	}
	
	void followGradient()
	{
		// You have your x,y coords. Now figure out what's the best way to move for the next y step

		// cStart & cEnd =  to help figure out which columns to search through for our min value
		int cStart = 0;
		if(x-((int)Boss.grid_cell_w/2)>0)
			cStart = x-((int)Boss.grid_cell_w/2);
		int cEnd = Boss.canvasW;
		if(x+((int)Boss.grid_cell_w/2)<Boss.canvasW)
			cEnd = x+((int)Boss.grid_cell_w/2);
		
		// Look at our gradient for the next step
		int min = 100; // min value
		int nextX = -1; // to store our next x coord
		int nextY = getY() + 1; // to store our next y coord
		
		//Figure out our min value in row(nextY) in our gradient
		for(int i=cStart; i<cEnd; i++)
		{
			int iValue = Math.abs((int) Boss.gradientMat.get(nextY, i)[0]);
			if(iValue < min)
			{
				min = iValue;
				nextX = i;		
			}
		}

		//System.out.println("current y="+y+" next y="+nextY);
		setX(nextX);
		setY(nextY);
		//System.out.println("next x="+getX());
	}
}
