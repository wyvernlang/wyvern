package wyvern.target.oir;

public interface EmitLLVM {
	public abstract <T> T acceptVisitor (ASTVisitor<T> visitor, OIREnvironment oirenv);
}