package wyvern.tools.typedAST.core.declarations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.objects.TypeDeclBinding;
import wyvern.tools.typedAST.core.binding.typechecking.LateNameBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;


/** Represents the contents of a structural type.  Not used at the top level to my knowledge;
 * used only within TypeVarDecl.
 * 
 * @author aldrich
 *
 */
public class TypeDeclaration extends AbstractTypeDeclaration implements CoreAST {
	private String name;
	protected DeclSequence decls;
	private Reference<Optional<TypedAST>> metadata;
	private NameBinding nameBinding;
	private TypeBinding typeBinding;
	
	private Reference<Value> metaValue = new Reference<>();

	// FIXME: I am not convinced typeGuard is required (alex).
	private boolean typeGuard = false;

	private boolean declGuard = false;
	public TypeDeclaration(String name, DeclSequence decls, Reference<Value> metadata, TaggedInfo taggedInfo, FileLocation clsNameLine) {
		// System.out.println("Initialising TypeDeclaration ( " + name + "): decls" + decls);
		this.name = name;
		this.decls = decls;
		nameBinding = new NameBindingImpl(name, null);
		typeBinding = new TypeBinding(name, null, metadata);
		Type objectType = new TypeType(this);



		nameBinding = new LateNameBinding(nameBinding.getName(), () ->
				metadata.get().getType());
		typeBinding = new TypeBinding(nameBinding.getName(), objectType, metadata);

		setupTags(name, typeBinding, taggedInfo);
		// System.out.println("TypeDeclaration: " + nameBinding.getName() + " is now bound to type: " + objectType);

		this.location = clsNameLine;
		this.metaValue = metadata;
	}
	
	@Override
	public Type getType() {
		return this.typeBinding.getType();
	}

	public DeclSequence getDecls() {
		return decls;
	}

	@Override
	public String getName() {
		return nameBinding.getName();
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; 
	}

	@Override
	public DeclType genILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<DeclType> genDeclTypeSeq(GenContext ctx){
		List<DeclType> declts = new LinkedList<DeclType>();
		for(Declaration d : decls.getDeclIterator()) {
			 // temporary context for verifying existence of variables within the same type so far
			if (d instanceof EffectDeclaration) { 
				/* HACK: only do it for effect-checking purposes (otherwise results in NullPointerException
				 * for tests like testTSL). */
				ctx = ctx.extend(d.getName(), null, new StructuralType(d.getName(), declts));
			}
			declts.add(d.genILType(ctx));
		}
		
		return declts;
	}
	
}
