import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.BasicStroke;

public class Menu {
	private final short dA = 5, dL = 10;
	private final float dT = 0.1f, doA = 0.05f;
	private final int num_snowflakes = 100;
	private final short max_opt = 2;
	private final int origX = 960, origY = 701;
	private final int finXL = 760, finYB = 849;
	private final int box_y = 571, box_w = 404, box_h = 253;
	private final int sel_h = 70;
	private final String[] opts = {"start", "settings", "exit"};
	private final int[] opt_y = {641, 714, 787};
	
	private Display parent; private Display.State next_state;
	private Font pl_base;
	private Snow[] snow;
	private short curr_state, curr_opt, bar_height, alpha, alpha2, delay;
	private int bar_xc, bar_yc, txt_yc, sel_y;
	private float theta, theta2;
	private float[] opt_alpha, opt_da;
	private boolean flag_load;
	
	public Menu(Display parent) {
		this.parent = parent;
		init_values();
	}
	
	public void init_values() {
		this.curr_state = 0;
		this.curr_opt = 0;
		this.opt_alpha = new float [max_opt+1]; this.opt_da = new float [max_opt+1];
		this.bar_height = 50;
		this.alpha = 255; this.alpha2 = 0;
		this.delay = 500 / 20;
		this.sel_y = get_opt_y();
		this.snow = new Snow[num_snowflakes];
		for (int i = 0; i < num_snowflakes; i++)
			snow[i] = new Snow(
					Math.random()*sX(1920), Math.random()*sY(1080), 
					Math.random()*sX(5)+1, Math.random()*sH(5)+sH(3), 
					Math.random()*sX(8)+sX(4), parent);
		this.theta = (float)Math.PI; this.theta2 = (float)Math.PI;
		this.bar_xc = origX; this.bar_yc = origY; this.txt_yc = 590;
		this.pl_base = new Font("Plantin MT Std", Font.PLAIN, sH(48));
		this.flag_load = false;
	}
	
	public void render(Graphics2D g) {
		draw_primary(g);
		switch (curr_state) {
			case 0: transition_in(g); break;
			case 1: opt_select(g); break;
			case 2:
		}
	}
		
	private void draw_primary(Graphics2D g) {
		g.drawImage(parent.get_images().get("MENU_BG"), sX(0), sY(0), sX(1920), sH(1080), null);
		g.setColor(Color.WHITE);
		for (Snow s : snow) g.fillOval(sX(s.x), sY(s.y), sX(s.dia), sH(s.dia));
		
		g.setColor(parent.bg_color);
		g.drawImage(parent.get_images().get("LOGO_BK"), sX(571), sY(200), sX(777), sH(258), null);
		g.fillRect(sX(0), sY(0), sX(1920), sH(this.bar_height));
		g.fillRect(sX(0), sY(1080-bar_height+1), sX(1920), sH(bar_height));	
	}
	
	private void transition_in(Graphics2D g) {
		g.setColor(new Color(255, 255, 255, alpha));
		g.fillRect(sX(0), sY(0), sX(1920), sH(1080));
		
		g.drawImage(parent.get_images().get("LOGO_BK"), sX(571), sY(200), sX(777), sH(258), null);
		
		g.setColor(parent.bg_color);
		g.fillRect(sX(0), sY(0), sX(1920), sH(this.bar_height));
		g.fillRect(sX(0), sY(1080-bar_height+1), sX(1920), sH(this.bar_height));
		
		g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		if (bar_yc < finYB) bar_yc = origY + 1 + (int)((finYB-origY)/2 * (1+Math.cos(theta)));
		else bar_yc = finYB;
		if (bar_xc > finXL) bar_xc = origX - 1 - (int)((origX-finXL)/2 * (1+Math.cos(theta2)));
		else bar_xc = finXL;
		if (bar_yc == finYB) {
			g.setColor(new Color(222, 238, 246, 200));
			g.fillRect(sX(bar_xc), sY(box_y), sX(2*(origX-bar_xc)), sH(box_h));
		}
		
		if(txt_yc - sH(830) > 0) {
			int a = ((txt_yc-sH(830))/2 > 255 ? 255 : (txt_yc - sH(830))/2);
			g.setColor(new Color(255, 255, 255, a));
			if (a == 255) flag_load = true;
			g.fillRect(sX(finXL), sY(sel_y), sX(box_w), sH(sel_h));
		}
		
		g.setColor(new Color(26, 26, 26, alpha2));
		g.drawLine(sX(bar_xc), sY(bar_yc), sX(bar_xc), sY(origY-(bar_yc-origY)));
		g.drawLine(sX(1920-bar_xc), sY(bar_yc), sX(1920-bar_xc), sY(origY-(bar_yc-origY)));
		
		g.setFont(pl_base);
		for (int i = 0; i < opts.length; i++) {
			if (opt_y[i] < txt_yc) { opt_da[i] = doA/2; }
			g.setComposite(AlphaComposite.SrcOver.derive(opt_alpha[i]));
			int fw = g.getFontMetrics().stringWidth(opts[i]);
			g.drawString(opts[i], (sX(1920)-fw)/2, sY(opt_y[i]));
		}
	}
	
