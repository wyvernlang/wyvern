package wyvern.stdlib.support.verifier;

import java.io.FileOutputStream;
import java.io.IOException;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.MessageLite;


public class ASTWrapper {
    public static final ASTWrapper ast = new ASTWrapper();
    
    public AST.Program.Builder newProgramBuilder() {
        return AST.Program.newBuilder();
    }
    
    public Object oper(String name) {
        switch (name) {
        case "+": return AST.Expop.Plus;
        case "-": return AST.Expop.Minus;
        case "*": return AST.Expop.Times;
        case "/": return AST.Expop.Div;
        case "!=": return AST.Cmpop.NEQ;
        case "==": return AST.Cmpop.EQ;
        case "<": return AST.Cmpop.LT;
        case ">": return AST.Cmpop.GT;
        case "<=": return AST.Cmpop.LE;
        case ">=": return AST.Cmpop.GE;
        default: throw new RuntimeException("bad operator " + name);
        }
    }
    
    public void writeAST(String filename, MessageLite program) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            CodedOutputStream outputStream = CodedOutputStream.newInstance(fileOutputStream);
            System.out.println("Writing:");
            System.out.println(program.toString());
            program.writeTo(outputStream);
            outputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
