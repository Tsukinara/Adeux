import java.util.ArrayList;

public class NoteBuffer {
	private static final int max_notes = 200;
	private static final int key_track = 10;
	public static final int same_thresh = 50000;
	public static final int tril_thresh = 500000;
	private static final int poly = 10;
	private static final int bass_dist = 16;
	
	// contains any notes whose frequencies are dominant
	public ArrayList<Note> note_buffer;
	
	// contains the notes that are held at the time
	public ArrayList<Note> hold_buffer;
	
	// contains any notes that are relevant
	public ArrayList<Note> rel_buffer;
	public ArrayList<Note> all_buffer;
	public ArrayList<Note> history;
	public ArrayList<Note> key_analysis;
	
	//contains all notes within 1.5 octaves of the dominant overtone
	protected ArrayList<Note> bass;
	private ArrayList<Note> marks;

	
	private byte dominant;
	private Display parent;
	
	public boolean damped;
	public Chord curr_chord;
	public KeySignature curr_key;
	
	/*
	 * Initializes the note buffer, as well as the hold buffer
	 */
	public NoteBuffer(Display parent) {
		this.parent = parent;
		this.note_buffer = new ArrayList<Note>();
		this.hold_buffer = new ArrayList<Note>();
		this.rel_buffer = new ArrayList<Note>();
		this.all_buffer = new ArrayList<Note>();
		this.history = new ArrayList<Note>();
		this.marks = new ArrayList<Note>();
		this.damped = false;
		this.bass = new ArrayList<Note>();
		this.key_analysis = new ArrayList<Note>();
		this.dominant = -1;
	}
	
	public void reinit() {
		this.note_buffer = new ArrayList<Note>();
		this.hold_buffer = new ArrayList<Note>();
		this.rel_buffer = new ArrayList<Note>();
		this.all_buffer = new ArrayList<Note>();
		this.history = new ArrayList<Note>();
		this.marks = new ArrayList<Note>();
		this.damped = false;
		this.bass = new ArrayList<Note>();
		this.key_analysis = new ArrayList<Note>();
		this.dominant = -1;
	}
	
	/*
	 * Adds a note to the hold buffer as well as the note buffer. Damped should
	 * always be true when this method is called. If the same note is already in the 
	 * note buffer, the most recent one is kept
	 */
	public synchronized void add_note(byte id, boolean damped, byte vel, long time) {
		Note n = new Note(id, vel, damped, time, this);
		Note tmp = null;
		for (Note nt : note_buffer) {
			if (nt.id() == id) tmp = nt;
		}
		analyze_dominant(n);
		
		if (Math.abs(id - this.dominant) < bass_dist && !bass.contains(n)) { bass.add(n); }
		for (Note nt : bass) {
			if (same_time(nt, n) && !rel_buffer.contains(n)) rel_buffer.add(n);
			for (int i = history.size() - poly; i < history.size(); i++)
				if (i >= 0) {
					Note ntmp = history.get(i);
					if (same_time(nt, ntmp) && !rel_buffer.contains(ntmp)) rel_buffer.add(ntmp);
				}
		}
		note_buffer.remove(tmp);
		note_buffer.add(n);
		hold_buffer.add(n);
		all_buffer.add(n);
		add_history(n);
		add_akey(n);
		
		if (parent != null) parent.note_pressed(id, vel, time);
		recalculate();
	}
	
	public synchronized void change_dom(Note nt) {
		if (this.dominant != nt.id()) {
			this.dominant = nt.id();
			ArrayList<Note> tmp = new ArrayList<Note>();
			for (Note n : rel_buffer) if (!hold_buffer.contains(n)) tmp.add(n);
			for (Note n : tmp) rel_buffer.remove(n); tmp.clear();
			for (Note n : all_buffer) if (!hold_buffer.contains(n)) tmp.add(n);
			for (Note n : tmp) all_buffer.remove(n); tmp.clear();
			for (Note n : bass) if (!hold_buffer.contains(n)) tmp.add(n);
			for (Note n : tmp) bass.remove(n); tmp.clear();
			this.bass.add(nt);
			this.rel_buffer.add(nt);
		}
	}
	
	private synchronized void add_history(Note n) {
		history.add(n);
		if (history.size() > max_notes) {
			Note tmp = history.remove(0);
			destroy_note(tmp);
		}
	}
	
	private synchronized void analyze_dominant(Note n) {
		if (dominant == -1) change_dom(n);
		else if (n.id() < dominant) change_dom(n);
	}
	
	private boolean same_time(Note a, Note b) {
		return Math.abs(a.get_start() - b.get_start()) < same_thresh;
	}
	
