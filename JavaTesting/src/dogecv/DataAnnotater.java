package dogecv;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import library.ImageTransforms;

public class DataAnnotater {
	
	public static JFrame f = new JFrame();
	public static JPanel organizer = new JPanel();
	public static SilverDetector detectorSilver = new SilverDetector();
	public static List<Circle> circles = new ArrayList<Circle>();
	public static GoldDetector detectorGold = new GoldDetector();
	public static List<Rect> rects = new ArrayList<Rect>();
	public static Mat workingMat;
	public static Mat displayMat;
	public static Size size = new Size(950,950);
	public static int count = 0;
	public static JLabel imageLabel;
	public static JTextField sensitivityField = new JTextField("1.7");
	public static JTextField minDistField = new JTextField("60");
	public static Font fontL = new Font("serif", Font.PLAIN, 24);
	public static Font fontS = new Font("serif", Font.PLAIN, 20);
	public static File[] images;
	
	public static final String path = "file:///C:/Users/LeviG/Documents/Robotics (FTC)/FTC 2018-2019/FTC Code/Java-Testing/JavaTesting/bin/fieldI/";
	
	public static void main(String[] args) throws MalformedURLException, IOException, URISyntaxException {
		DataAnnotater.init();
	}
	
	public static void init() throws IOException, URISyntaxException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		count = 0;
		images = new File(DataAnnotater.class.getResource("../fieldI").toURI()).listFiles();
		if(images.length == 0) {
			f.setSize(400, 400);
			f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			f.setTitle("Data Annotater");
			JLabel failureMessage = new JLabel("No images found. Re-enter path.");
			failureMessage.setFont(fontL);
			f.add(failureMessage);
			f.setVisible(true);
			return;
		}
		BufferedImage loadedImage = ImageIO.read(images[0]);
		System.out.println("Height: " + Double.toString(loadedImage.getHeight()));
    	workingMat = ImageTransforms.BufferedImage2Mat(loadedImage);
    	workingMat = ImageTransforms.resizeToMax(workingMat, size);
    	displayMat = workingMat.clone();
		f.setSize(1920, 1080);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setTitle("Data Annotater");
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(5,1));
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(2,2));
		
		JLabel inputTitle = new JLabel("Test of Image Display: ");
		inputTitle.setFont(fontL);
		
		JLabel sensitivityLabel = new JLabel("Sensitivity: ");
		sensitivityLabel.setFont(fontS);
		JLabel minDistLabel = new JLabel("Min Dist: ");
		minDistLabel.setFont(fontS);
		inputPanel.add(sensitivityLabel);
		inputPanel.add(sensitivityField);
		inputPanel.add(minDistLabel);
		inputPanel.add(minDistField);
		
		JPanel pnPanel = new JPanel();
		pnPanel.setLayout(new GridLayout(1,2));
		JButton next = new JButton("Next");
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				synchronized(this) {
					try {
						nextImage();
					} catch (IOException e) {
						System.out.println("I give up 1");
					}
				}
			}
		});
		JButton previous = new JButton("Previous");
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				synchronized(this) {
					try {
						previousImage();
					} catch (IOException e) {
						System.out.println("I give up 2");
					}
				}
			}
		});
		pnPanel.add(previous);
		pnPanel.add(next);
		
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				synchronized(this) {
					try {
						updateImage();
					} catch(Exception e) {
						System.out.println("I give up 3");
					}
				}
			}
		});
		
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				synchronized(this) {
					try {
						exportShapes();
					} catch(Exception e) {
						System.out.println("I give up 4");
					}
				}
			}
		});

		controlPanel.add(inputTitle);
		controlPanel.add(inputPanel);
		controlPanel.add(refresh);
		controlPanel.add(export);
		controlPanel.add(pnPanel);
		
		JPanel imagePanel = new JPanel();
		imageLabel = new JLabel(new ImageIcon(ImageTransforms.Mat2BufferedImage(displayMat)));
		imageLabel.addMouseListener(new ShapeListener());
		imagePanel.add(imageLabel, "Center");
		organizer.add(imagePanel, "East");
		organizer.add(controlPanel, "West");
		
		f.getContentPane().add(organizer, "Center");
		f.add(organizer);
		f.setVisible(true);
	}
	
	public static void nextImage() throws IOException {
		if(count + 1 < images.length) {
			count += 1;
		}
		workingMat = ImageTransforms.BufferedImage2Mat(ImageIO.read(images[count]));
		workingMat = ImageTransforms.resizeToMax(workingMat, size);
		updateImage();
		System.out.println("Next Image");
	}
	
	public static void previousImage() throws IOException {
		if(count - 1 >= 0) {
			count -= 1;
		}
		workingMat = ImageTransforms.BufferedImage2Mat(ImageIO.read(images[count]));
		workingMat = ImageTransforms.resizeToMax(workingMat, size);
		updateImage();
		System.out.println("Previous Image");
	}
	
	public static void updateImage() throws IOException {
		try {
			detectorSilver.minDist = Double.parseDouble(minDistField.getText());
			detectorSilver.sensitivity = Double.parseDouble(sensitivityField.getText());
		} catch (NumberFormatException e) {
			System.out.println("Try Again");
			return;
		}
		detectorSilver.analyzeImage(workingMat);
		displayMat = workingMat.clone();
		circles = detectorSilver.getCircles();
		detectorGold.analyzeImage(workingMat);
		rects = detectorGold.getCubes();
		System.out.println(rects.size());
		for (Circle circle : circles) { 
	       	 Imgproc.circle(displayMat, new Point(circle.x, circle.y), (int) circle.radius, new Scalar(200, 50, 40), 5);
		}
		imageLabel.setIcon(new ImageIcon(ImageTransforms.Mat2BufferedImage(displayMat)));
	}
	
	public static void exportShapes() {
		
	}
}
