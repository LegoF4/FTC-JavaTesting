package library;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
    /**
     * Assumes square input.
     * @param mat
     * @param maxSize
     * @return
     */
    public static Mat resizeToMax(Mat mat, Size maxSize) {
    	Size newSize = mat.width() > mat.height() ? new Size((int) (maxSize.width), (int) (mat.height()*maxSize.width/mat.width())) : new Size((int) (mat.width()*maxSize.height/mat.height()), (int) (maxSize.height));
    	Imgproc.resize(mat, mat, newSize);
    	Mat newMat = Mat.zeros(maxSize, CvType.CV_8UC3);
    	Core.copyMakeBorder(mat, newMat, 0, (int) (maxSize.height - newSize.height), 0, (int) (maxSize.width-newSize.width), Core.BORDER_CONSTANT, new Scalar(0,0,0));
    	mat.release();
    	return newMat;
    }

}
