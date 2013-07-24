package wyvern.tools.typedAST.core.declarations;

import java.util.LinkedList;

import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class PartialDeclSequence {
	
	private LinkedList<PartialDecl> decls = new LinkedList<PartialDecl>();
	
	private LinkedList<EnvironmentExtender> fullDecls = new LinkedList<>();
	
	public PartialDeclSequence () {
	}
	
	public Environment add(PartialDecl decl, Environment env) {
		if (!isResolved()) {
			decls.push(decl);
			return decl.extend(env);
		} else
			throw new RuntimeException("Tried to add to resolved decl sequence");
	}
	
	public Pair<TypedAST,Environment> resolve(Environment env) {
        LinkedList<PartialDecl> declsI = (LinkedList<PartialDecl>)decls.clone();
        decls = null;
		for (PartialDecl decl : declsI) {
			decl.preParseTypes(env);
		}
        for (PartialDecl decl : declsI) {
            decl.preParseDecls(env);
        }
		for (PartialDecl decl : declsI) {
			TypedAST ast = decl.getAST(env);
			fullDecls.addFirst((EnvironmentExtender) ast);
		}
		DeclSequence declseq = new DeclSequence(fullDecls);
		
		return new Pair<TypedAST, Environment>(declseq,env);
	}
	
	public boolean isResolved() {
		return decls == null;
	}
	
	public boolean isEmpty() {
		return decls.isEmpty();
	}

}
