package wyvern.tools.types;

public class SubtypeRelation {
	private Type subtype;
	private Type supertype;
	
	public Type getSubtype() { return this.subtype; }
	public Type getSupertype() { return this.supertype; }
	
	public SubtypeRelation(Type subtype, Type supertype) {
		this.subtype = subtype; 
		this.supertype = supertype;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SubtypeRelation) {
			SubtypeRelation sr = (SubtypeRelation) o;
			
			// System.out.println("Testing equality of: " + this + " and " + sr);
			
			return 	sr.subtype.equals(this.subtype) &&
					sr.supertype.equals(this.supertype);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.subtype.hashCode() * this.supertype.hashCode();
	}
	
	@Override
	public String toString() {
		return this.subtype + " <: " + this.supertype;
	}
}
