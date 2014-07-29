package wyvern.targets.Common.wyvernIL.IL.visitor;

import wyvern.targets.Common.wyvernIL.IL.Def.*;

public interface DefVisitor<R> {
	R visit(VarDef varDef);
	R visit(ValDef valDef);
	R visit(TypeDef typeDef);
	R visit(Def def);
	R visit(ClassDef classDef);
	R visit(ImportDef importDef);
	R visit(KeywordDef keywordDef);
}
