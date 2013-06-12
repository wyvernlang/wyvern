package wyvern.DSL.deploy.types;

import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;

public class ConnectionType implements Type {

	private final DomainType domain;
	private final Type viaType;
	private String name;
	private Arrow connSig;
	public ConnectionType(String name, Arrow conn) {
		this(name, conn, null, null);
	}

	public ConnectionType(String name, Arrow arrow, DomainType domain, Type viaType) {
		this.name = name;
		this.connSig = arrow;
		this.domain = domain;
		this.viaType = viaType;
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
	public void writeArgsToTree(TreeWriter writer) {
	}

	public Arrow getArrow() {
		return connSig;
	}
	@Override
	public boolean isSimple() {
		// TODO Auto-generated method stub
		return false;
	}
}
