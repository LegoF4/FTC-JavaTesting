package library;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageTransforms {
	
    public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
   	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
   	    ImageIO.write(image, "jpg", byteArrayOutputStream);
   	    byteArrayOutputStream.flush();
   	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
   }
    
    public static BufferedImage Mat2BufferedImage(Mat matrix)throws IOException {
   	    MatOfByte mob=new MatOfByte();
   	    Imgcodecs.imencode(".jpg", matrix, mob);
   	    return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
   	}
    
    public static BufferedImage resize(BufferedImage img, double scale) {
    	int w = img.getWidth();
    	int h = img.getHeight();
    	BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    	AffineTransform at = new AffineTransform();
    	at.scale(scale, scale);
    	AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    	after = scaleOp.filter(img, after);
    	return after;
    }

}
