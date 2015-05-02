import java.util.ArrayList;

public class NoteBuffer {
	// contains any notes whose frequencies are dominant
	public ArrayList<Note> note_buffer;
	
	// contains the notes that are held at the time
	public ArrayList<Note> hold_buffer;
	
	// contains any notes that are relevant
	public ArrayList<Note> rel_buffer;
	
	//contains all notes within 1.5 octaves of the dominant overtone
	private ArrayList<Note> bass;
	
	private ArrayList<Note> marks;
	
	private byte dominant;
	private Display parent;
	
	/*
	 * Initializes the note buffer, as well as the hold buffer
	 */
	public NoteBuffer(Display parent) {
		this.parent = parent;
		this.note_buffer = new ArrayList<Note>();
		this.hold_buffer = new ArrayList<Note>();
		this.rel_buffer = new ArrayList<Note>();
		this.marks = new ArrayList<Note>();
	}
	
	/*
	 * Adds a note to the hold buffer as well as the note buffer. Damped should
	 * always be true when this method is called. If the same note is already in the 
	 * note buffer, the most recent one is kept
	 */
	public synchronized void add_note(byte id, boolean damped, int vel, long time) {
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
	}
	
	public synchronized void change_dom(Note n) {
		if (this.dominant != n.id()) {
			this.dominant = n.id();
			this.bass.clear();
			this.bass.add(n);
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
		hold_buffer.remove(tmp);
		tmp.release(time, damped);
	}
	
	/*
	 * Undamps all notes in the note buffer that aren't held at the time it is called.
	 * Should be called either when the damper is released or the note is released
	 */
	public synchronized void undamp(long time) {
		ArrayList<Note> tmp = new ArrayList<Note>();
		for (Note n : note_buffer) {
			if (!hold_buffer.contains(n)) tmp.add(n);
		}
		for (Note n : tmp) n.undamp(time);
		for (Note n : marks) {
			note_buffer.remove(n);
			destroy_note(n);
		}
	}
	
	public synchronized void damp() {
		byte min_id = 127;
		Note new_dom = null;
		for (Note n : hold_buffer) {
			if (n.id() < min_id) {
				min_id = n.id();
				new_dom = n;
			}
		}
		if (new_dom != null) change_dom(new_dom);
	}
	
	public byte dom() { return this.dominant; }
	
	/*
	 * Removes a note from the note buffer, and sets it to null before garbage collection.
	 * Should be called by the note itself when its decay timer runs out
	 */
	public synchronized void decay_note(Note n) {
		note_buffer.remove(n);
		destroy_note(n);
	}
	
	public void destroy_note(Note n) {
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
}