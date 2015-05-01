public class Main {
	public static void main (String [] args) {
        String filename = "resources/music/menu_bgm.mp3";
        MPlayer mp3 = new MPlayer(filename);
        mp3.loop();
	}
}