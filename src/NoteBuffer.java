import java.util.ArrayList;

public class NoteBuffer {
	// contains any notes whose frequencies are dominant
	private ArrayList<Note> note_buffer;
	
	// contains the notes that are held at the time
	private ArrayList<Note> hold_buffer;
	
	/*
	 * Initializes the note buffer, as well as the hold buffer
	 */
	public NoteBuffer() {
		this.note_buffer = new ArrayList<Note>();
		this.hold_buffer = new ArrayList<Note>();
	}
	
	/*
	 * Adds a note to the hold buffer as well as the note buffer. Damped should
	 * always be true when this method is called. If the same note is already in the 
	 * note buffer, the most recent one is kept
	 */
	public synchronized void add_note(byte id, boolean damped, int vel) {
		Note n = new Note(id, vel, damped, this);
		Note tmp = null;
		for (Note nt : note_buffer) {
			if (nt.id() == id) tmp = nt;
		}
		note_buffer.remove(tmp);
		note_buffer.add(n);
		hold_buffer.add(n);
	}
	
	/*
	 * Releases a note from the hold buffer. If the damper is not down, the note is undamped
	 * as well. Otherwise, it remains in the note buffer.
	 */
	public synchronized void release_note(byte id, boolean damped) {
		Note tmp = null;
		for (Note n : hold_buffer) {
			if (n.id() == id) tmp = n;
		}
		hold_buffer.remove(tmp);
		if (!damped) tmp.undamp();
	}
	
	/*
	 * Undamps all notes in the note buffer that aren't held at the time it is called.
	 * Should be called either when the damper is released or the note is released
	 */
	public void undamp() {
		ArrayList<Note> tmp = new ArrayList<Note>();
		for (Note n : note_buffer) {
			if (!hold_buffer.contains(n)) tmp.add(n);
		}
		for (Note n : tmp) n.undamp();
	}
	
	/*
	 * Removes a note from the note buffer, and sets it to null before garbage collection.
	 * Should be called by the note itself when its decay timer runs out
	 */
	public void decay_note(Note n) {
		note_buffer.remove(n);
		n = null;
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
	public void print_holds() {
		System.out.print("CURRENTLY HELD: ");
		for (Note n : hold_buffer) {
			System.out.print(Music.getNoteName(n) + ", ");
		}
		System.out.println();
	}
	
	public void print_buffer() {
		System.out.print("CURRENTLY IN BUFFER: ");
		for (Note n : note_buffer) {
			System.out.print(Music.getNoteName(n) + ", ");
		}
		System.out.println();
	}
}