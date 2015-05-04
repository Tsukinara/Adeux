import java.awt.Graphics2D;

public class Chord {
	public String name;
	public byte inv, type, qual;
	public short base;
	public boolean seven;
	public byte[] notes;
	public String code;
	
	/*
	 * FORMATTING FOR CHORD NAMES:
	 * 
	 * STANDARD:
	 * A-BCDX
	 * A: number with value 1-7, indicating position of root
	 * B: -1, 0, or 1, indicating flat, natural, or sharp, respectively
	 * C: 0, 1, 2, or 3, indicating which inversion the chord is in
	 * D: 0 or 7, indicating whether the chord is a triad or a seventh
	 * X: M, m, d, or a, indicating major, minor, diminished, or augmented
	 * 
	 * SECONDARY DOMINANT:
	 * FA-BC
	 * F: literally the character "F"
	 * A: number with 1-7, indicating position of the chord being tonicized
	 * B: 0, 1, 2, or 3, indicating the inversion of the chord
	 * C: 0 or 7, indicating whether the chord is a triad or a seventh
	 * 
	 * SUSPENSIONS:
	 * SA-BC
	 * S: literally the character "S"
	 * A: number 1-7, indicating position of the chord being suspended
	 * B: 2 or 4, for sus2 or sus4
	 * C: 0 or 7, indicating whether the chord is a triad or a seventh
	 * 
	 * SPECIAL
	 * CAD64-A
	 * A: number 1-7, indicating the position of the chord being resolved to
	 */
	public Chord (String code) {
		switch (code.charAt(0)) {
			case 'F':
			case 'S':
			case 'C':
			default:
		}
	}
	
	public String code() { return this.code; }
	
	public String get_name() {
		return "";
	}
	
	public String get_name(KeySignature k) {
		return "";
	}
	
	public byte[] get_notes(KeySignature k) {
		return notes;
	}
	
	public void draw_roman(Graphics2D g, int x, int y, int size) {
		
	}
	
	public void draw_symbol(Graphics2D g, int x, int y, int size, KeySignature k) {
		
	}
}