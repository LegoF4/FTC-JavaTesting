package dogecv;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import library.Line;
import library.Lines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CryptoboxDetector  {

    public enum CryptoboxDetectionMode {
        HSV_RED, HSV_BLUE
    }

    public enum CryptoboxSpeed {
        VERY_FAST, FAST, BALANCED, SLOW, VERY_SLOW
    }


    public CryptoboxDetectionMode detectionMode      = CryptoboxDetectionMode.HSV_RED;
    public double                 downScaleFactor    = 0.6;
    public boolean                rotateMat          = false;
    public CryptoboxSpeed         speed              = CryptoboxSpeed.BALANCED;
    public boolean debugShowMask = true;


    private boolean CryptoBoxDetected = false;
    private boolean ColumnDetected = false;
    private int[] CryptoBoxPositions = new int[3];


    Scalar lower = new Scalar(90, 135, 25);
    Scalar upper = new Scalar(130, 250, 150);

    private Mat workingMat = new Mat();
    private Mat mask1  = new Mat();
    private Mat mask2  = new Mat();
    private Mat mask  = new Mat();
    private Mat hsv  = new Mat();
    private Mat structure  = new Mat();
    private Mat hierarchy = new Mat();
    Mat kernel = Mat.ones(5,5,CvType.CV_32F);

    public Mat processFrame(Mat bgr, Mat gray, Mat zeros) {
        Size initSize= bgr.size();
        Size newSize  = new Size(initSize.width * downScaleFactor, initSize.height * downScaleFactor);
        //Mat clone = bgr.clone();
        //Imgproc.resize(clone, clone, newSize);
        bgr.copyTo(workingMat);
        
        Mat bgrRe = new Mat();
		Imgproc.resize(bgr, bgrRe, newSize);

        Imgproc.resize(workingMat, workingMat,newSize);
        Imgproc.putText(workingMat,newSize.toString() + speed.toString(),new Point(5,15),0,0.6,new Scalar(0,255,255),2);
        if(rotateMat){
            Mat tempBefore = workingMat.t();

            Core.flip(tempBefore, workingMat, 1); //mRgba.t() is the transpose

            tempBefore.release();
        }



        List<MatOfPoint> contours = new ArrayList<>();
        List<Rect> boxes = new ArrayList<>();

        Imgproc.erode(workingMat, workingMat,kernel);
        Imgproc.dilate(workingMat, workingMat,kernel);
        Imgproc.cvtColor(workingMat,hsv,Imgproc.COLOR_BGR2HSV);


        switch(detectionMode){
            case HSV_RED:
                getRedMask(hsv);
                break;
            case HSV_BLUE:
                getBlueMask(hsv);
        }



        switch (speed){
            case VERY_FAST:
                Imgproc.blur(hsv,hsv,new Size(3,3));
                structure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1,30));
                Imgproc.morphologyEx(mask,mask,Imgproc.MORPH_CLOSE, structure);
                break;
            case FAST:
                Imgproc.blur(hsv,hsv,new Size(4,4));
                structure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,30));
                Imgproc.morphologyEx(mask,mask,Imgproc.MORPH_CLOSE, structure);
                break;

            case BALANCED:
                Imgproc.blur(hsv,hsv,new Size(5,5));
                structure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,40));
                Imgproc.morphologyEx(mask,mask,Imgproc.MORPH_CLOSE, structure);
                break;


            case SLOW:
                Imgproc.blur(hsv,hsv,new Size(7,7));
                structure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,55));
                Imgproc.morphologyEx(mask,mask,Imgproc.MORPH_CLOSE, structure);
                break;

            case VERY_SLOW:
                Imgproc.blur(hsv,hsv,new Size(8,8));
                structure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,60));
                Imgproc.morphologyEx(mask,mask,Imgproc.MORPH_CLOSE, structure);
                break;
        }




        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        
        for(MatOfPoint c : contours) {
            if(Imgproc.contourArea(c) >= newSize.height / 4 * 30) { //Filter by area
                Rect column = Imgproc.boundingRect(c);
                int ratio = Math.abs(column.height / column.width);
        		
                if(ratio > 1.5) { //Check to see if the box is tall
                    boxes.add(column); //If all true add the box to array
                    /**
                    Mat mask = zeros.clone();
            		Imgproc.fillConvexPoly(mask, c, new Scalar(255,255,255));
                    //Imgproc.rectangle(mask,new Point(column.x,column.y),new Point(column.x+column.width,column.y+column.height),new Scalar(255,255,255),2);
            		Mat masked = Mat.zeros(newSize, CvType.CV_8UC3);
            		Imgproc.resize(gray, gray, newSize);
            		clone.copyTo(masked, mask);
            		List<Line> lines = Lines.getOpenCvLines(masked, 3, 40);
            		List<Point> centers = new ArrayList<Point>();
            		double maxX = 0;
            		double maxI = -1;
            		double minX = 100000;
            		double minI = -1;
            		for (int i =0; i < lines.size(); i++) {
            			Line line = lines.get(i);
            			if(Lines.getAngularDistance(line, new Line(new Point(0,0), new Point(1000,0))) < 40) {
            				lines.remove(line);
            				i = i-1;
            			} else if(line.center().x > maxX) {
            				maxX = line.center().x;
            				maxI = i;
            			} else if(line.center().x < minX) {
            				minX = line.center().x;
            				minI = i;
            			}
            			centers.add(line.center());
            		}
            		for (int i =0; i < lines.size(); i++) {
            			Line line = lines.get(i);
            			if(i == maxI) {
            				line = Lines.linearExtend(line, 4, newSize);
            				Imgproc.line(workingMat, line.point1, line.point2, new Scalar(150,230,20),2);
            			} else if(i == minI) {
            				line = Lines.linearExtend(line, 4, newSize);
            				Imgproc.line(workingMat, line.point1, line.point2, new Scalar(150,230,20),2);
            			} else {
            				Imgproc.line(workingMat, line.point1, line.point2, new Scalar(40,200,30),2);
            			}
            			
            		}**/
                }
            }
        }
        for(Rect box : boxes) {
            Imgproc.rectangle(workingMat,new Point(box.x,box.y),new Point(box.x+box.width,box.y+box.height),new Scalar(255,0,0),2);
        }

        Collections.sort(boxes, new Comparator<Rect>() {
            @Override
            public int compare(Rect rect, Rect t1) {
                if(rect.x > t1.x){
                    return 1;
                }else if(rect.x < t1.x){
                    return -1;
                }else{
                    return 0;
                }
            }
        });

        CryptoBoxDetected = boxes.size() >=4;
        if(CryptoBoxDetected){
        	
            Point left = drawSlot(0,boxes);
            Point center = drawSlot(1,boxes);
            Point right = drawSlot(2,boxes);

            CryptoBoxPositions[0] = (int)left.x;
            CryptoBoxPositions[1] = (int)center.x;
            CryptoBoxPositions[2] = (int)right.x;

            Imgproc.putText(workingMat, "Left", new Point(left.x - 10, left.y - 20), 0,0.8, new Scalar(0,255,255),2);
            Imgproc.circle(workingMat,left,5,new Scalar(0,255,255), 3);

            Imgproc.putText(workingMat, "Center", new Point(center.x - 10, center.y - 20), 0,0.8, new Scalar(0,255,255),2);
            Imgproc.circle(workingMat,center, 5,new Scalar(0,255,255), 3);

            Imgproc.putText(workingMat, "Right", new Point(right.x - 10, right.y - 20), 0,0.8, new Scalar(0,255,255),2);
            Imgproc.circle(workingMat,right, 5,new Scalar(0,255,255), 3);
        }else{
            for(int i=0;i<boxes.size() - 1;i++){
                Point collumn = drawSlot(i,boxes);
                Imgproc.circle(workingMat,collumn,5,new Scalar(0,255,255), 3);
                if(i<3){
                    CryptoBoxPositions[i] = (int)collumn.x;
                }
            }

            ColumnDetected = boxes.size() > 1;
        }

        if(rotateMat){

            Mat tempAfter = workingMat.t();

            Core.flip(tempAfter, workingMat, 0); //mRgba.t() is the transpose

            tempAfter.release();
        }

        Imgproc.resize(workingMat, workingMat, initSize);


        return workingMat;


    }

    public Mat getRedMask(Mat input){
        Scalar lower1 = new Scalar(0,150,100);
        Scalar upper1 = new Scalar(20,255,255);

        Scalar lower2 = new Scalar(140,100,100);
        Scalar upper2 = new Scalar(179,255,255);


        Core.inRange(input,lower1,upper1,mask1);


        Core.inRange(input,lower2,upper2,mask2);

        Core.addWeighted(mask1,1.0, mask2,1.0, 0.0, mask);
        return mask;
    }

    public Mat getBlueMask(Mat input){

        Scalar lower = new Scalar(90, 135, 25);
        Scalar upper = new Scalar(130, 250, 150);



        Core.inRange(input,lower,upper,mask);
        return mask;
    }


    public Point drawSlot(int slot, List<Rect> boxes){
        Rect leftColumn = boxes.get(slot); //Get the pillar to the left
        Rect rightColumn = boxes.get(slot + 1); //Get the pillar to the right

        int leftX = leftColumn.x; //Get the X Coord
        int rightX = rightColumn.x; //Get the X Coord

        int drawX = ((rightX - leftX) / 2) + leftX; //Calculate the point between the two
        int drawY = leftColumn.height + leftColumn.y; //Calculate Y Coord. We wont use this in our bot's opetation, buts its nice for drawing

        return new Point(drawX, drawY);
    }

    public ArrayList ones(int width, int height) {
        ArrayList output = new ArrayList();
        for(int i = 1; i <= height; i++) {
            ArrayList row = new ArrayList();
            for(int j = 1; i <= width; i++) {
                row.add(1);
            }
            output.add(row);
        }
        return output;
    }

    public int[] getCryptoBoxPositions() {
        return CryptoBoxPositions;
    }

    public int getCryptoBoxLeftPosition() {
        return CryptoBoxPositions[0];
    }

    public int getCryptoBoxCenterPosition() {
        return CryptoBoxPositions[1];
    }

    public int getCryptoBoxRightPosition() {
        return CryptoBoxPositions[2];
    }

    public boolean isCryptoBoxDetected() {
        return CryptoBoxDetected;
    }

    public boolean isColumnDetected() {
        return ColumnDetected;
    }

}