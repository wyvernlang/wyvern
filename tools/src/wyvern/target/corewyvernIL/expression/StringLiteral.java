package wyvern.target.corewyvernIL.expression;

import java.io.IOException;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.corewyvernIL.support.Util;

public class StringLiteral extends Literal {
	
	private java.lang.String value;
	
	public StringLiteral(java.lang.String value) {
		super(null);
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

    public boolean equals(String s) {
        return this.value.equals(s);
    }

    public boolean equals(StringLiteral s) {
        return this.value.equals(s.getValue());
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
            return value.equals(other);
        } catch(ClassCastException e) {
            return false;
        }
        //return true; // TODO should this not be 'false' by default??
    }

    @Override
    public String toString() {
        return this.value.toString();
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
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
