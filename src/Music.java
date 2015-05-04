public class Music {
	public static final String s = "#";	//"\u266f";
	public static final String f = "b";	//"\u266d";
	public static final String n = "n";	//"\u266e";
	
	public static String getNoteName(Note nt) {
		int key = nt.key();
		int oct = nt.octave();
		String name = "";
		switch(key) {
			case 0: 	name = "A"; 	break;
			case 1: 	name = "A" + s; break;
			case 2: 	name = "B"; 	break;
			case 3: 	name = "C"; 	break;
			case 4: 	name = "C" + s; break;
			case 5: 	name = "D"; 	break;
			case 6: 	name = "D" + s; break;
			case 7: 	name = "E"; 	break;
			case 8: 	name = "F"; 	break;
			case 9: 	name = "F" + s; break;
			case 10: 	name = "G"; 	break;
			case 11: 	name = "G" + s; break;
		}
		return name + oct;
	}
	
	public static String getNoteName(byte id) {
		int value = (int)id - 20;
		int key = (value - 1)%12;
		int oct = (value + 8)/12;
		String name = "";
		switch(key) {
			case 0: 	name = "A"; 	break;
			case 1: 	name = "A" + s; break;
			case 2: 	name = "B"; 	break;
			case 3: 	name = "C"; 	break;
			case 4: 	name = "C" + s; break;
			case 5: 	name = "D"; 	break;
			case 6: 	name = "D" + s; break;
			case 7: 	name = "E"; 	break;
			case 8: 	name = "F"; 	break;
			case 9: 	name = "F" + s; break;
			case 10: 	name = "G"; 	break;
			case 11: 	name = "G" + s; break;
		}
		return name + oct;
	}
	
	public static String getRoot(int key) {
		String name = "";
		switch(key) {
			case 0: 	name = "A"; 	break;
			case 1: 	name = "A" + s; break;
			case 2: 	name = "B"; 	break;
			case 3: 	name = "C"; 	break;
			case 4: 	name = "C" + s; break;
			case 5: 	name = "D"; 	break;
			case 6: 	name = "D" + s; break;
			case 7: 	name = "E"; 	break;
			case 8: 	name = "F"; 	break;
			case 9: 	name = "F" + s; break;
			case 10: 	name = "G"; 	break;
			case 11: 	name = "G" + s; break;
		}
		return name;
	}
}