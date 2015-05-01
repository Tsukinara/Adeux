import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Display extends JFrame implements Runnable {

	private static final long serialVersionUID = 4767630629171590730L;
	private static final String DEFAULT_TITLE = "Project \u00c1deux";
	private static final int s_width = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int s_height = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final String font_path = "resources\\fonts\\";
	public static final String img_path = "resources\\images\\";
	public static final String musc_path = "resources\\music\\";
	public static final String settings = "profile\\settings.dat";
	public final Color bg_color = new Color(26, 26, 26, 255);
	
	public int width, height;
	private int draw_width, draw_height, offset_y, offset_x;
	private boolean windowed;
	
	private HashMap<String, BufferedImage> images;
	private MPlayer sfxplayer, mscplayer;
	private LoadingScreen s_ls;
	private Menu s_mn;
	private ProfileSelect s_ps;
	private AppCore s_ac;
	private SettingsMenu s_sm;
	protected Settings set;
	protected Snow[] snow;

	private Thread curr;
	private NoteBuffer buffer;
	public State state;

	public enum State {
		LOADING, MENU, MAIN, SETTINGS
	}

	public Display(NoteBuffer buffer) {
		super(DEFAULT_TITLE);
		this.state = State.MENU;
		this.buffer = buffer;
		this.sfxplayer = null; this.mscplayer = null;
		this.width = s_width; this.height = s_height;
		this.set = new Settings(new File(this.settings));
		
		if (set.window_size == -1) {
			windowed = false; width = s_width; height = s_height;
		} else { 
			windowed = true; width = set.window_size; height = set.window_size*9/16;
		}
		
		setUndecorated(!windowed);
		setSize(width, height);	

		setLocationRelativeTo(null);
		setResizable(false);
		initialize_basics();

		s_ls = new LoadingScreen(this);
		s_mn = new Menu(this);
		s_ac = new AppCore();
		s_sm = new SettingsMenu(this);
		s_ps = new ProfileSelect(this);

		setIconImage(images.get("FR_ICON"));
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Invisimouse"); 
		setCursor(blankCursor);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);

		this.curr = new Thread(this);
		this.curr.start();

		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				switch (state) {
				case LOADING: s_ls.handle(e); break;
				case MENU: s_mn.handle(e); break;
				case MAIN: s_ac.handle(e); break;
				case SETTINGS: s_sm.handle(e); break;
				}
			}

			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});

		//TODO: REMOVE THIS
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				//System.exit(0);
			}

		});
	}
	
	private void calculate_offsets() {
		this.draw_width = width;
		this.draw_height = width*9/16;
		this.offset_y = (height - draw_height) / 2; this.offset_x = 0;
		if (windowed) {
			this.offset_x += (getWidth() - getContentPane().getWidth())/2;
			this.offset_y += (getHeight() - getContentPane().getHeight()) - offset_x;
		}
	}
	
	private void begin() {
		setVisible(true); 
		getContentPane().setPreferredSize(new Dimension(width, height)); pack();
		calculate_offsets();
	}

	private void initialize_basics() {
		calculate_offsets();
		this.images = new HashMap<String, BufferedImage>();
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font_path + "PlantinMTStd-Bold.otf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font_path + "PlantinMTStd-BoldItalic.otf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font_path + "PlantinMTStd-Italic.otf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font_path + "PlantinMTStd-Regular.otf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font_path + "Tangerine_Bold.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font_path + "Tangerine_Regular.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font_path + "OpusTextStd.otf")));

			images.put("LOAD_BG", ImageIO.read(new File(img_path + "load_bg.png")));
			images.put("LOGO_BK", ImageIO.read(new File(img_path + "logo_bk.png")));
			images.put("LOGO_WH", ImageIO.read(new File(img_path + "logo_wh.png")));
			images.put("FR_ICON", ImageIO.read(new File("resources\\icons\\icon.png")));

		} catch (IOException|FontFormatException e) {
			e.printStackTrace();
		}
	}

	public void load_all_resources() {
		try {
			images.put("MENU_BG", ImageIO.read(new File(img_path + "menu_bg.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int scaleX (int x_old) { return ((int) ((double)x_old / 1920.0 * (double)this.draw_width)) + offset_x; }	
	public int scaleW (int w_old) { return (int) ((double)w_old / 1920.0 * (double)this.draw_width); }
	public int scaleY (int y_old) { return ((int) ((double)y_old / 1080.0 * (double)this.draw_height)) + offset_y; }
	public int scaleH (int h_old) { return (int) ((double)h_old / 1080.0 * (double)this.draw_height); }
	
	public int[] scaleX (int[] x_old) {
		int[] x_new = new int[x_old.length];
		for(int i = 0; i < x_old.length; i++) {
			x_new[i] = scaleX(x_old[i]);
		}
		return x_new;
	}

	public int[] scaleY (int[] y_old) {
		int[] y_new = new int[y_old.length];
		for(int i = 0; i < y_old.length; i++) {
			y_new[i] = scaleY(y_old[i]);
		}
		return y_new;
	}
	
	public int[] scaleH (int[] h_old) {
		int[] h_new = new int[h_old.length];
		for(int i = 0; i < h_old.length; i++) {
			h_new[i] = scaleH(h_old[i]);
		}
		return h_new;
	}

	public void paint(Graphics g) {
		Image i=createImage(getWidth(), getHeight()); 
		render((i.getGraphics()));
		g.drawImage(i,0,0,this);
	}
	
	public void play_clip(String filename) {
		if (sfxplayer != null) sfxplayer.close();
		sfxplayer = new MPlayer(musc_path + filename);
		sfxplayer.play();
	}
	
	public void play_bgm(String filename) {
		if (mscplayer != null) mscplayer.close();
		mscplayer = new MPlayer(musc_path + filename);
		mscplayer.loop();
	}
	public void stop_bgm(String filename) { if (mscplayer != null) mscplayer.close(); }

	public void render(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

		switch (this.state) {
		case LOADING:
			s_ls.render(g2d);
			break;
		case MENU:
			s_mn.render(g2d);
			break;
		case MAIN:
			s_ac.render(g2d);
			break;
		case SETTINGS:
			s_sm.render(g2d);
		}
	}

	public void set_state(State s) { this.state = s; }

	public void set_buffer(NoteBuffer b) { this.buffer = b; }
	public NoteBuffer get_buffer() { return this.buffer; }
	public HashMap<String, BufferedImage> get_images() { return this.images; }

	public void run() {
		while (Thread.currentThread() == curr) {
			repaint();
			switch (this.state) {
			case LOADING: s_ls.step(); break;
			case MENU: s_mn.step(); break;
			case MAIN: s_ac.step(); break;
			case SETTINGS: s_sm.step(); break;
			}
			try { Thread.sleep(20); } 
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	public static void main(String [] args) {
		Display d = new Display(null);
		d.begin();
	}
}