package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;
import wyvern.target.corewyvernIL.type.ValueType;

public class Rational extends Expression implements Value {
	
	private String numerator;
	private String denominator;
	
	public Rational(String numerator, String denominator) {
		super();
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public String getNumerator() {
		return numerator;
	}
	
	public void setNumerator(String numerator) {
		this.numerator = numerator;
	}
	
	public String getDenominator() {
		return denominator;
	}
	
	public void setDenominator(String denominator) {
		this.denominator = denominator;
	}

	@Override
	public java.lang.String acceptEmitILVisitor(EmitILVisitor emitILVisitor,
			Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType typeCheck(wyvern.tools.types.Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}
