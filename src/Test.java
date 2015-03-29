
public class Test {
	public static void main(String [] args) {
		NoteBuffer nb = new NoteBuffer();
		MidiHandler mh = new MidiHandler(nb);
		mh.imNotUseless();
	}
}
