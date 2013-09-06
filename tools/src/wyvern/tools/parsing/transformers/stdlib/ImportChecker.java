package wyvern.tools.parsing.transformers.stdlib;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.transformers.TransformerBase;
import wyvern.tools.parsing.transformers.TypedASTTransformer;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.*;

import static wyvern.tools.parsing.transformers.stdlib.ImportGraphBuilder.Node;

/**
 * Created by Ben Chung on 8/12/13.
 */
public class ImportChecker extends TransformerBase<TypedAST> {
	public <T extends TypedAST> ImportChecker(TypedASTTransformer<T> base) {
		super(base);
	}

	private static class SCCNode extends Node<SCCNode> {
		private int index = -1;
		private int lowlink = -1;

		private SCCNode(String name, TypedAST ast, LinkedList<Node<SCCNode>> links) {
			super(name, ast, links);
		}


	}


	@Override
	protected TypedAST doTransform(TypedAST transform) {
		List<List<SCCNode>> sccs = TarjansSCCCheck(new ImportGraphBuilder(new ImportGraphBuilder.NodeFactory<SCCNode>() {
			@Override
			public SCCNode create(String name, TypedAST ast, LinkedList<Node<SCCNode>> links) {
				return new SCCNode(name, ast, links);
			}
		}).getGraph("entry", transform));
		if (sccs.size() > 0) {
			StringBuilder allSCCs = new StringBuilder(sccs.size());
			for (List<SCCNode> scc : sccs) {
				StringBuilder sccBuilder = new StringBuilder(scc.size()*2+1);
				sccBuilder.append("\t");
				Iterator<SCCNode> iterator = scc.iterator();
				while (iterator.hasNext()) {
					SCCNode component = iterator.next();
					sccBuilder.append(component.getName());
					sccBuilder.append(" -> ");
				}
				sccBuilder.append(scc.get(0).getName() + "\n");
				allSCCs.append(sccBuilder.toString());
			}
			ToolError.reportError(ErrorMessage.IMPORT_CYCLE, allSCCs.toString(), transform);
		}
		return transform;
	}

	private int index = 0;
	private Stack<SCCNode> stack = new Stack<>();
	private HashSet<SCCNode> stackSet = new HashSet<>();
	private List<List<SCCNode>> TarjansSCCCheck(Map<TypedAST, SCCNode> graph) {
		List<List<SCCNode>> sccs = new LinkedList<>();
		Collection<SCCNode> V = graph.values();
		for (SCCNode v : V) {
			if (v.index == -1)
				strongConnect(v, sccs);
		}
		return sccs;
	}

	private void strongConnect(SCCNode v, List<List<SCCNode>> sccs) {
		// Set the depth index for v to the smallest unused index
		v.index = index;
		v.lowlink = index++;
		stack.push(v);
		stackSet.add(v);

		// Consider successors of v
		for (Node<SCCNode> n : v.getLinks()) {
			SCCNode w = (SCCNode)n;
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
			List<SCCNode> SCC = new LinkedList<>();
			SCCNode node = null;
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
