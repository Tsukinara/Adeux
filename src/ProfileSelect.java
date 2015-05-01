import java.awt.Graphics2D;

public class ProfileSelect {
	
	private Display parent;
	private short curr_state;
	
	public ProfileSelect(Display parent) {
		this.parent = parent;
		init_values();
	}
	
	private void init_values() {
		curr_state = 0;
	}
	
	public void render(Graphics2D g) {
		
	}
	
	public void step() {
		for (Snow s : parent.snow) s.step();
		switch (curr_state) {
		case 0: // transition in
		case 1: // idle
		case 2: // transition back
		case 3: // transition forwards
		}
	}
}