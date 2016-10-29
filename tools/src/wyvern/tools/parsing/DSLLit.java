package wyvern.tools.parsing;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Invokable;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.TSLBlock;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

/**
 * Created by Ben Chung on 3/11/14.
 */
public class DSLLit extends AbstractExpressionAST implements ExpressionAST {
	Optional<String> dslText = Optional.empty();
	TypedAST dslAST = null;
	Type dslASTType = null;
	FileLocation location;

	public void setText(String text) {
		if (dslText == null)
			throw new RuntimeException();
		dslText = Optional.of(text);
	}

	public Optional<String> getText() { return dslText; }

	public DSLLit(Optional<String> dslText) {
		this.dslText = (dslText);
	}

	public DSLLit(Optional<String> dslText, FileLocation loc) {
		location = loc;
		this.dslText = (dslText);
	}

	public TypedAST getAST() { return (dslAST); }

	@Override
	public Type getType() {
		return dslASTType;
	}

	private Type getDefaultType() {
		//TODO
		return null;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Type dslType = expected.orElseGet(this::getDefaultType);

		Value vparser =
				Util.invokeValue(dslType.getResolvedBinding().get().getMetadata().get().get(),
						"getParser", UnitVal.getInstance(FileLocation.UNKNOWN));
		ExtParser parser = (ExtParser) Util.toJavaObject(vparser, ExtParser.class);

		try {
			dslAST = new TSLBlock(parser.parse(new ParseBuffer(dslText.get())));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dslAST.typecheck(env,expected);
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return null;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return null;
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		if (expectedType == null) {
			ToolError.reportError(ErrorMessage.NO_EXPECTED_TYPE, this);
		}
		try {
			final wyvern.target.corewyvernIL.expression.Value metadata = expectedType.getMetadata(ctx);
			if (!(metadata instanceof Invokable)) {
				ToolError.reportError(ErrorMessage.METADATA_MUST_BE_AN_OBJECT, this, expectedType.toString());
			}
			ValueType metaType = metadata.getType();			
			final DeclType parseTSLDecl = metaType.getStructuralType(ctx).findDecl("parseTSL", ctx);
			if (parseTSLDecl == null) {
				ToolError.reportError(ErrorMessage.METADATA_MUST_INCLUDE_PARSETSL, this, expectedType.toString());				
			}
			// TODO: check that parseTSLDecl has the right signature
			List<wyvern.target.corewyvernIL.expression.Value> args = new LinkedList<wyvern.target.corewyvernIL.expression.Value>();
			args.add(new StringLiteral(dslText.get()));
			wyvern.target.corewyvernIL.expression.Value parsedAST = ((Invokable)metadata).invoke("parseTSL", args);
			// we get an option back, is it success?
			ValDeclaration isDefined = (ValDeclaration)((ObjectValue)parsedAST).findDecl("isDefined");			
			BooleanLiteral success = (BooleanLiteral)isDefined.getDefinition();
			if (success.getValue()) {
				ValDeclaration valueDecl = (ValDeclaration)((ObjectValue)parsedAST).findDecl("value");
				ObjectValue astWrapper = (ObjectValue)valueDecl.getDefinition();
				ValDeclaration astDecl = (ValDeclaration)((ObjectValue)astWrapper).findDecl("ast");
				return (Expression) ((JavaValue)astDecl.getDefinition()).getWrappedValue();
			} else {
				ToolError.reportError(ErrorMessage.TSL_ERROR, this, "[detailed TSL error messages not supported yet]");
				throw new RuntimeException("can't get here");
			}
		} catch (ToolError e) {
			if (e.getTypecheckingErrorMessage() == ErrorMessage.CANNOT_USE_METADATA_IN_SAME_FILE) {
				if (e.getLocation() == null) {
					// provide an error with the usage location
					ToolError.reportError(ErrorMessage.CANNOT_USE_METADATA_IN_SAME_FILE, this);
				}
			}
			throw e;
		}
	}
}
