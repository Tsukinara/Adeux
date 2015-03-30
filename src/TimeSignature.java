public class TimeSignature {
	
	public enum Type {
		SIMPLE_DUPLE, SIMPLE_TRIPLE, COMPOUND_DUPLE, COMPOUND_TRIPLE, UNKNOWN
	}
	public Type type;
	public short hi;
	public short lo;
	
	public TimeSignature(short top, short bottom) {
		this.hi = top;
		this.lo = bottom;
		
		switch (top) {
			case 2: case 4: 
				this.type = Type.SIMPLE_DUPLE; 
				break;
			case 3: 
				this.type = Type.SIMPLE_TRIPLE; 
				break;
			case 6: case 12:
				this.type = Type.COMPOUND_DUPLE;
				break;
			case 9:
				this.type = Type.COMPOUND_TRIPLE;
				break;
			default:
				this.type = Type.UNKNOWN;
		}
	}
}