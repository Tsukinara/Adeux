import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class AppCore {
	private Display parent;
	private NoteBuffer nb;
	
	public AppCore(Display parent) {
		this.parent = parent;
	}
	
	public void init_values() {
		
	}
	
	public void render(Graphics2D g) {
		
	}
	
	private void draw_analysis(Graphics2D g) {
		
	}
	
	private void draw_piano(Graphics2D g) {
		
	}
	
	public void handle(KeyEvent e) {
		
	}
	
	public void step() {
		
	}
	
	public void note_pressed(byte id, byte vel, long timestamp) {

	}
	
	public void re_init() { init_values(); }
	public void set_buffer(NoteBuffer nb) { this.nb = nb; }
	
	public void note_released(byte id, long timestamp) {}
	public void damp_pressed(long timestamp) {}
	public void damp_released(long timestamp) {}

	private int sX (int x) { return parent.scaleX(x); }
	private int sY (int y) { return parent.scaleY(y); }
	private int sW (int w) { return parent.scaleW(w); }
	private int sH (int h) { return parent.scaleH(h); }
}