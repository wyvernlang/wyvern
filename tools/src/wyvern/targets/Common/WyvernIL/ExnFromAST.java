package wyvern.targets.Common.WyvernIL;

import org.objectweb.asm.commons.StaticInitMerger;
import wyvern.targets.Common.WyvernIL.Def.*;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Expr.Inv;
import wyvern.targets.Common.WyvernIL.Imm.VarRef;
import wyvern.targets.Common.WyvernIL.Stmt.*;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;

import javax.lang.model.element.Name;
import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ben Chung on 11/11/13.
 */
public class ExnFromAST implements CoreASTVisitor {
	private List<Statement> statements = new ArrayList<Statement>();

	public List<Statement> getStatments() {
		return statements;
	}
	private TLFromAST TLFromASTApply(TypedAST in) {
		if (in == null)
			return null;
		if (!(in instanceof CoreAST))
			throw new RuntimeException();
		CoreAST ast = (CoreAST) in;
		TLFromAST t = new TLFromAST();
		ast.accept(t);
		return t;
	}
	private List<Statement> getBodyAST(TypedAST in) {
		if (!(in instanceof CoreAST))
			throw new RuntimeException();
		CoreAST ast = (CoreAST) in;
		ExnFromAST t = new ExnFromAST();
		ast.accept(t);
		return t.statements;
	}




	@Override
	public void visit(ValDeclaration valDeclaration) {
		TLFromAST tlator = TLFromASTApply(valDeclaration.getDefinition());
		statements.addAll(tlator.getStatements());
		statements.add(new Defn(new ValDef(valDeclaration.getName(), tlator.getExpr())));
	}

	@Override
	public void visit(VarDeclaration valDeclaration) {
		TLFromAST tlator = TLFromASTApply(valDeclaration.getDefinition());
		statements.addAll(tlator.getStatements());
		statements.add(new Defn(new VarDef(valDeclaration.getName(), tlator.getExpr())));
	}
	@Override
	public void visit(ClassDeclaration clsDeclaration) {
		ClassDef def = getClassDef(clsDeclaration);
		statements.add(new Defn(def));
	}

	@Override
	public void visit(DefDeclaration meth) {
		List<Statement> stmts = getBodyAST(meth.getBody());
		List<NameBinding> argBindings = meth.getArgBindings();
		List<Def.Param> params = getParams(argBindings);
		statements.add(new Defn(new Def(meth.getName(), params, stmts)));
	}

	private List<Def.Param> getParams(List<NameBinding> argBindings) {
		List<Def.Param> params = new LinkedList<>();
		for (NameBinding arg : argBindings) {
			params.add(new Def.Param(arg.getName(), arg.getType()));
		}
		return params;
	}


	@Override
	public void visit(Assignment assignment) {
		TLFromAST tgt = TLFromASTApply(assignment.getTarget());
		TLFromAST val = TLFromASTApply(assignment.getValue());
		statements.addAll(tgt.getStatements());
		statements.addAll(val.getStatements());
		statements.add(new Assign(tgt.getExpr(), val.getExpr()));
	}

	@Override
	public void visit(TypeDeclaration interfaceDeclaration) {
		DeclSequence decls = interfaceDeclaration.getDecls();
		TypeDef def = getTypeDecl(interfaceDeclaration, decls);
		statements.add(new Defn(def));
	}

	private ClassDef getClassDef(ClassDeclaration cd) {
		List<Definition> definitions = new LinkedList<>();
		List<Definition> classDefs = new LinkedList<>();
		List<Statement> inializer = new LinkedList<>();
		for (Declaration decl : cd.getDecls().getDeclIterator()) {
			if (decl instanceof DefDeclaration) {
				List<Statement> bodyAST = getBodyAST(((DefDeclaration) decl).getBody());
				Def e = new Def(decl.getName(), getParams(((DefDeclaration) decl).getArgBindings()), bodyAST);
				if (((DefDeclaration) decl).isClass())
					classDefs.add(e);
				else
					definitions.add(e);
			} else if (decl instanceof ValDeclaration) {
				TLFromAST gen = TLFromASTApply(((ValDeclaration) decl).getDefinition());
				if (gen != null) {
					inializer.addAll(gen.getStatements());
					inializer.add(new Assign(new Inv(new VarRef("this"),decl.getName()), gen.getExpr()));
				}
				ValDef e = new ValDef(decl.getName(), gen.getExpr());
				if (((ValDeclaration) decl).isClass())
					classDefs.add(e);
				else
					definitions.add(e);
			} else if (decl instanceof VarDeclaration) {
				TLFromAST gen = TLFromASTApply(((VarDeclaration) decl).getDefinition());
				inializer.addAll(gen.getStatements());
				inializer.add(new Assign(new Inv(new VarRef("this"),decl.getName()), gen.getExpr()));
				VarDef e = new VarDef(decl.getName(), gen.getExpr());
				if (((VarDeclaration) decl).isClass())
					classDefs.add(e);
				else
					definitions.add(e);
			} else if (decl instanceof TypeDeclaration) {
				TypeDef typeDecl = getTypeDecl((TypeDeclaration) decl, ((TypeDeclaration) decl).getDecls());
				classDefs.add(typeDecl);
			} else if (decl instanceof ClassDeclaration) {
				ClassDef classDef = getClassDef((ClassDeclaration) decl);
				classDefs.add(classDef);
			}
		}
		definitions.add(new Def("$init", new LinkedList<Def.Param>(), inializer));
		return new ClassDef(cd.getName(), definitions, classDefs);
	}

