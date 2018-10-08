package library;
import org.opencv.core.Point;
import org.opencv.core.Size;

public class Points {
	
	//Points
	/**
	 * Returns whether a given point is within the boundary of the image.
	 * @param point The point to be tested
	 * @param size The Size object of the image
	 * @return True if in bounds, false otherwise
	 */
	public static boolean inBounds(Point point, Size size) {
		if(point.x < size.width - 1 && point.x >= 0 && point.y < size.height-1 && point.y >= 0) {
			return true;
		}
    	else {
    		return false;
    	}
	}
	

    
    public static Point reverseTransform(double[] worldCoordinates, double f, double x0, double y0) {
    	double u1 = f*(worldCoordinates[0]/worldCoordinates[1]) + x0;
    	double v1 = -f*(worldCoordinates[2]/worldCoordinates[1]) + y0;
    	//System.out.println("u1, v1: " + Double.toString((int) 2*u1) + ", " + Double.toString(v1));
    	return new Point((int) (2.55*u1), (int) (2.55*v1));
    }
	
}
