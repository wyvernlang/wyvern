package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.FileLocation;

public class StringLiteral extends Literal {

	private java.lang.String value;

	public StringLiteral(java.lang.String value) {
		this(value, FileLocation.UNKNOWN);
	}
	
	public StringLiteral(java.lang.String value, FileLocation loc) {
		super(Util.stringType(), loc);
		this.value = value;
	}

	public java.lang.String getValue() {
		return value;
	}

	public void setValue(java.lang.String value) {
		this.value = value;
	}

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(this.getClass() != obj.getClass()) {
            return false;
        }
        try {
            StringLiteral other = (StringLiteral) obj;
            return this.getValue().equals(other.getValue());
        } catch(ClassCastException e) {
            return false;
        }
    }

	@Override
	public ValueType typeCheck(TypeContext env) {
        return Util.stringType();
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append('"').append(value).append('"');
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	@Override
	public Set<String> getFreeVariables() {
		return new HashSet<>();
	}


	@Override
	public ValueType getType() {
		return Util.stringType();
	}
}
