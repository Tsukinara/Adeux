public class Snow {
	public int x, y, dx, dy, dia;
	private Display parent;
	
	public Snow(double x, double y, double dx, double dy, double dia, Display parent) {
		this.x = (int)x; this.y = (int)y; 
		this.dx = (int)dx; this.dy = (int)dy;
		this.dia = (int)dia; this.parent = parent;
	}
	
	public void step() {
		x += dx; y += dy;
		if (x > parent.getWidth()) x = 0;
		if (y > parent.getHeight()) y = 0;
	}
}