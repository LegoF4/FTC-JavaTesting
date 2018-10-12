package dogecv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class SilverDetector {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	private List<Circle> circlesL;
	
	public double sensitivity = 1.9;
	public double minDist = 60;
	
	private Mat workingMat = new Mat();
	
	public SilverDetector() {
		
	}
	
	public void analyzeImage(Mat input) {
		workingMat = input.clone();
		//Imgproc.resize(workingMat, workingMat, new Size(900, (900*(workingMat.size().height/workingMat.size().width)))); //High Res
        Mat filtered = new Mat();
        Imgproc.bilateralFilter(workingMat, filtered, 5, 175, 175);
        workingMat = filtered.clone();
        Imgproc.cvtColor(workingMat, workingMat, Imgproc.COLOR_BGR2Lab);
        
        Imgproc.erode(workingMat, workingMat, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
        Imgproc.GaussianBlur(workingMat, workingMat, new Size(3,3), 0);
        List<Mat> channels = new ArrayList<Mat>();
		Core.split(workingMat, channels);
		 
        Mat circlesM = new Mat();
        circlesL = new ArrayList<Circle>();
        
        Imgproc.HoughCircles(channels.get(0), circlesM, Imgproc.CV_HOUGH_GRADIENT, sensitivity, minDist);
        System.out.println(circlesM.dump());
        for (int i = 0; i < circlesM.width(); i++) {
       	 double[] circle = circlesM.get(0, i);
       	 circlesL.add(new Circle(circle[0], circle[1], circle[2]));
        }
	}
	
	public synchronized List<Circle> getCircles() {
		return circlesL;
	}
	
}
