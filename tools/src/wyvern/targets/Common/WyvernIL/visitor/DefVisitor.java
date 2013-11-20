package wyvern.targets.Common.WyvernIL.visitor;

import wyvern.targets.Common.WyvernIL.Def.*;

public interface DefVisitor<R> {
	R visit(VarDef varDef);
	R visit(ValDef valDef);
	R visit(TypeDef typeDef);
	R visit(Def def);
	R visit(ClassDef classDef);
}
