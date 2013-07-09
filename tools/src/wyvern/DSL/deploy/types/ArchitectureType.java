package wyvern.DSL.deploy.types;

import wyvern.DSL.deploy.typedAST.architecture.Architecture;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;

public class ArchitectureType implements Type {
	private Architecture definition;

	public ArchitectureType(Architecture definition) {

		this.definition = definition;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean subtype(Type other) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

    @Override
    public LineParser getParser() {
        return null;
    }

    @Override
	public void writeArgsToTree(TreeWriter writer) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
	@Override
	public boolean isSimple() {
		// TODO Auto-generated method stub
		return true;
	}
}
