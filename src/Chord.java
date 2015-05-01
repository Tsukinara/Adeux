import java.awt.Graphics2D;

public class Chord {
	public String name;
	public byte inv, type, qual;
	public short base;
	public boolean seven;
	public byte[] notes;
	
	/*
	 * FORMATTING FOR CHORD NAMES:
	 * A-BCDX
	 * A: number with value 1-7, indicating position of root
	 * B: -1, 0, or 1, indicating flat, natural, or sharp, respectively
	 * C: 0, 1, 2, or 3, indicating which inversion the chord is in
	 * D: 0 or 7, indicating whether the chord is a triad or a seventh
	 * X: M, m, d, or a, indicating major, minor, diminished, or augmented
	 */
	public Chord (String name) {
		
	}
	
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