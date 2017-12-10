package wyvern.tools.typedAST.core.declarations;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import wyvern.target.corewyvernIL.decltype.DeclType;
//import wyvern.targets.java.annotations.Val;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class DeclSequence extends Sequence {

	public DeclSequence() {
		this(Arrays.asList());
	}

	public DeclSequence(TypedAST... init) {
		this(Arrays.asList(init));
	}


	public static DeclSequence simplify(DeclSequence in) {
		return new DeclSequence(new Iterable() {
			@Override
			public Iterator iterator() {
				return new Iterator() {
					private Stack<Iterator> iterstack = new Stack<>();
					{
						iterstack.push(in.iterator());
					}
					private Object lookahead;

					@Override
					public boolean hasNext() {
						if (!iterstack.empty()) {
							if (!iterstack.peek().hasNext()) {
								iterstack.pop();
								return hasNext();
							}
							lookahead = iterstack.peek().next();
							if (lookahead instanceof DeclSequence) {
								iterstack.push(((DeclSequence) lookahead).iterator());
								lookahead = null;
								return hasNext();
							}
							return true;
						}
						return false;
					}

					@Override
					public Object next() {
						if (lookahead != null) {
							Object res = lookahead;
							lookahead = null;
							return res;
						}
						return iterstack.peek().next();
					}
				};
			}
		});
	}
	
	public DeclSequence(final Iterable first) {
		super(new Iterable<TypedAST>() {

			@Override
			public Iterator<TypedAST> iterator() {
				final Iterator<Declaration> iter = first.iterator();
				return new Iterator<TypedAST>() {
					@Override
					public boolean hasNext() {
						return iter.hasNext();
					}

					@Override
					public TypedAST next() {
						return iter.next();
					}

					@Override
					public void remove() {
						iter.remove();
					}

				};
			}

		});
		
	}

	public DeclSequence(Declaration decl) {
		super(decl);
	}

    /**
     * 
     * @return the sequence of require 
     */
	public Sequence filterRequires() {
		
		Sequence reqSeq = new DeclSequence();
		for (Declaration d : this.getDeclIterator()) {
			if(d instanceof ImportDeclaration && ((ImportDeclaration) d).isRequire()) {
				reqSeq = Sequence.append(reqSeq, d);
			}
		}
		return reqSeq;
	}

	/**
	 * 
	 * @return the sequence of import/instantiate
	 */
	public Sequence filterImportInstantiates() {
		Sequence impInstSeq = new DeclSequence();
		for (TypedAST d : this.getDeclIterator()) {
			if(d instanceof ImportDeclaration && !((ImportDeclaration) d).isRequire()
					|| d instanceof Instantiation) {
				impInstSeq = Sequence.append(impInstSeq, d);
			}
		}
		return impInstSeq;
	}

	/**
	 * 
	 * @return the sequence of simple declarations, not require/import/instantiate
	 */
	public Sequence filterNormal() {
		boolean recBlock = false;
		Sequence normalSeq = new Sequence();
		Sequence recSequence = new DeclSequence();
		for (TypedAST d : this.getDeclIterator()) {
			if(d instanceof TypeVarDecl || d instanceof DefDeclaration) {
				if(recBlock == false) {
					recBlock = true;
					recSequence = new DeclSequence();
				}
				recSequence = Sequence.append(recSequence, d);
			} else if(!(d instanceof ImportDeclaration) && !(d instanceof Instantiation)) {
				if(recBlock == true) {
				    recBlock = false;
					normalSeq = Sequence.append(normalSeq, recSequence);
				}
				normalSeq = Sequence.append(normalSeq, d);
			}
		}
		
		if (recBlock == true) {
			normalSeq = Sequence.append(normalSeq, recSequence);
		}
		return normalSeq;
	}
	
	/**
	 * Top-level translation of a mutually-recursive block of declarations.
	 */
	@Override
	public void genTopLevel(TopLevelContext tlc) {
		String newName = GenContext.generateName();
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls =
				new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		List<wyvern.target.corewyvernIL.decltype.DeclType> declts =
				new LinkedList<wyvern.target.corewyvernIL.decltype.DeclType>();
		
		// build up a context that has all the elements in this recursive block
		GenContext newCtx = tlc.getContext();
		
		for(TypedAST seq_ast : getDeclIterator()) {
			Declaration d = (Declaration) seq_ast;
			// TODO: refactor to make rec a method of Declaration
			newCtx = newCtx.rec(newName, d); // extend the environment
		}
		
		for(TypedAST seq_ast : getDeclIterator()) {
			ValueType type = new StructuralType(newName, declts);
			GenContext incrCtx = newCtx.extend(newName, new Variable(newName), type);
			Declaration d = (Declaration) seq_ast;
			declts.add(d.genILType(incrCtx));
		}
		
		ValueType type = new StructuralType(newName, declts);
		GenContext genCtx = newCtx.extend(newName, new Variable(newName), type);

		// Do the translation using this updated context 
		tlc.updateContext(genCtx);
		tlc.setReceiverName(newName);
		for(TypedAST seq_ast : getDeclIterator()) {
			Declaration d = (Declaration) seq_ast;
			wyvern.target.corewyvernIL.decl.Declaration decl = d.topLevelGen(genCtx, null);
			decls.add(decl);
			d.addModuleDecl(tlc);
		}
		tlc.setReceiverName(null);
	
		// determine if we need to be a resource type
		for (wyvern.target.corewyvernIL.decl.Declaration d: decls) {
			d.typeCheck(tlc.getContext(), tlc.getContext());
			if (d.containsResource(tlc.getContext())) {
				type = new StructuralType(newName, declts, true);
				break;
			}
		}
		
		/* wrap the declarations into an object */
		Expression newExp = new New(decls, newName, type, getLocation());
		tlc.addLet(newName, type, newExp, true);
	}
	
	/**
	 * Figure out the structural type represented by this sequence.
	 * @param ctx: context to evaluate in.
	 * @return structural type of this sequence.
	 */
	public StructuralType inferStructuralType (GenContext ctx, String selfName) {
		boolean isResource = false;
		
		// Fake an appropriate context.
		GenContext ctxTemp = ctx.extend(selfName, new Variable(selfName), null);
		
		// Store the types for each declaration in this list.
		List<DeclType> declTypes = new LinkedList<DeclType>();
		
		// Look at each declaration.
		wyvern.tools.typedAST.core.declarations.DelegateDeclaration delegateDecl = null;
		for (TypedAST d : this) {
			if (d instanceof wyvern.tools.typedAST.core.declarations.DelegateDeclaration) {
				delegateDecl = (wyvern.tools.typedAST.core.declarations.DelegateDeclaration)d;
			}
			else {
				Declaration dd = (Declaration) d;
				
				/* for checking whether effects used in an effect declaration or method header were declared
				 * in the type signature prior to its use. */
				if (dd instanceof EffectDeclaration) {
					/* HACK: only do it for effect-checking purposes (otherwise results in NullPointerException
					 * for tests like testTSL). */
					ctxTemp = ctxTemp.extend(dd.getName(), null, new StructuralType(dd.getName(), declTypes));
				}
				
				DeclType t = dd.genILType(ctxTemp);
				declTypes.add(t);
			}
		}
		
		final GenContext finalCtxTemp = ctxTemp; // for declTypes.stream() later, which requires this to be final
		
		// Add delegate object's declaration which has not been overridden to the structural type.
		if (delegateDecl != null) {
			StructuralType delegateStructuralType = delegateDecl.getType().getILType(ctxTemp).getStructuralType(ctxTemp);
			for (DeclType declType : delegateStructuralType.getDeclTypes()) {
				if (!declTypes.stream().anyMatch(newDefDecl-> newDefDecl.isSubtypeOf(declType, finalCtxTemp))) {
					declTypes.add(declType);
				}
			}
		}
		
		return new StructuralType(selfName, declTypes, isResource);
	}
}
