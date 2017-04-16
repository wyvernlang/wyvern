package wyvern.stdlib.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wyvern.tools.tests.TestUtil;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.expression.Literal;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.typedAST.interfaces.ExpressionAST;

public class AST {
	public static AST utils = new AST();
	
	public ValueType intType() {
		return Util.intType();
	}

    public ValueType dynType() {
        return Util.dynType();
	}

	public Expression intLiteral(int i) {
		return new IntegerLiteral(i);
	}
	
	public Value stringLiteral(String s) {
		return new StringLiteral(s);
	}
	
	public Expression variable(String s) {
		return new Variable(s);
	}
	
	public Expression oneArgCall(ObjectValue receiver, String methodName, ObjectValue argument) {
		return new MethodCall(getExpr(receiver), methodName, Arrays.asList(getExpr(argument)), null);
	}

    public Expression object(List<ObjectValue> decls) {
        List<NamedDeclaration> javaDecls = new LinkedList<>();
        FileLocation loc = null;
        for (ObjectValue decl : decls) {
            JavaValue fieldValue = (JavaValue) decl.getField("decl");
            javaDecls.add((NamedDeclaration)fieldValue.getWrappedValue());
            loc = decl.getLocation();
        }
        return new New(javaDecls, loc);
    }
	
	public Expression oneDeclObject(ObjectValue decl) {
		final JavaValue fieldValue = (JavaValue) decl.getField("decl");
		NamedDeclaration realDecl = (NamedDeclaration) fieldValue.getWrappedValue();
		return new New(realDecl);
	}

    public Expression twoDeclObject(ObjectValue decl1, ObjectValue decl2) {
        final JavaValue fieldValue1 = (JavaValue) decl1.getField("decl");
        NamedDeclaration realDecl1 = (NamedDeclaration) fieldValue1.getWrappedValue();
        final JavaValue fieldValue2 = (JavaValue) decl2.getField("decl");
        NamedDeclaration realDecl2 = (NamedDeclaration) fieldValue2.getWrappedValue();
        return new New(realDecl1, realDecl2);
    }
	
	private Expression getExpr(ObjectValue wyvernAST) {
		final Value ast = wyvernAST.getField("ast");
		if (ast instanceof JavaValue) {
			final JavaValue value = (JavaValue) ast;
			return (Expression) value.getWrappedValue();
		} else if (ast instanceof Literal) {
			return (Expression) ast;
		} else {
			throw new RuntimeException("unexpected!");
		}
	}
	
	public DefDeclaration OneArgDefn(String name, ObjectValue resultType, ObjectValue body) {
		final JavaValue fieldValue = (JavaValue) resultType.getField("typ");
		ValueType realType= (ValueType) fieldValue.getWrappedValue();
		Expression realBody = getExpr(body);
		return new DefDeclaration(name, new LinkedList<FormalArg>(), realType, realBody, null);
	}

    public IExpr parseExpression(String input, JavaValue context) throws ParseException {
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input + "\n", "TSL Parse");
        GenContext cxt = (GenContext)context.getFObject().getWrappedValue();
        // Extend parseTSL with a second argument (abstract type representing context)
        // TODO: Handle InterpreterState/GenContext
        return ast.generateIL(cxt, null, null);
    }

    private String commonPrefix(String s1, String s2) {
        if (s1 == null) return s2;
        if (s2 == null) return s1;

        int minLen = Math.min(s1.length(), s2.length());
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < minLen; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                result.append(s1.charAt(i));
            } else {
                break;
            }
        }
        return result.toString();
    }

    public String stripLeadingWhitespace(String input) throws IOException {
        // Remove the least common whitespace prefix from all lines in [input]
        String toStrip = null;
        String line = null;
        BufferedReader bufReader = new BufferedReader(new StringReader(input));
        Pattern p = Pattern.compile("^(\\s+).+");
        while ((line = bufReader.readLine()) != null) {
        	Matcher m = p.matcher(line);
        	String leadingWhitespace;
        	if (m.matches()) {
        		leadingWhitespace = m.group(1);
        	} else {
        		leadingWhitespace = "";
        	}
            toStrip = commonPrefix(leadingWhitespace, toStrip);
        }
        bufReader = new BufferedReader(new StringReader(input));
        StringBuilder result = new StringBuilder();
        while ((line = bufReader.readLine()) != null) {
            result.append(line.substring(toStrip.length()));
            result.append("\n");
        }
        return result.toString();
    }
}
