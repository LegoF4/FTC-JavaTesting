package dogecv;
import java.applet.Applet;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.*;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.SystemException;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import library.ImageTransforms;
 
public class LoadImageApplet extends Applet {
 
     private BufferedImage img;
 
     public void init() {
    	 this.resize(this.getWidth()*7, this.getHeight()*3);
    	 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
         try {
        	 URL url = new URL(getCodeBase(), "fieldI/field1.jpg");
             img = ImageIO.read(url);
             Mat image = ImageTransforms.BufferedImage2Mat(img);
             
             Imgproc.resize(image, image, new Size(960, 540)); //High Res
        	 long startTime = System.currentTimeMillis();
             Mat rgbImage = image.clone();
             Mat filtered = new Mat();
             System.out.println(image.type());
             Imgproc.bilateralFilter(image, filtered, 5, 175, 175);
             image = filtered.clone();
             Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2Lab);
             
             Imgproc.erode(image, image, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
             Imgproc.GaussianBlur(image, image, new Size(3,3), 0);
             List<Mat> channels = new ArrayList<Mat>();
 			 Core.split(image, channels);
 			 
             Mat circles = new Mat();
             Imgproc.HoughCircles(channels.get(0), circles, Imgproc.CV_HOUGH_GRADIENT, 1.8, 60);
             System.out.println(circles.dump());
             for (int i = 0; i < circles.width(); i++) {
            	 double[] circle = circles.get(0, i);
            	 Rect rect = new Rect((int) (circle[0]-0.7*circle[2]),(int) (circle[1]-0.7*circle[2]), (int) (1.4*circle[2]), (int) (1.4*circle[2]));
            	 Imgproc.rectangle(rgbImage, rect.tl(), rect.br(), new Scalar(50, 200, 30));
            	 Imgproc.circle(rgbImage, new Point(circle[0], circle[1]), (int) circle[2], new Scalar(200, 50, 40), 5);
             }
             System.out.println("Elapsed Time: " + Double.toString(0.001*(System.currentTimeMillis() - startTime)));
             img = ImageTransforms.Mat2BufferedImage(rgbImage);
             img = ImageTransforms.resize(img, 1);
             
        	//URL url = new URL(getCodeBase(), "glyphs/major_sides/glyph.jpg");
        	 /**
             URL url = new URL(getCodeBase(), "fieldI/field5.jpg");
             System.out.println(url);
             img = ImageIO.read(url);
             Mat image = ImageTransforms.BufferedImage2Mat(img);
             System.out.println();
             System.out.println(image.size());
             Imgproc.resize(image, image, new Size(1920,1080));
             Imgproc.GaussianBlur(image, image ,new Size(3,3),0);
             Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
             Imgproc.Laplacian(image,image, image.type());
             Core.multiply(image, new Scalar(2), image);
             Imgproc.GaussianBlur(image, image ,new Size(7,7),0);
             //image = CryptoboxDetector.run(image,true);
            // Imgproc.circle(image, new Point(image.width()/2,image.height()/2), 20, new Scalar(200,30,40), 10);
             //image = LineDetector.run(image);
             img = ImageTransforms.Mat2BufferedImage(image);
             img = ImageTransforms.resize(img, 0.5);
             //**/
        	 /**
        	 URL url = new URL(getCodeBase(), "glyphsI/glyph16.jpg");
             img = ImageIO.read(url);
             Mat image = ImageTransforms.BufferedImage2Mat(img);
             /**Mat cameraMatrix = new Mat(new Size(3,3), CvType.CV_32F);
             cameraMatrix.put(0, 0, new double[]{1138.4f});
             cameraMatrix.put(1, 1, new double[]{1138.4f});
             cameraMatrix.put(2, 2, new double[]{1.0f});
             cameraMatrix.put(0, 2, new double[]{639.5f});
             cameraMatrix.put(1, 2, new double[]{359.5f});
             Mat distortionMatrix = new Mat(new Size(5, 1), CvType.CV_32F);
             distortionMatrix.put(0, 0, new double[]{0.178f});
             distortionMatrix.put(0, 1, new double[]{-0.3389f});
             distortionMatrix.put(0, 2, new double[]{0f});
             distortionMatrix.put(0, 3, new double[]{0f});
             distortionMatrix.put(0, 4, new double[]{0f});
             //Generate look-up tables for remapping the camera image
             Mat resized = new Mat();
             Mat undistorted = new Mat();
             Imgproc.resize(image, resized, new Size(1280, 720));
             Imgproc.undistort(resized, undistorted, cameraMatrix, distortionMatrix);
             //img = ImageTransforms.resize(img,0.4);
             System.out.println(System.currentTimeMillis() - startT);**/
        	 /**
             Mat output = new Mat();
             Imgproc.resize(image, output, new Size(1280,720));
             Mat image1 = GlyphDetector.run(output);
             img = ImageTransforms.Mat2BufferedImage(image1);
             img = ImageTransforms.resize(img,0.4);
             //**/
             /**
        	 int detected= 0;
        	 long totalTime = 0;
        	 int start = 1;
        	 int count = 27;
        	 double detectedTot = 0;
             for (int i = start; i < start+count; i++) {
            	 System.out.println("---------Glyph " + Integer.toString(i) + ":---------");
                 URL url = new URL(getCodeBase(), "glyphsI/s5/glyph" + Integer.toString(i) + ".jpg");
                 img = ImageIO.read(url);
            	 Mat image = ImageTransforms.BufferedImage2Mat(img);
            	 long startT = System.currentTimeMillis();
            	 List<Glyph> glyphs = GlyphDetector.run(image);
            	 if(glyphs.size() > 0) detected++;
            	 detectedTot += glyphs.size();
            	 totalTime += System.currentTimeMillis() - startT;
             }
             System.out.println("---------Stats---------");
             System.out.println("Detection Rate: " + Double.toString(( (int) (10000*detected/count))/100) + "%");
             System.out.println("Average Elapsed Time: " + Double.toString(0.001*(totalTime/count)) + "s");
             System.out.println("Average Glyphs Detected: " + Double.toString(detectedTot/count));
             //**/

         } catch (IOException e) {
        	 
         }
     }
     
