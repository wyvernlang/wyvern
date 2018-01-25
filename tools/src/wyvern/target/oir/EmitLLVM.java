package wyvern.target.oir;

public interface EmitLLVM {
    <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state);
}
