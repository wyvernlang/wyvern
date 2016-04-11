package wyvern.target.oir;

public interface EmitLLVM {
    public abstract <S, T> T acceptVisitor (ASTVisitor<S, T> visitor, S state);
}
