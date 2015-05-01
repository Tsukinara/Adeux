import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class SettingsMenu {
	private static final short dA = 5;
	private final int origX = 960, origY = 701;
	private final int finXL = 760, finYB = 849, finYT = 553;
	private final int setXL = 440, setYB = 924, setYT = 156;
	private final int setTT = 213, sel_h = 70;
	private final int box_y = 571, box_w = 404, box_h = 253;
	private final int l_sp = 84, r_al = 696;
	private final int v_h = 34, v_w = 360, opt_h = 64;
	private final Color main = new Color(26, 47, 72);
	private final Color side = new Color(152, 182, 201);
	private final Color shim = new Color(106, 143, 165);
	private final Color volb = Color.WHITE;
	private final String[] tits = {"display", "bgm vol", "accomp. vol", "tempo", "key", "meter"};
	private Display.State next_state = Display.State.MENU;
	
	private Display parent;
	private short curr_state, curr_opt, bar_height;
	private int sel_y;
	private Font pl_base, opt_base;
	
	public SettingsMenu(Display parent) {
		this.parent = parent;
		init_values();
	}
	
	private void init_values() {
		this.curr_state = 0; this.curr_opt = 0;
		this.sel_y = get_opt_y();
		this.bar_height = 50;
		this.pl_base = new Font("Plantin MT Std", Font.PLAIN, sH(48));
		this.opt_base = new Font("Plantin MT Std", Font.PLAIN, sH(38));
	}
	
	public void render(Graphics2D g) {
		draw_primary(g);
		switch (curr_state) {
			case 0: transition_in(g); break;
			case 1: set_select(g); break;
			case 2: transition_out(g); break;
		}
	}
	
	private void draw_primary (Graphics2D g) {
		g.drawImage(parent.get_images().get("MENU_BG"), sX(0), sY(0), sW(1920), sH(1080), null);
		g.setColor(Color.WHITE);
		for (Snow s : parent.snow) g.fillOval(sX(s.x), sY(s.y), sW(s.dia), sH(s.dia));
		
		g.setColor(parent.bg_color);
		g.fillRect(sX(0), sY(0), sW(1920), sH(this.bar_height));
		g.fillRect(sX(0), sY(1080-bar_height+1), sW(1920), sH(bar_height));	
	}
	
	private void draw_menu (Graphics2D g) {
		g.setComposite(AlphaComposite.SrcOver.derive(1f));

		int menu_h = (setYB - setYT) - (finYB - finYT - box_h);
		int menu_y = setYT + (setYB - setYT - menu_h)/2;
			
		g.setColor(new Color(222, 238, 246, 200));
		g.fillRect(sX(setXL), sY(menu_y), sW(2*(origX-setXL)), sH(menu_h));
		
		g.setColor(Color.WHITE);
		g.fillRect(sX(setXL), sY(setTT), sW(2*(origX-setXL)), sH(sel_h));
		
		g.setColor(new Color(152, 189, 209, 255));
		g.fillRect(sX(setXL), sY(sel_y), sW(1920-(2*setXL)), sH(opt_h));
		
		g.setColor(parent.bg_color);
		g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine(sX(setXL), sY(setYB), sX(setXL), sY(setYT));
		g.drawLine(sX(1920-setXL), sY(setYB), sX(1920-setXL), sY(setYT));
		
		g.setFont(pl_base);
		int fw = g.getFontMetrics().stringWidth("settings");
		g.drawString("settings", (sX(1920)-fw)/2, sY(setTT + 50));
	}
	
	private void transition_in(Graphics2D g) {
		draw_menu(g);
		for (int i = 0; i < tits.length; i++) draw_line(g, i, 255);
		g.setFont(pl_base);
		g.setColor(main);
		int fw = g.getFontMetrics().stringWidth("save settings");
		g.drawString("save settings", (sX(1920)-fw)/2, sY(858));
	}
	
	private int get_opt_y() {
		if (curr_opt < 6) return l_sp*curr_opt + 311;
		else return 815;
	}
	
	private int get_opt_y(int num) {
		if (num < 6) return l_sp*num + 311;
		else return 815;
	}
	
	private void draw_line(Graphics2D g, int num, int alpha) {
		Settings s = parent.set;
		boolean curr = num == curr_opt;
		int y = l_sp*num + 356;
		g.setFont(opt_base);
		FontMetrics fm = g.getFontMetrics();
		int fw = fm.stringWidth(tits[num]);
		g.setColor(new Color(26, 26, 26, alpha));
		g.drawString(tits[num], sX(r_al) - fw, sY(y));
		switch (num) {
			case 0: 
				g.setColor(s.window_size < 0? main: (curr ? shim : side));
				g.drawString("fullscreen", sX(782), sY(y));
				g.setColor(s.window_size == 1600? main: (curr ? shim : side));
				g.drawString("1600x900", sX(999), sY(y));
				g.setColor(s.window_size == 1280? main: (curr ? shim : side));
				g.drawString("1280x720", sX(1207), sY(y));
				break;
			case 1:
				g.setColor(s.bgm_vol == 0? main: (curr ? shim : side));
				g.drawString("off", sX(782), sY(y));
				g.setColor(curr ? shim : side);
				g.fillRect(sX(884), sY(get_opt_y(1) + (opt_h - v_h)/2), sW((int)((double)s.bgm_vol/100.0 * v_w)), sH(v_h));
				g.setColor(s.bgm_vol != 0? main: (curr ? shim : side));
				g.drawString(s.bgm_vol + "%", sX(1278), sY(y));
				break;
			case 2:
				g.setColor(s.harm_vol == 0? main: (curr ? shim : side));
				g.drawString("off", sX(782), sY(y));
				g.setColor(curr ? shim : side);
				g.fillRect(sX(884), sY(get_opt_y(2) + (opt_h - v_h)/2), sW((int)((double)s.harm_vol/100.0 * v_w)), sH(v_h));
				g.setColor(s.harm_vol != 0? main: (curr ? shim : side));
				g.drawString(s.harm_vol + "%", sX(1278), sY(y));
				break;
			case 3:
				g.setColor(s.tempo == -1? main: (curr ? shim : side));
				g.drawString("auto", sX(782), sY(y));
				g.setColor(curr ? shim : side);
				int temp_o;
				if (s.tempo == -1) temp_o = 40; else temp_o = s.tempo; 
				g.fillRect(sX(884), sY(get_opt_y(3) + (opt_h - v_h)/2), sW((int)((double)(temp_o - 40)/200.0 * v_w)), sH(v_h));
				g.setColor(s.tempo > 39? main: (curr ? shim : side));
				g.drawString(temp_o + "bpm", sX(1278), sY(y));
				break;
			case 4:
				g.setColor(s.ksig == null? main: (curr ? shim : side));
				g.drawString("auto", sX(782), sY(y));
				g.setColor(s.ksig != null? main: (curr ? shim : side));
				if (s.ksig != null) {
					if (s.ksig.get_type().equals("")) 
						g.drawString(s.ksig.get_base() + " " + s.ksig.get_maj_min(), sX(928), sY(y));
					else {
						g.drawString(s.ksig.get_base(), sX(928), sY(y));
						int tmp = 928 + fm.stringWidth(s.ksig.get_base()) + sW(14);
						g.setFont(new Font("Opus Text Std", Font.PLAIN, sH(40)));
						g.drawString(s.ksig.get_type(), sX(tmp), sY(y));
						tmp += g.getFontMetrics().stringWidth(s.ksig.get_type()) + sW(10);
						g.setFont(opt_base);
						g.drawString(" " + s.ksig.get_maj_min(), sX(tmp), sY(y));
					}
				}
				if (curr) {
					g.setColor(shim);
					g.setFont(new Font("Plantin MT Std", Font.PLAIN, sH(28)));
					g.drawString("(play any chord to set)", sX(1082), sY(y));
				}
				break;
			case 5:
				g.setColor(s.tsig == null? main: (curr ? shim : side));
				g.drawString("auto", sX(782), sY(y));
				g.setColor(s.tsig != null? main: (curr ? shim : side));
				if (s.tsig != null) g.drawString(s.tsig.getTS(), sX(928), sY(y));
				else g.drawString("simple duple", sX(928), sY(y));
				break;
		}
	}

	private void set_select(Graphics2D g) {
		
	}
	
	private void transition_out(Graphics2D g) {
		
	}
	
	public void handle(KeyEvent e) {
		if (curr_state < 2) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE: case KeyEvent.VK_X: curr_opt = 6; break;
				case KeyEvent.VK_UP: curr_opt = (short)(curr_opt - 1 < 0 ? 0 : curr_opt - 1); break;
				case KeyEvent.VK_DOWN: curr_opt = (short)(curr_opt + 1 > 6? 6: curr_opt + 1); break;
				case KeyEvent.VK_ENTER: case KeyEvent.VK_Z:
					if (curr_opt == 6) { 
						curr_state = 2; 
						next_state = Display.State.MENU;
						parent.set.write_settings(Display.settings);
					}
					break;
				case KeyEvent.VK_LEFT:
					switch (curr_opt) {
						case 0: 
							if (parent.set.window_size == 1280) parent.set.window_size = 1600;
							else parent.set.window_size = -1; break;
						case 1:
							parent.set.bgm_vol = (short)(parent.set.bgm_vol - 5 < 0? 0: parent.set.bgm_vol - 5);
							break;
						case 2:
							parent.set.harm_vol = (short)(parent.set.harm_vol - 5 < 0? 0: parent.set.harm_vol - 5);
							break;
						case 3:
							if (parent.set.tempo <= 40) parent.set.tempo = -1;
							else parent.set.tempo = (short)(parent.set.tempo - 5 < 40? 40: parent.set.tempo - 5);
							break;
						case 4:
							if (parent.set.ksig != null) parent.set.ksig = null;
							break;
						case 5:
							if (parent.set.tsig != null) {
								if (parent.set.tsig.type == TimeSignature.Type.SIMPLE_DUPLE) parent.set.tsig = null;
								else parent.set.tsig.decrement();
							}
							break;
					}
					break;
				case KeyEvent.VK_RIGHT:
					switch (curr_opt) {
						case 0: 
							if (parent.set.window_size == -1) parent.set.window_size = 1600;
							else parent.set.window_size = 1280; break;
						case 1:
							parent.set.bgm_vol = (short)(parent.set.bgm_vol + 5 > 100? 100: parent.set.bgm_vol + 5);
							break;
						case 2:
							parent.set.harm_vol = (short)(parent.set.harm_vol + 5 > 100? 100: parent.set.harm_vol + 5);
							break;
						case 3:
							if (parent.set.tempo == -1) parent.set.tempo = 45;
							else parent.set.tempo = (short)(parent.set.tempo + 5 > 240? 240: parent.set.tempo + 5);
							break;
						case 4:
							if (parent.set.ksig == null) parent.set.ksig = new KeySignature("C", true);
							break;
						case 5:
							if (parent.set.tsig == null) parent.set.tsig = new TimeSignature((short)2, (short)4);
							else if (parent.set.tsig.type != TimeSignature.Type.COMPOUND_QUADRUPLE) parent.set.tsig.increment();
							break;					
				}
			}
		}
	}
	
	public void step() {
		for (Snow s : parent.snow) s.step();
		if (sel_y < get_opt_y()) {
			int dS = sH(get_opt_y() - sel_y)/sH(5);
			sel_y += (dS > sH(4) ? dS : sH(4));
			if (sel_y > get_opt_y()) sel_y = get_opt_y();
		} else if (sel_y > get_opt_y()) {
			int dS = sH(get_opt_y() - sel_y)/sH(5);
			sel_y += (dS < sH(-4) ? dS : sH(-4));
			if (sel_y < get_opt_y()) sel_y = get_opt_y();
		}
		switch (curr_state) {
		case 0: // transition in
			break;
		case 1: // idle
			break;
		case 2:
			parent.set_state(next_state);
		}
	}
	
	private int sX (int x) { return parent.scaleX(x); }
	private int sY (int y) { return parent.scaleY(y); }
	private int sW (int w) { return parent.scaleW(w); }
	private int sH (int h) { return parent.scaleH(h); }
}