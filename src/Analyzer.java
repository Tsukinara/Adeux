import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Analyzer {
	
	public static byte get_dominant_overtone(ArrayList<Note> buffer) {
		return 0;
	}
	
	public static byte get_dominant_overtone(ArrayList<Note> buffer, KeySignature k) {
		return 0;
	}
	
	public static KeySignature get_key_signature(ArrayList<Chord> history) {
		return null;
	}
	
	public static TimeSignature get_time_signature(ArrayList<Note> note_history) {
		return null;
	}
	
	public static int get_tempo(TimeSignature ts, ArrayList<Note> note_history) {
		return 0;
	}
	
	public static String get_chord_context_free (ArrayList<Note> notes, int min) {
		ArrayList<Integer> unique = new ArrayList<Integer>();
		for (Note n : notes) if (!unique.contains(n.key())) unique.add(n.key());
		if (unique.size() > 4) return "unknown";
		if (unique.size() < min) return "unknown";
		Integer [] uniq = unique.toArray(new Integer [unique.size()]);
		int triad = get_triad(uniq); String ret = "";
		if (triad < 0) {
			int seventh = get_seventh(uniq);
			if (seventh < 0) {
				int third = get_third(uniq);
				if (third < 0) return "unknown";
				ret += (third < 2 ? Music.getRoot((int)uniq[0]) : Music.getRoot((int)uniq[1]));
				return ret + (third%2 == 0 ? "maj" : "min");
			}
			switch (seventh) {
				case 0: case 1: case 2: case 3: case 100: case 101: case 102: case 103:
					ret += Music.getRoot((int)uniq[0]); break;
				case 4: case 5: case 6: case 7: case 104: case 105: case 106: case 107:
					ret += Music.getRoot((int)uniq[1]); break;
				case 8: case 9: case 10: case 11: case 108: case 109: case 110: case 111:
					ret += Music.getRoot((int)uniq[2]); break;
				case 112: case 113: case 114: case 115:
					ret += Music.getRoot((int)uniq[3]); break;
			}
			if (seventh%4 == 0) return ret + "dom7";
			if (seventh%4 == 1) return ret + "maj7";
			if (seventh%4 == 2) return ret + "min7";
			return ret + "dim7";
		} else {
			switch (triad) {
				case 0: case 1: case 2: ret += Music.getRoot((int)uniq[0]); break;
				case 3: case 4: case 5: ret += Music.getRoot((int)uniq[1]); break;
				case 6: case 7: case 8: ret += Music.getRoot((int)uniq[2]); break;
			}
			if (triad%3 == 0) return ret + "maj";
			if (triad%3 == 1) return ret + "min";
			return ret + "dim";
		}
	}
	
	public static int get_triad(Integer[] nk) {
		if (nk.length != 3) return -1;
		for (int i = 0; i < 3; i++) {
			if (has(nk, (nk[0]+4)%12) && has(nk, (nk[0]+7)%12)) return i*3 + 0;
			if (has(nk, (nk[0]+3)%12) && has(nk, (nk[0]+7)%12)) return i*3 + 1;
			if (has(nk, (nk[0]+3)%12) && has(nk, (nk[0]+6)%12)) return i*3 + 2;
			nk = circ_shift(nk);
		}
		return -1;
	}
	
	public static int get_third(Integer[] nk) {
		if (nk.length != 2) return -1;
		if ((nk[0]+4)%12 == nk[1]) return 0;
		if ((nk[0]+3)%12 == nk[1]) return 1;
		if ((nk[1]+4)%12 == nk[0]) return 2;
		if ((nk[1]+3)%12 == nk[0]) return 3;
		return -1;
	}
	
	public static int get_seventh(Integer[] nk) {
		int type = -20; //0-dom, 1-maj, 2-min, 3-dim 
		int inv = 0;
		if (nk.length != 3 && nk.length != 4) return -1;
		if (nk.length == 3) {
			for (int i = 0; i < 3; i++) {
				if (has(nk, (nk[0]+4)%12) && has(nk, (nk[0]+10)%12)) { inv = i*4; type = 0; }
				if (has(nk, (nk[0]+4)%12) && has(nk, (nk[0]+11)%12)) { inv = i*4; type = 1; }
				if (has(nk, (nk[0]+3)%12) && has(nk, (nk[0]+10)%12)) { inv = i*4; type = 2; }
				if (has(nk, (nk[0]+3)%12) && has(nk, (nk[0]+9 )%12)) { inv = i*4; type = 3; }
				nk = circ_shift(nk);
			}
			if  (type > -1) return type+inv;
			return -1;
		} else {
			for (int i = 0; i < 4; i++) {
				if (has(nk, (nk[0]+4)%12) && has(nk, (nk[0]+10)%12) && has(nk, (nk[0]+7)%12)) { inv = i*4; type = 0; }
				if (has(nk, (nk[0]+4)%12) && has(nk, (nk[0]+11)%12) && has(nk, (nk[0]+7)%12)) { inv = i*4; type = 1; }
				if (has(nk, (nk[0]+3)%12) && has(nk, (nk[0]+10)%12) && has(nk, (nk[0]+7)%12)) { inv = i*4; type = 2; }
				if (has(nk, (nk[0]+3)%12) && has(nk, (nk[0]+ 9)%12) && has(nk, (nk[0]+6)%12)) { inv = i*4; type = 3; }
				nk = circ_shift(nk);
			}
			if (type > -1) return type+inv+100;
			return -1;
		}
	}
	
	public static void print_arr(Integer[] in) { 
		System.out.print("[" + in[0]);
		for (int i = 1; i < in.length; i++) System.out.print(", " + in[i]);
		System.out.println("]");
	}
	
	private static boolean has(Integer[]in, int k) { return Arrays.asList(in).contains(k); }
	
	private static Integer[] circ_shift(Integer[] in) {
		Integer[] out = new Integer[in.length];
		for (int i = 1; i < in.length; i++)
			out[i-1] = in[i];
		out[in.length-1] = in[0];
		return out;
	}
	
	public static Chord get_chord(ArrayList<Note> rel, KeySignature key) {
		Collections.sort(rel);
		if (rel.size() == 0) return null;
		int dom = rel.get(0).key();
		ArrayList<Integer> keys = new ArrayList<Integer>();
		for (Note n : rel) keys.add(n.key());
		String c = "";
		switch (dom) {
			case 0: 
				if (keys.contains(4)) c = "F2-0" + (keys.contains(10)? "7" : "0") + "m";
				else c = "6-10" + (keys.contains(10)? "7" : "0") + "m";
				break;
			case 1:
				c = "7-000M";
				break;
			case 2:
				c = "7-100d";
				break;
			case 3:
				c = "1-100M";
				break;
			case 4:
				c = "F2-10m";
				break;
			case 5:
				c = "2-100m";
				break;
			case 6:
				c = "4-137M";
				break;
			case 7:
				if (keys.contains(0)) c = "CAD64-6";
				else if (keys.contains(11)) c = "F6-10m";
				else c = "3-100m";
				break;
			case 8:
				if (keys.contains(5)) c = "2-110m";
				else c = "4-100M";
				break;
			case 9:
				c = "F5-10M";
				break;
			case 10:
				if (keys.contains(3)) c = "CAD64-1";
				else if (keys.contains(8)) c = "5-107M";
				else c = "5-100M";
				break;
			case 11:
				c = "6-000M";
				break;
		}
		return new Chord(c);
	}
	
	public static Chord get_chord(ArrayList<Note> held, ArrayList<Chord> history, KeySignature key) {
		return null;
	}
	
	public static void main(String [] args) {
		NoteBuffer nb = new NoteBuffer(null);
		MidiHandler mh = new MidiHandler(nb);
		mh.imNotUseless();
	}
}