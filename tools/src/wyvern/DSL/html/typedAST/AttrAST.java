package wyvern.DSL.html.typedAST;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import wyvern.DSL.html.parsing.HtmlTagParser;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class AttrAST implements TypedAST {
	
	private HashMap<String, TypedAST> attrs;
	private FileLocation loc;

	public AttrAST(Map<String,TypedAST> attrs, FileLocation loc) {
		this.attrs = new HashMap<>(attrs);
		this.loc = loc;
	}
	
	public TypedAST getVal(TypedAST orig) {
		TypedAST out = orig;
		for (Entry<String, TypedAST> elems  : attrs.entrySet()) {
			out = new Invocation(
					(out==null)?new StringConstant(""):out, 
					"+", 
					new Invocation(
							new StringConstant(" "+elems.getKey()+"="),
							"+",
							HtmlTagParser.enquote(elems.getValue()),
							elems.getValue().getLocation()),
							elems.getValue().getLocation()
			);
		}
		
		return out;
	}
	
	public HashMap<String,TypedAST> getAttrs() {
		return attrs;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		throw new RuntimeException();
	}

	@Override
	public FileLocation getLocation() {
		return this.loc;
	}

	@Override
	public Type getType() {
		throw new RuntimeException();
	}

	@Override
	public Type typecheck(Environment env) {
		throw new RuntimeException();
	}

	@Override
	public Value evaluate(Environment env) {
		throw new RuntimeException();
	}

	@Override
	public LineParser getLineParser() {
		throw new RuntimeException();
	}

	@Override
	public LineSequenceParser getLineSequenceParser() {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return attrs;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new AttrAST(newChildren, loc);
	}

}
