package dogecv;

import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class DataAnnotater {
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setSize(500, 300);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setTitle("Form Filler");
		JPanel p = new JPanel();
		JLabel l1 = new JLabel("Test of Image Display: ");
		//ImagePanel pI = new ImagePanel(ImageIO.read(new URL(getCodeBase(), "fieldI/field8.jpg")));
		p.add(l1);
		f.add(p);
		f.setVisible(true);
	}
}
