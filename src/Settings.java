import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Settings {
	public TimeSignature tsig;
	public KeySignature ksig;
	public short window_size;
	public int tempo;
	public boolean harmonize;
	public ChordType chord_type;
	public short bgm_vol, harm_vol;
	
	public enum ChordType {
		ROMAN, SYMBOL
	}
	
	public Settings () {
		this.tsig = null;		this.ksig = null;
		this.window_size = -1;
		this.tempo = -1;
		this.harmonize = true;
		this.chord_type = ChordType.ROMAN;
		this.bgm_vol = 100;		this.harm_vol = 100;
	}
	
	public Settings (File f) {
		ArrayList<String> settings = new ArrayList<String>();
 		try {
			Scanner read = new Scanner(f);
			while (read.hasNextLine()) {
				settings.add(read.nextLine());
			}
			parse_settings(settings);
			read.close();
		} catch (FileNotFoundException e) {
			System.err.println("No file found. Using default settings.");
			this.tsig = null;		this.ksig = null;
			this.window_size = -1;
			this.tempo = -1;
			this.harmonize = true;
			this.chord_type = ChordType.ROMAN;
			this.bgm_vol = 100;		this.harm_vol = 100;
		}
	}
	
	public void write_settings(String filename) {
		
	}

	private void parse_settings(ArrayList<String> settings) {
		String skey = "";	boolean smaj = true;
		this.chord_type = ChordType.ROMAN;
		try {
			for (String s : settings) {
				String key = s.substring(0, 4);
				String val = s.substring(5);
				if (key.equals("tsig")) {
					if (!val.equals("null")) {
						short top = (short)Integer.parseInt(val.substring(0, val.indexOf('/')));
						short bot = (short)Integer.parseInt(val.substring(val.indexOf('/') + 1));
						this.tsig = new TimeSignature(top, bot);
					} else this.tsig = null;
				}
				if (key.equals("disp")) this.window_size = (short)Integer.parseInt(val);
				if (key.equals("skey"))	skey = val.trim();
				if (key.equals("styp")) smaj = val.equals("maj");
				if (key.equals("harm")) this.harmonize = val.equals("true");
				if (key.equals("type")) if (val.equals("symbol")) this.chord_type = ChordType.SYMBOL;
				if (key.equals("bgmv")) this.bgm_vol = (short)Integer.parseInt(val);
				if (key.equals("hrmv")) this.harm_vol = (short)Integer.parseInt(val);
				if (key.equals("tmpo")) this.tempo = Integer.parseInt(val);
			}
		if (skey.equals("null")) this.ksig = null;
		else this.ksig = new KeySignature(skey, smaj);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Invalid settings file format");
		}
	}
	
	public String toString() {
		String s = "==== SETTINGS ====\n";
		s += "Time Sig:\t" + (tsig == null ? "auto" : tsig.toString()) + "\n";
		s += "Key Sig:\t" + (ksig == null ? "auto" : ksig.toString()) + "\n";
		s += "Accomp:\t\t" + (harmonize ? "on" : "off") + "\n";
		s += "Tempo:\t\t" + (tempo > 0 ? tempo + " bpm" : "auto") + "\n";
		s += "Chords:\t\t" + (chord_type == ChordType.ROMAN ? "roman" : "symbol") + "\n";
		if (window_size > 0) s += "Display:\t" + window_size + "x" + (short)(window_size/16*9);
		else s += "Display:\tFull Screen";
		s += "\n";
		s += "BGM Vol:\t" + bgm_vol + "%\n";
		s += "Accomp Vol:\t" + harm_vol + "%\n";
		return s;
	}
	
	public static void main(String[] args) {
		File f = new File("profile\\settings.dat");
		Settings s = new Settings(f);
		System.out.println(s.toString());
	}
}