import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * 
 */

/**
 * @author jac.zhou
 *
 */
public class ImageIdentification {

	/**
	 * @param args
	 */
	public static final BufferedImage[][] cateType = new BufferedImage[5][100];                //hat, upper, lower, outer, accessory   
	//There will be about ten original samples in the pool for each first layer cateType
	                                                                        //purple, red, green, blue, yellow, orange, brown, black, white, grey
	public static final Color black = new Color(0, 0, 0);
	public static final Color white = new Color(255, 255, 255);
	/*public static int purpleR, purpleG, purpleB, redR, redG, redB, greenR, greenG, greenB, blueR, blueG, blueB, yellowR, yellowG, yellowB, orangeR, orangeG, orangeB, brownR, brownG, brownB, greyR, greyG, greyB;
	public static Color purple = new Color(purpleR, purpleG, purpleB);
	public static Color red = new Color(redR, redG, redB);
	public static Color green = new Color(greenR, greenG, greenB);
	public static Color blue = new Color(blueR, blueG, blueB);
	public static Color yellow = new Color(yellowR, yellowG, yellowB);
	public static Color orange = new Color(orangeR, orangeG, orangeB);
	public static Color brown = new Color(brownR, brownG, brownB);
	public static Color grey = new Color(greyR, greyG, greyB);*/
	public static long RED_VALUE, GREEN_VALUE, BLUE_VALUE;
	public static final int imgxHeight = 30, imgxWidth = 30;
	public static int[] sampleTypeCounter = new int[5];
	
	
	
	
	public static void main(String[] args) throws IOException {
		/*
try
{
    Image picture = ImageIO.read(new File("picture.png"));
}
catch (IOException e)
{
    String workingDir = System.getProperty("user.dir");
    System.out.println("Current working directory : " + workingDir);
    e.printStackTrace();
}
		 */
		Image picture = ImageIO.read(new File("C:/Users/jac.zhou/Desktop/hack-ucsc-gabe/hat samples/01.png"));
		BufferedImage image01 = new BufferedImage(picture.getWidth(null), picture.getHeight(null), BufferedImage.TYPE_INT_ARGB); 
		image01 = toBufferedImage(picture);
		/*Color c = colorScan(image01);
		String colorType = defineColor(c);
		System.out.println(colorType);
		System.out.println(c.getRed());
		System.out.println(c.getGreen());
		System.out.println(c.getBlue());*/
		
		
	}
	
	static BufferedImage blackedBufferedImage(BufferedImage img){
		int a = img.getHeight();
		int b = img.getWidth();
		BufferedImage imgx = new BufferedImage(a, b, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < a; i++){
			for (int j = 0; j < b; j++){
				if (img.getTransparency() != 0){
					imgx.setRGB(j, i, colorInt(black));
				}
			}
		}
		return imgx;
	}
	
	static BufferedImage creatingTypeSample(BufferedImage img){
		int a = img.getHeight();
		int b = img.getWidth();
		BufferedImage imgx = new BufferedImage(imgxWidth, imgxHeight, BufferedImage.TYPE_INT_ARGB);
		int pixelCounter = 0;
		int[] tempRGB = new int[a * b / ((a / imgxHeight) * (b / imgxWidth))];
		for (int i = 0; i < imgxWidth; i++){
			for (int j = 0; j < imgxHeight; j++){
				img.getRGB(i * (b / imgxWidth), j * (a / imgxHeight), (b / imgxWidth), (a / imgxHeight), tempRGB, 0, (b / imgxWidth));
				for (int k = 0; k < ((b / imgxWidth) * (a / imgxHeight)); k++){
					if (tempRGB[k] == colorInt(black))
						pixelCounter++;
					if (pixelCounter >= (((b / imgxWidth) * (a / imgxHeight)) / 2 + 1)){
						imgx.setRGB(i, j, colorInt(black));
					}
				}
			}
		}
		
		  return imgx;
	}
	
	static Color colorScan(BufferedImage img){
		RED_VALUE = 0;
		GREEN_VALUE = 0;
		BLUE_VALUE = 0;
		int a = img.getHeight();
		int b = img.getWidth();
		long pixelCounter = 0;
		long finalRed, finalGreen, finalBlue;
		int r, g, bl;
		for (int i = 0; i < a; i++){
			for (int j = 0; j < b; j++){
				if (img.getTransparency() != 0){
					pixelCounter++;
					Color c = new Color(img.getRGB(j, i));
					int red = c.getRed();
					int green = c.getGreen();
					int blue = c.getBlue();
					RED_VALUE += red;
					GREEN_VALUE += green;
					BLUE_VALUE += blue;
				}
			}
		}
		finalRed = RED_VALUE / pixelCounter;
		finalGreen = GREEN_VALUE / pixelCounter;
		finalBlue = BLUE_VALUE / pixelCounter;
		r = (int) finalRed;
		g = (int) finalGreen;
		bl = (int) finalBlue;
		Color averColor = new Color(r, g, bl);
		return averColor;
	}
	
