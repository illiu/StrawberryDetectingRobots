import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class MainUI {

	static ArrayList<BufferedImage> strawberryPics = null;
	static ArrayList<Mat> strawberryMats = null;
	public static void main(String[] args)
	{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		/* Ask the User for the dimensions of the 'farm' */
		Scanner input = new Scanner(System.in);
		// TODO: CHECK IF VALID INPUT
		System.out.println("Width in ft? ");
		double w = input.nextDouble(); // width
		System.out.println("Height in ft? ");
		double h = input.nextDouble(); // height
		
		System.out.println("Size of farm: "+ w + " ft x " + h + " ft");
		run(w,h);
		
	}
	public static void run(double w, double h)
	{
		addStrawberryPics();
		Boss boss = new Boss();
		boss.start(w,h);
	}
	
	public static void addStrawberryPics()
	{
		strawberryPics = new ArrayList<BufferedImage>();
		strawberryMats = new ArrayList<Mat>();
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("images/s1.png"));
			strawberryMats.add(Highgui.imread("images/s1.png"));
			strawberryPics.add(img);
			
			img = ImageIO.read(new File("images/s2.png"));
			strawberryMats.add(Highgui.imread("images/s2.png"));
			strawberryPics.add(img);
			
			img = ImageIO.read(new File("images/s3.png"));
			strawberryMats.add(Highgui.imread("images/s3.png"));
			strawberryPics.add(img);
			
			img = ImageIO.read(new File("images/s4.png"));
			strawberryMats.add(Highgui.imread("images/s4.png"));
			strawberryPics.add(img);
			
			img = ImageIO.read(new File("images/s5.png"));
			strawberryMats.add(Highgui.imread("images/s5.png"));
			strawberryPics.add(img);
			
			img = ImageIO.read(new File("images/s6.png"));
			strawberryMats.add(Highgui.imread("images/s6.png"));
			strawberryPics.add(img);
		} catch (IOException e) {
			System.out.println("unable to open image");
			e.printStackTrace();
		}
	}
}
