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
		public int index = -1;
		public int lowlink = -1;
		private LinkedList<Node> links;
		private String name;
		private TypedAST ast;

		private Node(String name, TypedAST ast, LinkedList<Node> links) {
			this.links = links;
			this.ast = ast;
			this.name = name;
		}
	}

	@Override
	protected TypedAST doTransform(TypedAST transform) {
		Node ret = searchFile("entry", transform);
		List<List<Node>> sccs = TarjansSCCCheck();
		if (sccs.size() > 0) {
			StringBuilder allSCCs = new StringBuilder(sccs.size());
			for (List<Node> scc : sccs) {
				StringBuilder sccBuilder = new StringBuilder(scc.size()*2+1);
				sccBuilder.append("\t");
				Iterator<Node> iterator = scc.iterator();
				while (iterator.hasNext()) {
					Node component = iterator.next();
					sccBuilder.append(component.name);
					sccBuilder.append(" -> ");
				}
				sccBuilder.append(scc.get(0).name + "\n");
				allSCCs.append(sccBuilder.toString());
			}
			ToolError.reportError(ErrorMessage.IMPORT_CYCLE, allSCCs.toString(), transform);
		}
		return transform;
	}

	private Node searchFile(String name, TypedAST file) {
		LinkedList<Node> links = new LinkedList<>();
		Node node = new Node(name, file, links);
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
			imports.add(searchFile(((ImportDeclaration) root).getSrc(), ast));
		}
	}

	private int index = 0;
	private Stack<Node> stack = new Stack<>();
	private HashSet<Node> stackSet = new HashSet<>();
	private List<List<Node>> TarjansSCCCheck() {
		List<List<Node>> sccs = new LinkedList<>();
		Collection<Node> V = graph.values();
		for (Node v : V) {
			if (v.index == -1)
				strongConnect(v, sccs);
		}
		return sccs;
	}

	private void strongConnect(Node v, List<List<Node>> sccs) {
		// Set the depth index for v to the smallest unused index
		v.index = index;
		v.lowlink = index++;
		stack.push(v);
		stackSet.add(v);

		// Consider successors of v
		for (Node w : v.links) {
			if (w.index == -1) {
				// Successor w has not yet been visited; recurse on it
				strongConnect(w, sccs);
				v.lowlink = Math.min(v.lowlink, w.lowlink);
			} else if (stackSet.contains(w)) {
				// Successor w is in stack S and hence in the current SCC
				v.lowlink = Math.min(v.lowlink, w.index);
			}
		}

		// If v is a root node, pop the stack and generate an SCC
		if (v.lowlink == v.index) {
			List<Node> SCC = new LinkedList<>();
			Node node = null;
			do {
				node = stack.pop();
				stackSet.remove(node);
				SCC.add(node);
			} while (node != v);

			if (SCC.size() > 1)
				sccs.add(SCC);
		}
	}
}