	static int colorInt(Color c){
		return c.getRGB();
	}
	
	/*RGB range
	 * purple: (100, 255) (0, 100) (100, 255) ..
	 * red: (150, 255) (0, 40) (0, 100)..
	 * green: (0, 210) (130, 255) (0, 110)..
	 * blue: (0, 80) (0, 100) (130, 255)..
	 * yellow: (200. 255) (160, 200) (0, 150)..
	 * orange: (200. 255) (100, 150) (0, 100)..
	 * brown: (150, 210) (80, 180) (40, 110)..
	 * grey: R & G & B difference stay with in 25..
	 * 
	 */
	
	static String defineColor(Color averColor){
		int r = averColor.getRed();
		int g = averColor.getGreen();
		int b = averColor.getBlue();
		if (((r >= 190 && r <= 200) && (g >= 200 && g <= 255) && (b >= 170 && b <= 255)) || ((r >= 0 && r <= 25) && (g >= 0 && g <= 30) && (b >= 0 && b <= 40))){
			return "blue & green";
		} else if (((r >=145 && r <= 255) && (g >= 0 && g <= 40) && (b >= 0 && b <= 80)) || ((r >= 180 && r <= 230) && (g >= 95 && g <= 130) && (b >= 95 && b <= 130))){
			return "red";
		} else if ((r >=50 && r <= 200) && (g >= 0 && g <= 40) && (b >= 30 && b <= 80)){
			return "purple & violet";
		} else if (((r >= 50 && r <= 90) && (g >= 30 && g <= 70) && (b >= 0 && b <= 60)) || ((r >= 170 && r <= 220) && (g >= 145 && g <= 185) && (b >= 60 && b <= 100))){
			return "yellow & orange";
		} else if ((r >= 140 && r <= 170) && (g >= 90 && g <= 120) && (b >= 20 && b <= 100)){
			return "brown";
		} else if (((r - g <= 20 && r - g >=0) || (g - r >= 0 && g - r <= 20)) && ((r - b <= 20 && r - b >= 0) || (b - r <= 20 && b - r >= 0)) && ((g - b <= 20 && g - b >=0) || (b - g <= 20 && b - g >= 0))){
			return "almost grey & dark & white";
		} else return "others";
		
	}
	
	static String defineType(BufferedImage img, BufferedImage[][] cateType){
		double counter = 0;
		int[] likeliness =  new int[5];
		double accuracy = 0;
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 10; j++){
				for (int k = 0; k < imgxWidth; k++){
					for (int l = 0; l < imgxHeight; l++){
						if (img.getRGB(k, l) == cateType[i][j].getRGB(k, l)){
							counter++;
								
							}
						}
					}
				accuracy= counter / (imgxWidth * imgxHeight);
				if (accuracy >= 0.70){
					likeliness[i]++;
				}
			}
		}
		int[] likeliness2 = likeliness;
		int temp = 0;
		for (int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				if (likeliness2[j] > likeliness2[j + 1]){
					temp = likeliness2[j];
					likeliness2[j] = likeliness2[j + 1];
					likeliness2[j + 1] = temp;
				}
			}
		}
		
		if (likeliness2[4] == likeliness[0]){
			return "hat";
		} else if (likeliness2[4] == likeliness[1]){
			return "upper";
		} else if (likeliness2[4] == likeliness[2]){
			return "lower";
		} else if (likeliness2[4] == likeliness[3]){
			return "outer";
		} else if (likeliness2[4] == likeliness[4]){
			return "accessory";
		} else return "shoes & others";
	}

	static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	static void sampleTypeInput(){
		
	}
	
	static String addLabelByHuman(){
		Scanner in = new Scanner(System.in);
		String typeLabel = "";
		while (true){
		typeLabel = in.nextLine();
			if (typeLabel == "hat" || typeLabel == "upper" || typeLabel == "lower" || typeLabel == "outer" || typeLabel == "accessory" || typeLabel == "shoes & others")
				break;
		}
		return typeLabel;
	}
	
}
