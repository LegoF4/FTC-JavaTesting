package dogecv;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import library.ImageTransforms;
import library.MathFTC;

public class ShapeListener implements MouseListener {

	
	List<Circle> circles;
	List<Circle> circlesNew;
	List<Rect> rects;
	List<Rect> rectsNew;
	
	Mat displayMat;
	
	@Override
	public void mouseClicked(MouseEvent event) {
		synchronized(this) {
			Point point = event.getPoint();
			circles = DataAnnotater.circles;
			circlesNew = new ArrayList<Circle>();
			rects = DataAnnotater.rects;
			rectsNew = new ArrayList<Rect>();
			if(circles.size() > 0 || rects.size() > 0) {
				displayMat = DataAnnotater.workingMat.clone();
				if(circles.size() > 0) {
					double dist;
					for (Circle circle : circles) {
						dist = Math.pow(circle.x - point.x, 2) + Math.pow(circle.y - point.y, 2);
						dist = Math.sqrt(dist);
						if(Math.abs(dist-circle.radius) > 15) {
							circlesNew.add(circle);
						}
					}
					for (Circle circle : circlesNew) { 
						Imgproc.circle(displayMat, new org.opencv.core.Point(circle.x, circle.y), (int) circle.radius, new Scalar(200, 50, 40), 5);
					}
					DataAnnotater.circles = circlesNew;
				}
				if(rects.size() > 0) {
					for (Rect rect : rects) {
						if(Math.abs(point.x-rect.tl().x) < 15 && MathFTC.inRange(point.y, rect.tl().y-10, rect.br().y+10)) continue;
						if(Math.abs(point.x-rect.br().x) < 15 && MathFTC.inRange(point.y, rect.tl().y-10, rect.br().y+10)) continue;
						if(Math.abs(point.y-rect.tl().y) < 15 && MathFTC.inRange(point.x, rect.tl().x-10, rect.br().x+10)) continue;
						if(Math.abs(point.y-rect.br().y) < 15 && MathFTC.inRange(point.x, rect.tl().x-10, rect.br().x+10)) continue;
						rectsNew.add(rect);
					}
					for (Rect rect : rectsNew) {
						Imgproc.rectangle(displayMat, rect.tl(), rect.br(), new Scalar(40,40,255),2); // Draw rect
					}
					DataAnnotater.rects = rectsNew;
				}
				DataAnnotater.displayMat = displayMat;
				try {
					DataAnnotater.imageLabel.setIcon(new ImageIcon(ImageTransforms.Mat2BufferedImage(displayMat)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
			
			
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	
}
