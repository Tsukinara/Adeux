import java.util.ArrayList;

public class NoteBuffer {
	private static final int max_notes = 100;
	// contains any notes whose frequencies are dominant
	public ArrayList<Note> note_buffer;
	
	// contains the notes that are held at the time
	public ArrayList<Note> hold_buffer;
	
	// contains any notes that are relevant
	public ArrayList<Note> rel_buffer;
	public ArrayList<Note> history;
	
	//contains all notes within 1.5 octaves of the dominant overtone
	private ArrayList<Note> bass;
	
	private ArrayList<Note> marks;
	
	private byte dominant;
	private Display parent;
	
	public boolean damped;
	
	/*
	 * Initializes the note buffer, as well as the hold buffer
	 */
	public NoteBuffer(Display parent) {
		this.parent = parent;
		this.note_buffer = new ArrayList<Note>();
		this.hold_buffer = new ArrayList<Note>();
		this.rel_buffer = new ArrayList<Note>();
		this.history = new ArrayList<Note>();
		this.marks = new ArrayList<Note>();
		this.damped = false;
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
		if (id - this.dominant < 20) {
			bass.add(n);
		}
		note_buffer.remove(tmp);
		note_buffer.add(n);
		hold_buffer.add(n);
		add_history(n);
		parent.note_pressed(id, vel, time);
	}
	
	public synchronized void change_dom(Note n) {
		if (this.dominant != n.id()) {
			this.dominant = n.id();
			this.bass.clear();
			this.bass.add(n);
		}
	}
	
	private synchronized void add_history(Note n) {
		history.add(n);
		if (history.size() > max_notes) {
			Note tmp = history.remove(0);
			destroy_note(tmp);
		}
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
		parent.note_released(id, time);
	}
	
	/*
	 * Undamps all notes in the note buffer that aren't held at the time it is called.
	 * Should be called either when the damper is released or the note is released
	 */
	public synchronized void undamp(long time) {
		this.damped = false;
		ArrayList<Note> tmp = new ArrayList<Note>();
		for (Note n : note_buffer) {
			if (!hold_buffer.contains(n)) tmp.add(n);
		}
		for (Note n : tmp) n.undamp(time);
		for (Note n : marks) {
			note_buffer.remove(n);
		}
		parent.damp_released(time);
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
		parent.damp_pressed(time);
	}
	
	public byte dom() { return this.dominant; }
	
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
		for (Note n : hold_buffer) {
			System.out.print(Music.getNoteName(n) + ", ");
		}
		System.out.println();
	}
	
	public synchronized void print_buffer() {
		System.out.print("CURRENTLY IN BUFFER: ");
		for (Note n : note_buffer) {
			System.out.print(Music.getNoteName(n) + ", ");
		}
		System.out.println();
	}
	
	public synchronized void print_history() {
		System.out.print("HISTORY: ");
		for (Note n : history) {
			System.out.print(Music.getNoteName(n) + ", ");
		}
		System.out.println();
	}
}