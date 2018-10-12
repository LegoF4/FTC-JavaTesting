package dogecv;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import library.ImageTransforms;

public class ShapeListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent event) {
		System.out.println(event.getPoint().toString());
		synchronized(this) {
			Point point = event.getPoint();
			List<Circle> circles = DataAnnotater.circles;
			List<Circle> circlesNew = new ArrayList<Circle>();
			if(circles.size() > 0) {
				double dist;
				Mat displayMat = DataAnnotater.workingMat.clone();
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
