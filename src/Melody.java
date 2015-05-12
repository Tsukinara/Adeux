public class Melody {
	public int start_index, start_octave;
	public int next_index;
	public int chord_index;
	
	public char tsig, qual;
	public Type type;
	public boolean seven;
	
	public enum Type { TREBLE, BASS };
	
	public int[] times;
	public int[] notes;
	
	public Melody(String code) {
		try {
			String id = code.substring(0, code.indexOf(":") - 1);
			String rest = code.substring(code.indexOf(":") + 2, code.length());
			int num_notes = num_instances(rest, '-');
			times = new int[num_notes]; notes = new int[num_notes];
			parse_id(id);
			parse_melody(rest);
		} catch (Exception e) {
			System.err.println("Error: Invalid synthesis file.");
		}
	}
	
	private void parse_id(String id) {
		try {
			chord_index = Integer.parseInt(id.substring(0, id.indexOf('-')));
			id = id.substring(id.indexOf('-') + 1, id.length());
			seven = id.charAt(0) == '7';
			tsig = id.charAt(1); qual = id.charAt(3);
			type = (id.charAt(2) == 'B' ? Type.TREBLE : Type.BASS);
			next_index = Integer.parseInt(id.substring(id.indexOf('-') + 1, id.length()));
		} catch (Exception e) {
			System.err.println("Error: Invalid synthesis file");
		}
	}
	
	private void parse_melody(String mel) {
		for (int i = 0; mel.contains("-"); i++) {
			int ind = Integer.parseInt();
			notes[i] = 
		}
	}
	
	private int num_instances(String s, char c) {
		int total = 0; for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) total++; return total;
	}
	
	public String toString() {
		String s = "";
		s += "Chord index: " + chord_index + "\n";
		s += "Start index: " + start_index + "\n";
		s += "Next index:  " + next_index + "\n";
		s += "Is 7 chord?: " + seven + "\n";
		s += "TSig. Type:  " + tsig + "\n";
		s += "Chord qual.: " + qual + "\nNOTES:\n";
		for (int i = 0; i < notes.length; i++) s += notes[i] + " : " + times[i] + " ;; ";
		return s;
	}
	
	public static void main(String[] args) {
		String test = "0-0QTM-2 : (414-0)(014-4)(704-8)(414-12)(014-16)(704-20)(514-24)(014-28)(704-32)(414-36)(014-40)(704-44)";
		Melody m = new Melody(test);
		System.out.println(m);
	}
}