     public void print(String line) {
    	 System.out.println(line);
     }
 
     public void paint(Graphics g) {
       g.drawImage(img, 5, 5, null);
     }
     
     /**
     
             URL url = new URL(getCodeBase(), "glyphs/major_sides/glyph3.jpg");
             img = ImageIO.read(url);
             Mat image = ImageTransforms.BufferedImage2Mat(img);
             long startT = System.currentTimeMillis();
             Mat bgra = new Mat();
             Mat gray = new Mat();
             Imgproc.cvtColor(image, bgra, Imgproc.COLOR_BGR2BGRA);
             Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
             GlyphDetectorDodge glyphDetector = new GlyphDetectorDodge();
             glyphDetector.minScore = 1;
             glyphDetector.downScaleFactor = 0.1;
             glyphDetector.speed = GlyphDetectorDodge.GlyphDetectionSpeed.FAST;
             Mat output = glyphDetector.processFrame(bgra, gray);
             System.out.println("Elapsed Time: " + Double.toString(0.001*(System.currentTimeMillis() - startT)));
             img = ImageTransforms.Mat2BufferedImage(output);
             img = ImageTransforms.resize(img, 0.4);
      **/

     /**
     Mat cameraMatrix = new Mat(new Size(3,3), CvType.CV_32F);
     cameraMatrix.put(0, 0, new double[]{976.35f});
     cameraMatrix.put(1, 1, new double[]{976.35f});
     cameraMatrix.put(2, 2, new double[]{1.0f});
     cameraMatrix.put(0, 2, new double[]{639.5f});
     cameraMatrix.put(1, 2, new double[]{359.5f});
     System.out.println(cameraMatrix.dump());
     Mat distortionMatrix = new Mat(new Size(5, 1), CvType.CV_32F);
     //distortionMatrix.put(0, 0, new double[]{0.0605f});
     //distortionMatrix.put(0, 1, new double[]{0.0203f});
     distortionMatrix.put(0, 2, new double[]{0f});
     distortionMatrix.put(0, 3, new double[]{0f});
     distortionMatrix.put(0, 4, new double[]{0f});
     System.out.println(distortionMatrix.dump());
     //Generate look-up tables for remapping the camera image
     Mat image = ImageTransforms.BufferedImage2Mat(img);
     Mat undistorted = new Mat();
     Imgproc.undistort(image, undistorted, cameraMatrix, distortionMatrix);**/
}