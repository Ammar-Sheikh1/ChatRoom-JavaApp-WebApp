package project7;

/*
 * "Pair" class which acts more as a 2-tuples in this instance.
 *	Order does not matter when boolean comparing (equals/hashcode)
 * */
public class Pair {
	
	
	private final String x;
	private final String y;
	
	/**
	 * Constructs Pair with member fields set to a and b
	 * @param int a
	 * @param int b
	 */
	Pair(String a, String b){
		x = a;
		y = b;
	}
	
	/**
	 * Returns x member variable integer
	 * @return
	 */
	public String getX() {
		return x;
	}
	/**
	 * Returns y member variable integer
	 * @return
	 */
	public String getY() {
		return y;
	}
	
	/**
	 * Returns true if two Pair objects have equal member fields.
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof Pair))
			return false;
		
		Pair p = (Pair) o;
		return (this.x.equals(p.x) && this.y.equals(p.y)) || (this.x.equals(p.y) && this.y.equals(p.x)); 
	}
	
	/**
	 * Returns the hashCode of the Pair based solely member fields.
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result += x.hashCode();
		result += y.hashCode();
		return result;
	}
}
