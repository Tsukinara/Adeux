import java.awt.Font;
import java.awt.Graphics2D;

public class Chord {
	private static final String[] roman = {"", "I", "II", "III", "IV", "V", "VI", "VII"};
	private static final String[] types = {"\u00ec  ", "", "\u00ef  "};
	private static final String[] invs3 = {"", "6", "64"};
	private static final String[] invs7 = {"7", "65", "43", "\u00d1"};
	private static final String[] quals = {"\u00ba", "\u00f8", "&"};
	public String name;
	// inv: 0, 1, 2, or 3 for each respective inversion
	// type: -1, 0, or 1 for flat, natural, or sharp
	// susp: 0, 2, or 4, depending on suspension
	public byte inv, type, susp;
	
	// qual: M, m, d, h, or a, for Major, minor, diminished, half-diminished, or augmented
	public char qual;
	
	// base: number representing root position, is the chord being tonicized in the case of secondary dom
	public short base;
	
	// true if the chord is a seven chord
	public boolean seven;
	
	public byte[] notes;
	public String code;
	
	/*
	 * FORMATTING FOR CHORD NAMES:
	 * 
	 * STANDARD:
	 * A-BCDX
	 * A: number with value 1-7, indicating position of root
	 * B: 0, 1, or 2, indicating flat, natural, or sharp, respectively
	 * C: 0, 1, 2, or 3, indicating which inversion the chord is in
	 * D: 0 or 7, indicating whether the chord is a triad or a seventh
	 * X: M, m, d, or h, indicating major, minor, diminished, or half-diminished
	 * 
	 * SECONDARY DOMINANT:
	 * FA-BCX
	 * F: literally the character "F"
	 * A: number with 1-7, indicating position of the chord being tonicized
	 * B: 0, 1, 2, or 3, indicating the inversion of the chord
	 * C: 0 or 7, indicating whether the chord is a triad or a seventh
	 * X: M or m, indicating major, minor for the chord being tonicized
	 * 
	 * SUSPENSIONS:
	 * SA-BCD
	 * S: literally the character "S"
	 * A: number 1-7, indicating position of the chord being suspended
	 * B: 2 or 4, for sus2 or sus4
	 * C: 0 or 7, indicating whether the chord is a triad or a seventh
	 * D: M or m, indicating major or minor suspension
	 * 
	 * SPECIAL
	 * CAD64-A
	 * A: number 1-7, indicating the position of the chord being resolved to
	 */
	public Chord (String code) {
		this.code = code;
		if (!code.equals("")) {
			switch (code.charAt(0)) {
				case 'F': 
					this.base = Short.parseShort(code.charAt(1) + "");
					this.inv = Byte.parseByte(code.charAt(3) + "");
					this.type = 1; // assume all secondary dominants are not NSTs
					this.seven = code.charAt(4) == '7';
					this.qual = code.charAt(5); // assume all secondary dominants are major
					this.susp = 0; // assume all secondary dominants aren't suspended
					break;
				case 'S':
					this.base = Short.parseShort(code.charAt(1) + "");
					this.inv = 0; // assume all suspensions are in root position
					this.type = 1; // assume all suspensions are not NSts
					this.seven = code.charAt(4) == '7';
					this.qual = code.charAt(5);
					this.susp = Byte.parseByte(code.charAt(3) + "");
					break;
				case 'C':
					this.base = Short.parseShort(code.charAt(6) + "");
					this.inv = 0; this.type = 1; this.seven = false; // lot of assumptions
					this.susp = 0; this.qual = 'M'; // assume cadential 6-4s are not suspended and major
					break;
				default:
					this.base = Short.parseShort(code.charAt(0) + "");
					this.type = Byte.parseByte(code.charAt(2) + "");
					this.inv = Byte.parseByte(code.charAt(3) + "");
					this.seven = code.charAt(4) == '7';
					this.qual = code.charAt(5);
					this.susp = 0;
			}
		}
	}
	
	public String code() { return this.code; }
	
	public String get_roman_name() {
		String ret = "";
		if (code.equals("")) return "";
		switch (code.charAt(0)) {
			case 'F':
				ret += "V";
				if (seven) ret += invs7[inv];
				else ret += invs3[inv];
				ret += "/";
				ret += (qual == 'M' ? roman[base] : roman[base].toLowerCase());
				break;
			case 'S':
				ret += roman[base];
				if (qual == 'm') ret = ret.toLowerCase();
				ret += susp + " _" + (susp-1);
				break;
			case 'C':
				ret += "cadV64";
				if (base != 1) ret += "/" + roman[base];
				break;
			default:
				ret += types[type];
				ret += (qual == 'M' ? roman[base] : roman[base].toLowerCase());
				switch (qual) {
					case 'd': ret += quals[0]; break;
					case 'h': ret += quals[1]; break;
					case 'a': ret += quals[2]; break;
				}
				if (seven) ret += invs7[inv];
				else ret += invs3[inv];
		}
		return ret;
	}
	
	public String get_sym_name(KeySignature k) {
		return "";
	}
	
	public byte[] get_notes(KeySignature k) {
		return notes;
	}
	
	public boolean is_chord_tone(Note n) {
		return false;
	}
	
	public void draw_roman(Graphics2D g, int x, int y, int size) {
		g.setFont(new Font("Opus Chords Std", Font.PLAIN, size));
		int fw = g.getFontMetrics().stringWidth(get_roman_name());
		g.drawString(get_roman_name(), x - (fw/2), y);
	}
	
	public void draw_symbol(Graphics2D g, int x, int y, int size, KeySignature k) {
		
	}
	
	public static void main(String [] args) {
		Chord c = new Chord("5-017M");
		System.out.println(c.code());
		System.out.println(c.get_roman_name());
	}
}