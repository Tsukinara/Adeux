import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Profile {
	private HashMap<String, HashMap<String, Integer>> map;
	public String name;
	
	public Profile() {
		map = new HashMap<String, HashMap<String, Integer>>();
	}
	
	public Profile(String filename) {
		this.name = filename.substring(0, filename.length()-4);
		ArrayList<String> lines = new ArrayList<String>();
		map = new HashMap<String, HashMap<String, Integer>>();
		try {
			Scanner read = new Scanner(new File(filename));
			while (read.hasNextLine()) {
				lines.add(read.nextLine());
			}
			parse_profile(lines);
			read.close();
		} catch (FileNotFoundException e) {
			System.err.println("No file found. Using blank profile.");
		}
	}
	
	private void write_profile () {
		
	}
	
	private void parse_profile (ArrayList<String> in) {
		
	}
	
	
}