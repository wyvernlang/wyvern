package wyvern.tools.parsing.transformers.stdlib;

import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ImportGraphBuilder<T extends ImportGraphBuilder.Node> {
	private final NodeFactory<T> factory;
	private HashMap<TypedAST, T> graph = new HashMap<>();
	public interface NodeFactory<T extends Node> {
		public T create(String name, TypedAST ast, LinkedList<Node<T>> links);
	}

	private static class SimpleNodeFactory implements NodeFactory<Node> {

		@Override
		public Node create(String name, TypedAST ast, LinkedList<Node<Node>> links) {
			return new Node(name, ast, links);
		}
	}



	public ImportGraphBuilder(NodeFactory<T> factory) {
		this.factory = factory;
	}

	public Map<TypedAST, T> getGraph(String name, TypedAST file) {
		searchFile(name, file);
		return graph;
	}

	public static class Node<T> {
		private LinkedList<Node<T>> links;
		private String name;
		private TypedAST ast;

		public Node(String name, TypedAST ast, LinkedList<Node<T>> links) {
			this.links = links;
			this.ast = ast;
			this.name = name;
		}

		public LinkedList<Node<T>> getLinks() {
			return links;
		}

		public String getName() {
			return name;
		}

		public TypedAST getAst() {
			return ast;
		}
	}

	private T searchFile(String name, TypedAST file) {
		LinkedList<Node<T>> links = new LinkedList<>();
		T node = factory.create(name, file, links);
		graph.put(file, node);
		searchImports(file, links);
		return node;
	}

	private TypedAST searchImports(TypedAST transform, List<Node<T>> imports) {
		findDeclarations(transform, imports);
		return transform;
	}

	private void findDeclarations(TypedAST root, List<Node<T>> imports) {
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
			imports.add(searchFile(((ImportDeclaration) root).getSrc(), ast));
		}
	}
}
