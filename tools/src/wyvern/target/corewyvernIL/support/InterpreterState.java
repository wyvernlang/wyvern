package wyvern.target.corewyvernIL.support;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;

import java.io.File;
import java.util.ArrayList;

public class InterpreterState {
    private static final ThreadLocal<InterpreterState> localInterpreter = new ThreadLocal<InterpreterState>() {
        @Override protected InterpreterState initialValue() {
            return null;
        }
    };

    private ModuleResolver resolver;
    private GenContext genCtx;

    private ArrayList<BytecodeOuterClass.Bytecode.Import> javascriptFFIImports = new ArrayList<>();

    public ArrayList<BytecodeOuterClass.Bytecode.Import> getJavascriptFFIImports() {
        return javascriptFFIImports;
    }


    public static final String PLATFORM_PYTHON = "python";
    public static final String PLATFORM_JAVA = "java";
    public static final String PLATFORM_JAVASCRIPT = "javascript";

    public InterpreterState(String platform, File rootDir, File libDir) {
        resolver = new ModuleResolver(platform, rootDir, libDir);
        resolver.setInterpreterState(this);
        localInterpreter.set(this);
    }

    public ModuleResolver getResolver() {
        return resolver;
    }

    public GenContext getGenContext() {
        return genCtx;
    }
    public void setGenContext(GenContext ctx) {
        if (genCtx != null) {
            throw new RuntimeException();
        }
        genCtx = ctx;
    }

    /** Get the interpreter state for this thread.
     * Admittedly this is slightly hacky, but it's a way for code to get the surrounding
     * interpreter if it isn't passed in explicitly.
     *
     * @return
     */
    public static InterpreterState getLocalThreadInterpreter() {
        return localInterpreter.get();
    }

    public static void resetThreadLocalInterpreter() {
        localInterpreter.set(null);
    }

    public void addModuleAST(String moduleName, ModuleDeclaration newAST) {
        this.resolver.addModuleAST(moduleName, newAST);
    }
}