	private void recalculate() {
		if (parent.set.ksig == null) {
			KeySignature k = Analyzer.get_key_signature(key_analysis, this.curr_key);
			System.out.println((k!= null ? k.toString() : "unknown"));
			if (k != null) this.curr_key = k;
		} else this.curr_key = parent.set.ksig;
		Chord c = Analyzer.get_chord(rel_buffer, all_buffer, dominant, curr_key, curr_chord);
		if (c != null) this.curr_chord = c;
	}
	
	private synchronized void add_akey(Note n) {
		key_analysis.add(n);
		ArrayList<Note> tmp = new ArrayList<Note>();
		for (int i = 0; i < key_analysis.size(); i++) {
			Note nt = key_analysis.get(i);
			if (n.get_start() - nt.get_start() > key_track*1000000) tmp.add(nt);
			else break;
		}
		for (Note nt : tmp) key_analysis.remove(nt);
	}
	/*
	 * Releases a note from the hold buffer. If the damper is not down, the note is undamped
	 * as well. Otherwise, it remains in the note buffer.
	 */
	public synchronized void release_note(byte id, boolean damped, long time) {
		Note tmp = null;
		for (Note n : hold_buffer) {
			if (n.id() == id) tmp = n;
		}
		if (tmp != null) {
			hold_buffer.remove(tmp);
			tmp.release(time, damped);
		}
		if (parent != null) parent.note_released(id, time);
		recalculate();
	}
	
	/*
	 * Undamps all notes in the note buffer that aren't held at the time it is called.
	 * Should be called either when the damper is released or the note is released
	 */
	public synchronized void undamp(long time) {
		this.damped = false;
		ArrayList<Note> tmp = new ArrayList<Note>();
		for (Note n : note_buffer) if (!hold_buffer.contains(n)) tmp.add(n);
		for (Note n : tmp) n.undamp(time); tmp.clear();
		for (Note n : rel_buffer) if (!hold_buffer.contains(n)) tmp.add(n);
		for (Note n : tmp) rel_buffer.remove(n); tmp.clear();
		for (Note n : bass) if (!hold_buffer.contains(n)) tmp.add(n);
		for (Note n : tmp) bass.remove(n); tmp.clear();
		for (Note n : marks) note_buffer.remove(n);
		for (Note n : all_buffer) if (!hold_buffer.contains(n)) tmp.add(n);
		for (Note n : tmp) all_buffer.remove(n); tmp.clear();
		if (parent != null) parent.damp_released(time);
		byte min_id = 127;
		Note new_dom = null;
		for (Note n : hold_buffer) {
			if (n.id() < min_id) {
				min_id = n.id();
				new_dom = n;
			}
		}
		if (new_dom != null && Math.abs(dominant - new_dom.id()) < 12) change_dom(new_dom);
		recalculate();
	}
	
	public synchronized void damp(long time) {
		this.damped = true;
		byte min_id = 127;
		Note new_dom = null;
		for (Note n : hold_buffer) {
			if (n.id() < min_id) {
				min_id = n.id();
				new_dom = n;
			}
		}
		if (new_dom != null) change_dom(new_dom);
		if (parent != null) parent.damp_pressed(time);
		recalculate();
	}
	
	public byte dom() { return this.dominant; }
	
	public synchronized boolean is_held(int val) {
		for (Note n : hold_buffer) if (n.value() == val) return true;
		return false;
	}
	
	public synchronized boolean is_rel(int val) {
		for (Note n : rel_buffer) if (n.value() == val) return true;
		return false;
	}
	
	public synchronized boolean in_buf(int val) {
		for (Note n : note_buffer) if (n.value() == val) return true;
		return false;
	}
	
	/*
	 * Removes a note from the note buffer, and sets it to null before garbage collection.
	 * Should be called by the note itself when its decay timer runs out
	 */
	public synchronized void decay_note(Note n) {
		note_buffer.remove(n);
	}
	
	public synchronized void destroy_note(Note n) {
		n.destroy();
		n = null;
	}
	
	public synchronized void mark_note(Note n) {
		marks.add(n);
	}
	
	/*
	 * Returns the note buffer
	 */
	public ArrayList<Note> get_buffer() {
		return note_buffer;
	}
	
	/*
	 *  Debug methods below, for printing information about buffers
	 */
	public synchronized void print_holds() {
		System.out.print("CURRENTLY HELD: ");
		for (Note n : hold_buffer) System.out.print(Music.getNoteName(n) + ", ");
		System.out.println();
	}
	
	public synchronized void print_buffer() {
		System.out.print("CURRENTLY IN BUFFER: ");
		for (Note n : note_buffer)	System.out.print(Music.getNoteName(n) + ", ");
		System.out.println();
	}
	
	public synchronized void print_history() {
		System.out.print("HISTORY: ");
		for (Note n : history) System.out.print(Music.getNoteName(n) + ", ");
		System.out.println();
	}
}