	private TypeDef getTypeDecl(TypeDeclaration interfaceDeclaration, DeclSequence decls) {
		List<Definition> definitions = new LinkedList<>();
		for (Declaration decl : decls.getDeclIterator()) {
			if (decl instanceof DefDeclaration) {
				definitions.add(new Def(decl.getName(), getParams(((DefDeclaration) decl).getArgBindings()), null));
			} else if (decl instanceof ValDeclaration) {
				definitions.add(new ValDef(decl.getName(), null));
			} else if (decl instanceof VarDeclaration) {
				definitions.add(new VarDef(decl.getName(), null));
			} else if (decl instanceof TypeDeclaration) {
				definitions.add(getTypeDecl((TypeDeclaration) decl, ((TypeDeclaration) decl).getDecls()));
			} else if (decl instanceof ClassDeclaration) {

			}
		}
		return new TypeDef(interfaceDeclaration.getName(), definitions);
	}

	@Override
	public void visit(Sequence sequence) {
		Iterator<TypedAST> flatten = sequence.flatten();
		List<Statement> foo = new ArrayList<Statement>();

		while(flatten.hasNext()){
			TypedAST ast = flatten.next();

			if (!(ast instanceof CoreAST)) {
				throw new RuntimeException();
			}

			CoreAST cast = (CoreAST)ast;
			ExnFromAST visitor = new ExnFromAST();
			cast.accept(visitor);

			foo.addAll(visitor.getStatments());
		}

		this.statements = foo;
	}


	@Override
	public void visit(WhileStatement whileStatement) {
		Label start = new Label();
		Label inner = new Label();
		Label end = new Label();
		statements.add(start);
		TLFromAST tlFromAST = TLFromASTApply(whileStatement.getConditional());
		statements.addAll(tlFromAST.getStatements());
		statements.add(new IfStmt(tlFromAST.getExpr(), inner));
		statements.add(new Goto(end));
		statements.add(inner);
		List<Statement> bodyAST = getBodyAST(whileStatement.getBody());
		statements.addAll(bodyAST);
		statements.add(new Goto(start));
		statements.add(end);
	}


	//UNUSED

	@Override
	public void visit(LetExpr let) {

	}

	@Override
	public void visit(TypeInstance typeInstance) {

	}

	//END UNUSED

	//DELEGATE TO TLFromAST
	private void getSimp(CoreAST in) {
		TLFromAST apply = TLFromASTApply(in);
		statements.addAll(apply.getStatements());
		statements.add(new Pure(apply.getExpr()));
	}

	@Override
	public void visit(IfExpr ifExpr) {
		getSimp(ifExpr);
	}

	@Override
	public void visit(Fn fn) {
		getSimp(fn);
	}

	@Override
	public void visit(UnitVal unitVal) {
		getSimp(unitVal);
	}

	@Override
	public void visit(New new1) {
		getSimp(new1);
	}

	@Override
	public void visit(TupleObject meth) {
		getSimp(meth);
	}

	@Override
	public void visit(Invocation invocation) {
		getSimp(invocation);
	}

	@Override
	public void visit(Application application) {
		getSimp(application);
	}

	@Override
	public void visit(Variable variable) {
		getSimp(variable);
	}

	@Override
	public void visit(IntegerConstant booleanConstant) {
		getSimp(booleanConstant);
	}

	@Override
	public void visit(StringConstant booleanConstant) {
		getSimp(booleanConstant);
	}

	@Override
	public void visit(BooleanConstant booleanConstant) {
		getSimp(booleanConstant);
	}

}
