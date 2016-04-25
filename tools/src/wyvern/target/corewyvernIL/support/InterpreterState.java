package wyvern.target.corewyvernIL.support;

import java.io.File;

public class InterpreterState {
	private ModuleResolver resolver;
	private GenContext genCtx;
	
	public InterpreterState(File rootDir, File libDir) {
		resolver = new ModuleResolver(rootDir, libDir);
		resolver.setInterpreterState(this);
	}
	
	public ModuleResolver getResolver() {
		return resolver;
	}

	public GenContext getGenContext() {
		return genCtx;
	}
	public void setGenContext(GenContext ctx) {
		if (genCtx != null)
			throw new RuntimeException();
		genCtx = ctx;
	}
}
