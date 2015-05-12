import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class Harmonizer {
	private Receiver midiReceiver;
	private static int stdVelocity = 127;
	public HashMap<Integer, Melody> melodies;
	
	public Harmonizer (String filename) {
		ArrayList<String> melodies = new ArrayList<String>();
 		try {
			Scanner read = new Scanner(new File(filename));
			while (read.hasNextLine()) { 
				String tmp = read.nextLine();
				if (tmp.charAt(0) != '%') melodies.add(tmp);	
			}
			parse_melodies(melodies);
			read.close();
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find synthesis file at: " + filename);
		}
	}
	
	private void parse_melodies(ArrayList<String> melodies) {
		for (String s : melodies) {
			Melody tmp = new Melody(s);
			this.melodies.put(tmp.chord_index, tmp);
		}
	}
	
	public void play_note(int note, int velocity) {
		try{
			ShortMessage myMsg = new ShortMessage();
			myMsg.setMessage(ShortMessage.NOTE_ON, 0, note, velocity);
			long timeStamp = System.nanoTime()/1000;
			midiReceiver.send(myMsg, timeStamp);
		} catch (InvalidMidiDataException e) {
			System.err.println("Invalid MIDI Data Exception Thrown");
		}
	}
	
	public void stop_note(int note, int velocity) {
		try{
			ShortMessage myMsg = new ShortMessage();
			myMsg.setMessage(ShortMessage.NOTE_OFF, 0, note, velocity);
			long timeStamp = System.nanoTime()/1000;
			midiReceiver.send(myMsg, timeStamp);
		} catch (InvalidMidiDataException e) {
			System.err.println("Invalid MIDI Data Exception Thrown");
		}
	}
}