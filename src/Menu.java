import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Menu {
	private final short dA = 5, dL = 10;
	private final float dT = 0.1f, dT2 = 0.07f, doA = 0.05f;
	private final int num_snowflakes = 200;
	private final short max_opt = 2;
	private final int origX = 960, origY = 701;
	private final int finXL = 760, finYB = 849, finYT = 553;
	private final int box_y = 571, box_w = 404, box_h = 253;
	private final int sel_h = 70;
	private final int setXL = 440, setYB = 924, setYT = 156;
	private final int setTT = 213;

	private final String[] opts = {"start", "settings", "exit"};
	private final int[] opt_y = {641, 714, 787};
	
	private Display parent; private Display.State next_state;
	private Font pl_base;
	private short curr_state, curr_opt, bar_height, alpha, alpha2, delay;
	private int bar_xc, bar_yc, bar_yc2, txt_yc, sel_y;
	private float theta, theta2, theta3;
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
		parent.snow = new Snow[num_snowflakes];
		for (int i = 0; i < num_snowflakes; i++)
			parent.snow[i] = new Snow(
					Math.random()*sX(1920), Math.random()*sY(1080), 
					Math.random()*sW(8)+1, Math.random()*sH(6)+sH(4), 
					Math.random()*sW(8)+sW(5));
		this.theta = (float)Math.PI; this.theta2 = (float)Math.PI;
		this.theta3 = (float)Math.PI;
		this.bar_xc = origX; this.bar_yc = origY; this.txt_yc = 590;
		this.bar_yc2 = finYT;
		this.pl_base = new Font("Plantin MT Std", Font.PLAIN, sH(48));
		this.flag_load = false;
	}
	
	public void render(Graphics2D g) {
		draw_primary(g);
		switch (curr_state) {
			case 0: transition_in(g); break;
			case 1: opt_select(g); break;
			case 2: transition_profile(g); break;
			case 3: profile_select(g); break;
			case 4: transition_settings(g); break;
			case 5: transition_out(g); break;
			case 6: transition_back(g); break;
		}
	}
		
	private void draw_primary(Graphics2D g) {
		g.drawImage(parent.get_images().get("MENU_BG"), sX(0), sY(0), sW(1920), sH(1080), null);
		g.setColor(Color.WHITE);
		for (Snow s : parent.snow) g.fillOval(sX(s.x), sY(s.y), sW(s.dia), sH(s.dia));
		
		g.setColor(parent.bg_color);
		g.fillRect(sX(0), sY(0), sW(1920), sH(this.bar_height));
		g.fillRect(sX(0), sY(1080-bar_height+1), sW(1920), sH(bar_height));	
	}
	
	private void transition_in(Graphics2D g) {
		g.drawImage(parent.get_images().get("LOGO_BK"), sX(571), sY(200), sW(777), sH(258), null);
		g.setColor(new Color(255, 255, 255, alpha));
		g.fillRect(sX(0), sY(0), sW(1920), sH(1080));
		
		g.drawImage(parent.get_images().get("LOGO_BK"), sX(571), sY(200), sW(777), sH(258), null);
		
		g.setColor(parent.bg_color);
		g.fillRect(sX(0), sY(0), sW(1920), sH(this.bar_height));
		g.fillRect(sX(0), sY(1080-bar_height+1), sW(1920), sH(this.bar_height));
		
		g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		if (bar_yc < finYB) bar_yc = origY + 1 + (int)((finYB-origY)/2 * (1+Math.cos(theta)));
		else bar_yc = finYB;
		if (bar_xc > finXL) bar_xc = origX - 1 - (int)((origX-finXL)/2 * (1+Math.cos(theta2)));
		else bar_xc = finXL;
		if (bar_yc == finYB) {
			g.setColor(new Color(222, 238, 246, 200));
			g.fillRect(sX(bar_xc), sY(box_y), sW(2*(origX-bar_xc)), sH(box_h));
		}
		
		if(txt_yc - sH(930) > 0) {
			int a = ((txt_yc-sH(930))/2 > 255 ? 255 : (txt_yc - sH(930))/2);
			g.setColor(new Color(255, 255, 255, a));
			g.fillRect(sX(finXL), sY(sel_y), sW(box_w), sH(sel_h));
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
			if (i == opts.length - 1 && opt_alpha[i] == 1f) flag_load = true;
		}
	}
	
	private void opt_select(Graphics2D g) {
		g.drawImage(parent.get_images().get("LOGO_BK"), sX(571), sY(200), sW(777), sH(258), null);
		g.setColor(new Color(222, 238, 246, 200));
		g.fillRect(sX(finXL), sY(box_y), sW(box_w), sH(box_h));
		
		g.setColor(Color.WHITE);
		g.fillRect(sX(finXL), sY(sel_y), sW(box_w), sH(sel_h));
		
		g.setColor(parent.bg_color);
		g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine(sX(finXL), sY(finYB), sX(finXL), sY(finYT));
		g.drawLine(sX(1920-finXL), sY(finYB), sX(1920-finXL), sY(finYT));
		
		g.setFont(pl_base);
		for (int i = 0; i < opts.length; i++) {
			int fw = g.getFontMetrics().stringWidth(opts[i]);
			g.drawString(opts[i], (sX(1920)-fw)/2, sY(opt_y[i]));
		}
	}
	
	private void transition_profile(Graphics2D g) {

	}
	
	private void profile_select(Graphics2D g) {
		
	}
	
	private void transition_out(Graphics2D g) {
		
	}
	
	private void transition_back(Graphics2D g) {
		transition_in(g);
	}
	
	private void transition_settings(Graphics2D g) {
		g.setComposite(AlphaComposite.SrcOver.derive(1f));
				
		if (bar_yc < setYB) bar_yc = finYB + 2 + (int)((setYB-finYB)/2 * (1+Math.cos(theta3)));
		else bar_yc = setYB;
		if (bar_yc2 > setYT) bar_yc2 = finYT - 2 - (int)((finYT-setYT)/2 * (1+Math.cos(theta3)));
		else bar_yc2 = setYT;
		if (bar_xc > setXL) bar_xc = finXL - 2 - (int)((finXL-setXL)/2 * (1+Math.cos(theta3)));
		else bar_xc = setXL;
		if (sel_y > setTT) sel_y = get_opt_y() - 2 - (int)((get_opt_y()-setTT)/2 * (1+Math.cos(theta3)));
		else sel_y = setTT;
		
		int menu_h = (bar_yc - bar_yc2) - (finYB - finYT - box_h);
		int menu_y = bar_yc2 + (bar_yc - bar_yc2 - menu_h)/2;
		g.setColor(new Color(222, 238, 246, 200));
		g.fillRect(sX(bar_xc), sY(menu_y), sW(2*(origX-bar_xc)), sH(menu_h));
		
		g.setColor(Color.WHITE);
		g.fillRect(sX(bar_xc), sY(sel_y), sW(2*(origX-bar_xc)), sH(sel_h));
		
		g.setColor(parent.bg_color);
		g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine(sX(bar_xc), sY(bar_yc), sX(bar_xc), sY(bar_yc2));
		g.drawLine(sX(1920-bar_xc), sY(bar_yc), sX(1920-bar_xc), sY(bar_yc2));
		
		g.setFont(pl_base);
		FontMetrics fm = g.getFontMetrics();
		g.setComposite(AlphaComposite.SrcOver.derive(1f));
		int fw =fm.stringWidth(opts[1]);
		int txty = get_opt_y() - opt_y[1];
		g.drawString(opts[1], (sX(1920)-fw)/2, sY(sel_y - txty));
	
		for (int i = 0; i < opts.length; i++) {
			if (!opts[i].equals("settings")) {
				g.setComposite(AlphaComposite.SrcOver.derive((float)(alpha/255f)));
				fw = fm.stringWidth(opts[i]);
				g.drawString(opts[i], (sX(1920)-fw)/2, sY(opt_y[i]));
			}
		}
		g.drawImage(parent.get_images().get("LOGO_BK"), sX(571), sY(200), sW(777), sH(258), null);
	}
	
	private int get_opt_y() { return 71*curr_opt + 593; }
	
	public void handle(KeyEvent e) {
		switch (e.getKeyCode()) {
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
						case 0: curr_state = 3; break;
						case 1: sel_y = get_opt_y(); next_state = Display.State.SETTINGS; curr_state = 4; break;
						case 2: System.exit(0);
					}
				}
			case KeyEvent.VK_X: case KeyEvent.VK_ESCAPE:
				if (curr_state < 2) curr_opt = 2;
				break;
		}
	}
	
	public void step() {
		for (Snow s : parent.snow) s.step();
		switch (curr_state) {
		case 0: // transition in from load
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
			
			if (sel_y < get_opt_y()) {
				int dS = (get_opt_y() - sel_y)/sH(5);
				sel_y += (dS > sH(4) ? dS : sH(4));
				if (sel_y > get_opt_y()) sel_y = get_opt_y();
			} else if (sel_y > get_opt_y()) {
				int dS = (get_opt_y() - sel_y)/sH(5);
				sel_y += (dS < sH(-4) ? dS : sH(-4));
				if (sel_y < get_opt_y()) sel_y = get_opt_y();
			}
			
			if (theta > 2*Math.PI) theta = (float)(-2*Math.PI);
			if (theta2 > 2*Math.PI) theta2 = (float)(-2*Math.PI);
			if (flag_load) { curr_state = 1; alpha = 255; }
			
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
			break;
		case 2: // transition to profile select
		case 3: // profile select
		case 4: // transition settings
			alpha = (short)(alpha-2*dA < 0 ? 0 : alpha-2*dA);
			theta3 += dT2;
			if (theta3 > 2*Math.PI) { 
				parent.set_state(next_state); 
				curr_state = 6;
			}
			break;
		case 5: // transition main
		case 6: // transition from settings / profile
		}
	}
	
	private int sX (int x) { return parent.scaleX(x); }
	private int sY (int y) { return parent.scaleY(y); }
	private int sW (int w) { return parent.scaleW(w); }
	private int sH (int h) { return parent.scaleH(h); }
//	private int[] sX (int[] x) { return parent.scaleX(x); }
//	private int[] sY (int[] y) { return parent.scaleY(y); }
}