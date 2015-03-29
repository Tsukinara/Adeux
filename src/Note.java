public class Note implements Comparable<Note> {
	
	// value is the index of the note in a regular 88 key piano, where 1 is A0
	// key represents what the note is, where 0 is A, 1 is A#, etc. 
	// octave is the octave of the key in scientific pitch notation
	private int value, key, octave, velocity;
	private NoteBuffer parent;
	
	// frequency is the frequency of the note, assuming A4 is 440Hz
	private double frequency;
	private byte id;
	private boolean damped;
	
	/*
	 * Generates a new note object, given the MIDI byte which was received and a
	 * boolean representing whether or not the pedal is down when the note was entered
	 */
	public Note(byte id, int vel, boolean damped, NoteBuffer parent) {
		this.parent = parent;
		this.id = id;
		this.velocity = vel;
		this.damped = damped;
		this.value = (int)id - 20;
		this.key = (value - 1)%12;
		this.octave = (value + 8)/12;
		this.frequency = get_frequency(value);
	}
	
	/*
	 * Calculates the frequency of the note, assuming scientific pitch (A4 = 440Hz)
	 * This might be replaced with a lookup table if it becomes too slow
	 */
	private double get_frequency(int value) {
		return 55 * Math.pow(2.0, ((double)value - 13.0)/12);
	}
	
	/*
	 * Undamps the note, starting the timer for its decay. There is no damp method, since
	 * you cannot damp a note once it has been played. 
	 */
	public void undamp() {
		this.damped = false;
		parent.decay_note(this);
	}
		
	/*
	 * Getter methods for note variables: frequency, key, octave, and damped
	 */
	public byte id() { return this.id; }
	public int key() { return this.key; }
	public int vel() { return this.velocity; }
	public int octave() { return this.octave; }
	public double freq() { return this.frequency; }
	public boolean is_damped() { return this.damped; }
	public NoteBuffer get_parent() { return this.parent; }

	/*
	 * Compares two notes with each other, returning the difference between them in half steps
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Note n) {
		return this.id - n.id();
	}
	
}