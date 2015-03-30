import java.util.ArrayList;

public class Analyzer {
	
	public static byte get_dominant_overtone(ArrayList<Note> buffer) {
		return 0;
	}
	
	public static byte get_dominant_overtone(ArrayList<Note> buffer, KeySignature k) {
		return 0;
	}
	
	public static KeySignature get_key_signature(ArrayList<Chord> chord_history) {
		return null;
	}
	
	public static TimeSignature get_time_signature(ArrayList<Note> note_history) {
		return null;
	}
	
	public static int get_tempo(TimeSignature ts, ArrayList<Note> note_history) {
		return 0;
	}
}