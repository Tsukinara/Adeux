public class KeySignature {
	public char key;
	public char type;
	public boolean major;
	public byte[] signature;
	
	public KeySignature(String name, boolean major) {
		this.major = major;
		if (major) name = name.toUpperCase();
		else name = name.toLowerCase();
		this.key = name.charAt(0);
		if (name.length() > 1)
			switch (name.charAt(1)) {
				case 'b': this.type = Music.f.charAt(0); break;
				case '#': this.type = Music.s.charAt(0); break;
				default: this.type = 'n';
			}
		else this.type = 'n';
	}
	
	public static String get_key_name(Note n) {
		return "";
	}
	
	public String toString() {
		String s = key + "" + (type != 'n' ? type : "") + (major ? " maj" : " min");
		return s;
	}
}