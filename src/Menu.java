import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Menu {
	private final short dA = 5;
	private final int num_snowflakes = 100;
	private final short max_opt = 2;
	
	private Display parent;
	private Snow[] snow;
	private short curr_state, curr_opt, bar_height, alpha, delay;
	
	public Menu(Display parent) {
		this.parent = parent;
		init_values();
	}
	
	public void init_values() {
		this.curr_state = 0;
		this.curr_opt = 0;
		this.bar_height = 50;
		this.alpha = 255;
		this.delay = 500 / 20;
		this.snow = new Snow[num_snowflakes];
		for (int i = 0; i < num_snowflakes; i++)
			snow[i] = new Snow(
					Math.random()*sX(1920), Math.random()*sY(1080), 
					Math.random()*sX(5)+1, Math.random()*sY(5)+sY(3), 
					Math.random()*sX(8)+sX(4), parent);
	}
	
	public void render(Graphics2D g) {
		draw_primary(g);
		switch (curr_state) {
			case 0: transition_in(g); break;
			case 1:
			case 2:
		}
	}
		
	private void draw_primary(Graphics2D g) {
		g.drawImage(parent.get_images().get("MENU_BG"), 0, 0, sX(1920), sY(1080), null);
		
		g.setColor(Color.WHITE);
		for (Snow s : snow) g.fillOval(s.x, s.y, s.dia, s.dia);
		
		g.setColor(parent.bg_color);
		g.drawImage(parent.get_images().get("LOGO_BK"), sX(571), sY(200), sX(777), sY(258), null);
		g.fillRect(0, 0, sX(1920), sY(this.bar_height));
		g.fillRect(0, sY(1080-bar_height+1), sX(1920), sY(bar_height));	
	}
	
	private void transition_in(Graphics2D g) {
		g.setColor(new Color(255, 255, 255, alpha));
		g.fillRect(0, 0, sX(1920), sY(1080));
		
		g.drawImage(parent.get_images().get("LOGO_BK"), sX(571), sY(200), sX(777), sY(258), null);
		
		g.setColor(parent.bg_color);
		g.fillRect(0, 0, sX(1920), sY(this.bar_height));
		g.fillRect(0, sY(1080-bar_height+1), sX(1920), sY(this.bar_height));
	}
	
	public void handle(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE: System.exit(0); break;
			case KeyEvent.VK_R: init_values(); break;
			case KeyEvent.VK_UP: case KeyEvent.VK_LEFT:
				if (curr_state == 1) curr_opt = (short)(curr_opt - 1 < 0 ? 0 : curr_opt - 1);
				break;
			case KeyEvent.VK_DOWN: case KeyEvent.VK_RIGHT:
				if (curr_state == 1) curr_opt = (short)(curr_opt + 1 > max_opt ? max_opt : curr_opt + 1);
				break;			
		}
	}
	
	public void step() {
		for (Snow s : snow) s.step();
		switch (curr_state) {
		case 0: // transition in
			if (delay <= 0) alpha = (short)(alpha-(dA/2) < 0 ? 0 : alpha-(dA/2));
			else delay--;
			
			if (alpha <= 0)	curr_state++;
			break;
		case 1: // idle
		case 2: // profile select
		case 3: // transition out	
		}
	}
	
	private int sX (int x) { return parent.scaleX(x); }
	private int sY (int y) { return parent.scaleY(y); }
//	private int[] sX (int[] x) { return parent.scaleX(x); }
//	private int[] sY (int[] y) { return parent.scaleY(y); }
}