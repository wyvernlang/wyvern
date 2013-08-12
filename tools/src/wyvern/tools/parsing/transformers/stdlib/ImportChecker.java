package wyvern.tools.parsing.transformers.stdlib;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.transformers.TransformerBase;
import wyvern.tools.parsing.transformers.TypedASTTransformer;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.*;

/**
 * Created by Ben Chung on 8/12/13.
 */
public class ImportChecker extends TransformerBase<TypedAST> {
	public <T extends TypedAST> ImportChecker(TypedASTTransformer<T> base) {
		super(base);
	}

	private HashSet<TypedAST> visited = new HashSet<>();

	@Override
	protected TypedAST doTransform(TypedAST transform) {
		if (visited.contains(transform))
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, transform);
		visited.add(transform);
		findDeclarations(transform);
		return transform;
	}

	private void findDeclarations(TypedAST root) {
		if (root instanceof Sequence) {
			for (TypedAST ast : (Sequence)root)
				findDeclarations(ast);
		}
		if (root instanceof ImportDeclaration) {
			doTransform(((ImportDeclaration) root).getAST());
		}
	}
}
