package dogecv;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class FileIOTest {
	
	public static void main(String[] args) throws URISyntaxException {
		File folder = new File(DataAnnotater.class.getResource("../fieldI").toURI());
		
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
		    System.out.println("File " + listOfFiles[i].getName());
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}
	}
}
