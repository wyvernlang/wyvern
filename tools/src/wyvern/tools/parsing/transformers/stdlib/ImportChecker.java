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
	private HashMap<TypedAST, Node> graph = new HashMap<>();

	private static class Node {
		private LinkedList<Node> links;
		private TypedAST ast;

		private Node(TypedAST ast, LinkedList<Node> links) {
			this.links = links;
			this.ast = ast;
		}
	}

	@Override
	protected TypedAST doTransform(TypedAST transform) {
		Node ret = searchFile(transform);
		return transform;
	}

	private Node searchFile(TypedAST file) {
		LinkedList<Node> links = new LinkedList<>();
		Node node = new Node(file, links);
		graph.put(file, node);
		searchImports(file, links);
		return node;
	}

	private TypedAST searchImports(TypedAST transform, List<Node> imports) {
		findDeclarations(transform, imports);
		return transform;
	}

	private void findDeclarations(TypedAST root, List<Node> imports) {
		if (root instanceof Sequence) {
			for (TypedAST ast : (Sequence)root)
				searchImports(ast, imports);
		}
		if (root instanceof ImportDeclaration) {
			TypedAST ast = ((ImportDeclaration) root).getAST().get();
			if (graph.containsKey(ast)) {
				imports.add(graph.get(ast));
				return;
			}
			imports.add(searchFile(ast));
		}
	}
}
