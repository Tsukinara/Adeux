import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class Harmonizer {
	private final static int treble_base = 57;
	private final static int bass_base = 31;
	
	private Receiver midiReceiver;
	private static final int std_vel = 127;
	private Melody curr_melody;
	private Melody bass_melody;
	private ArrayList<Integer> curr_held;
	
	public HashMap<Integer, ArrayList<Melody>> melodies;
	
	public Harmonizer (String filename) {
		this.curr_held = new ArrayList<Integer>();
		this.melodies = new HashMap<Integer, ArrayList<Melody>>();
		ArrayList<String> melodies = new ArrayList<String>();
 		try {
			Scanner read = new Scanner(new File(filename));
			while (read.hasNextLine()) { 
				String tmp = read.nextLine();
				if (tmp.length() > 0 && tmp.charAt(0) != '%') melodies.add(tmp);	
			}
			midiReceiver = MidiSystem.getReceiver();
			parse_melodies(melodies);
			read.close();
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find synthesis file at: " + filename);
		} catch (MidiUnavailableException e) {
			System.err.println("Midi Unavailable Exception Thrown");
		}
	}
	
	private void parse_melodies(ArrayList<String> mel) {
		for (String s : mel) {
			Melody tmp = new Melody(s);
			if (!melodies.containsKey(tmp.chord_index)) melodies.put(tmp.chord_index, new ArrayList<Melody>());
			this.melodies.get(tmp.chord_index).add(tmp);
		}
	}	
	
	public void match_melody_to(Chord c, int[]nexts) {
		int root_ind = c.equvalent_base();
		if (melodies.containsKey(root_ind)) {
			ArrayList<Melody> t_cs = melodies.get(root_ind);
			ArrayList<Melody> b_cs = new ArrayList<Melody>();
			if (t_cs.size() == 0) { System.err.println("Unable to find harmony for: " + c.toString()); return; }
			for (int i = 0; i < t_cs.size(); i++) 
				if (t_cs.get(i).type == Melody.Type.BASS) b_cs.add(t_cs.remove(i--));
			
			this.bass_melody = b_cs.get((int)(Math.random() * b_cs.size()));
			ArrayList<Melody> finals = new ArrayList<Melody>();
			for (int i : nexts) {
				for (Melody m : t_cs) if (m.next_index == i) finals.add(m);
			}
			if (finals.size() == 0) {
				System.err.println("Unable to find perfect harmony. Using arbitrary harmony.");
				int index = (int)(Math.random() * t_cs.size());
				this.curr_melody = t_cs.get(index);
			} else {
				int index = (int)(Math.random() * finals.size());
				this.curr_melody = finals.get(index);
			}
		}
		else System.err.println("Unable to find harmony for: " + c.toString());
		this.curr_melody = melodies.get(0).get(0);
	}
	
	public void play_melody(double time, int kkey) {
		int[] bnotes = bass_melody.get_held_notes(time);
		int[] notes = curr_melody.get_held_notes(time);
		for (int i : notes)
			if (!curr_held.contains(i)) {
				play_note(i + treble_base + kkey, std_vel);
				curr_held.add(i);
			}
		for (int i : bnotes)
			if (!curr_held.contains(i)) {
				play_note(i + bass_base + kkey, std_vel);
				curr_held.add(i);
			}
		for (int i = 0; i < curr_held.size(); i++) {
			if (!has(notes, (int)curr_held.get(i))) {
				stop_note(curr_held.get(i) + treble_base + kkey, 0);
				curr_held.remove(i);
			}
			if (!has(bnotes, (int)curr_held.get(i))) {
				stop_note(curr_held.get(i) + bass_base + kkey, 0);
				curr_held.remove(i);
			}
		}
		
	}
	
	private static boolean has(int[] in, int k) { boolean f = false; for (int i : in) if (i == k) f = true; return f; }
	
	public void play_note(int note, int velocity) {
		System.out.println("PLAYING NOTE: " + note);
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
		System.out.println("RELEASING NOTE: " + note);
		try{
			ShortMessage myMsg = new ShortMessage();
			myMsg.setMessage(ShortMessage.NOTE_OFF, 0, note, velocity);
			long timeStamp = System.nanoTime()/1000;
			midiReceiver.send(myMsg, timeStamp);
		} catch (InvalidMidiDataException e) {
			System.err.println("Invalid MIDI Data Exception Thrown");
		}
	}
	
	public boolean is_held(int val, int kkey) {
		int base;
		if (curr_melody.type == Melody.Type.TREBLE) base = treble_base;
		else base = bass_base;
		return curr_held.contains(val + 20 - kkey - base);
	}
	
	public static void main(String[] args) {
		Harmonizer h = new Harmonizer("resources\\synthesis.dat");
		System.out.println(h.melodies.toString());
		h.match_melody_to(null, new int[] { 2 });
		for (int i = 0; (double)i/Math.PI < 60; i++) {
			System.out.println(i);
			try {
				h.play_melody((double)i/Math.PI, Music.getKey("C"));
				Thread.sleep(10);
			} catch (Exception e) {e.printStackTrace();}
		}
	}
}