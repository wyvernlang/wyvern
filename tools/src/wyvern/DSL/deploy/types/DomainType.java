package wyvern.DSL.deploy.types;

import wyvern.DSL.deploy.typedAST.architecture.properties.DomainProperty;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;

public class DomainType implements Type {

	private DomainProperty source;

	public DomainType(DomainProperty source) {
		this.source = source;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return false;
	}

	@Override
	public boolean subtype(Type other) {
		return false;
	}

    @Override
    public LineParser getParser() {
        return null;
    }

    @Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	public DomainProperty getDomain() {
		return source;
	}
	@Override
	public boolean isSimple() {
		// TODO Auto-generated method stub
		return true;
	}
}
