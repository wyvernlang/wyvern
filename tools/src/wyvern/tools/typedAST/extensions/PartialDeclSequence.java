package wyvern.tools.typedAST.extensions;

import java.util.Iterator;
import java.util.LinkedList;

import javax.management.RuntimeErrorException;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.extensions.declarations.PartialDecl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class PartialDeclSequence {
	
	private LinkedList<PartialDecl> decls = new LinkedList<PartialDecl>();
	
	private LinkedList<Declaration> fullDecls = new LinkedList<Declaration>();
	
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
		for (PartialDecl decl : decls) {
			fullDecls.addFirst((Declaration) decl.getAST(env));
		}
		DeclSequence declseq = new DeclSequence(fullDecls);
		decls = null;
		
		return new Pair<TypedAST, Environment>(declseq,env);
	}
	
	public boolean isResolved() {
		return decls == null;
	}

}
