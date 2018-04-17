package wyvern.tools;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.astvisitor.PlatformSpecializationVisitor;
import wyvern.target.corewyvernIL.astvisitor.TailCallVisitor;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.tools.errors.ToolError;

public class REPL {
	
	
	public REPL() {
		
	}
	
	public void parseVar(String code) {
		
	}
	
	
	public void RunCode(String args) {
        try {
            String rootLoc = System.getenv("WYVERN_ROOT");
            if (wyvernRoot.get() != null) {
                rootLoc = wyvernRoot.get();
            } else {
                rootLoc = System.getProperty("user.dir");
            }
            File rootDir = new File(rootLoc);
            String wyvernPath = System.getenv("WYVERN_HOME");
            if (wyvernPath == null) {
                if (wyvernHome.get() != null) {
                    wyvernPath = wyvernHome.get();
                } else {
                    System.err.println("must set WYVERN_HOME environmental variable to wyvern project directory");
                    return;
                }
            }
            }catch (ToolError e) {
            System.err.println(e.getMessage());
        }
    }
	
	 // used to set WYVERN_HOME when called programmatically
    public static final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();

    // used to set WYVERN_ROOT when called programmatically
    public static final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();
}