	private void opt_select(Graphics2D g) {
		g.setColor(new Color(222, 238, 246, 200));
		g.fillRect(sX(finXL), sY(box_y), sX(box_w), sH(box_h));
		
		g.setColor(Color.WHITE);
		g.fillRect(sX(finXL), sY(sel_y), sX(box_w), sH(sel_h));
		
		g.setColor(parent.bg_color);
		g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine(sX(finXL), sY(finYB), sX(finXL), sY(origY-(finYB-origY)));
		g.drawLine(sX(1920-finXL), sY(finYB), sX(1920-finXL), sY(origY-(finYB-origY)));
		
		g.setFont(pl_base);
		for (int i = 0; i < opts.length; i++) {
			if (opt_y[i] < txt_yc) { opt_da[i] = doA/2; }
			g.setComposite(AlphaComposite.SrcOver.derive(opt_alpha[i]));
			int fw = g.getFontMetrics().stringWidth(opts[i]);
			g.drawString(opts[i], (sX(1920)-fw)/2, sY(opt_y[i]));
		}
	}
	
	private int get_opt_y() { return 71*curr_opt + 593; }
	
	public void handle(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE: System.exit(0); break;
			case KeyEvent.VK_R: init_values(); break;
			case KeyEvent.VK_UP: case KeyEvent.VK_LEFT:
				if (curr_state < 2) curr_opt = (short)(curr_opt - 1 < 0 ? 0 : curr_opt - 1);
				break;
			case KeyEvent.VK_DOWN: case KeyEvent.VK_RIGHT:
				if (curr_state < 2) curr_opt = (short)(curr_opt + 1 > max_opt ? max_opt : curr_opt + 1);
				break;
			case KeyEvent.VK_ENTER: case KeyEvent.VK_Z:
				if (curr_state < 2) {
					switch (curr_opt) {
						case 0: curr_state++; break;
						case 1: next_state = Display.State.SETTINGS; curr_state = 4; break;
						case 2: System.exit(0);
					}
				}
		}
	}
	
	public void step() {
		for (Snow s : snow) s.step();
		switch (curr_state) {
		case 0: // transition in
			if (delay <= 0) alpha = (short)(alpha-dA < 0 ? 0 : alpha-dA);
			else delay--;
					
			if (alpha <= 90 && bar_yc != finYB) { 
				theta += dT;
				alpha2 = (short)(alpha2+dA > 255 ? 255 : alpha2+dA);
			} else if (alpha <= 90 && bar_xc != finXL) {
				theta2 += dT;
				alpha2 = (short)(alpha2+dA > 255 ? 255 : alpha2+dA);
			}
			if (bar_xc == finXL && bar_yc == finYB) txt_yc += dL;
			for (int i = 0; i < opts.length; i++) {
				opt_alpha[i] += opt_da[i];
				if (opt_alpha[i] > 1f) opt_alpha[i] = 1f;
			}
			if (theta > 2*Math.PI) theta = (float)(-2*Math.PI);
			if (theta2 > 2*Math.PI) theta2 = (float)(-2*Math.PI);
			if (flag_load) curr_state++;
			break;
		case 1: // idle
			if (sel_y < get_opt_y()) {
				int dS = (get_opt_y() - sel_y)/sH(5);
				sel_y += (dS > sH(4) ? dS : sH(4));
				if (sel_y > get_opt_y()) sel_y = get_opt_y();
			} else if (sel_y > get_opt_y()) {
				int dS = (get_opt_y() - sel_y)/sH(5);
				sel_y += (dS < sH(-4) ? dS : sH(-4));
				if (sel_y < get_opt_y()) sel_y = get_opt_y();
			}
		case 2: // transition to profile select
		case 3: // profile select
		case 4: // transition out	
		}
	}
	
	private int sX (int x) { return parent.scaleX(x); }
	private int sY (int y) { return parent.scaleY(y); }
	private int sH (int h) { return parent.scaleH(h); }
//	private int[] sX (int[] x) { return parent.scaleX(x); }
//	private int[] sY (int[] y) { return parent.scaleY(y); }
}