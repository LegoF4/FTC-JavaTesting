package library;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

public class Camera {
	//G2 Intrinsic Parameters
	//public static final double F = 976.35;
	//public static final double X0 = 639.5;
	//public static final double Y0 = 359.5;
	//S5 Intrinsic Parameters
	public static final double F = 1138.4;
	public static final double X0 = 639.5;
	public static final double Y0 = 359.5;
	//S5 Front Camera 1280x720
	//public static final double F2 = 676.26;
	//public static final double X02 = 639.5;
	//public static final double Y02 = 359.5;
	//S5 Front Camera 1920x1080
	public static final double F2 = 1163.4;
	public static final double X02 = 959.5;
	public static final double Y02 = 539.5;
	//Rear distortion
	public static final double[] distortionCoefficents = new double[] {0.178f, -0.3389f, 0f, 0f, 0f};
	//Front Distortion
	public static final double[] distortionCoefficents2 = new double[] {0.089275f, -0.19792f, 0f, 0f, 0f};
	//Generated Matrices
	public static final Mat distortionMatrix = Camera.getDistortionMatrix();
	public static final Mat intrinsicMatrix = Camera.getInstrinsicMatrix();
	
	private static Mat getInstrinsicMatrix() {
		Mat cameraMatrix = new Mat(new Size(3,3), CvType.CV_32F);
        cameraMatrix.put(0, 0, new double[] {F});
        cameraMatrix.put(1, 1, new double[]{F});
        cameraMatrix.put(2, 2, new double[]{1.0f});
        cameraMatrix.put(0, 2, new double[]{X0});
        cameraMatrix.put(1, 2, new double[]{Y0});
        return cameraMatrix;
	}
	
	private static Mat getDistortionMatrix() {
		Mat distortionMatrix = new Mat(new Size(5, 1), CvType.CV_32F);
        distortionMatrix.put(0, 0, new double[]{distortionCoefficents[0]});
        distortionMatrix.put(0, 1, new double[]{distortionCoefficents[1]});
        distortionMatrix.put(0, 2, new double[]{distortionCoefficents[2]});
        distortionMatrix.put(0, 3, new double[]{distortionCoefficents[3]});
        distortionMatrix.put(0, 4, new double[]{distortionCoefficents[4]});
        return distortionMatrix;
	}

}
