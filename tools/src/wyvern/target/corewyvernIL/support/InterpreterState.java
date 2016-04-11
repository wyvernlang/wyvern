package wyvern.target.corewyvernIL.support;

import java.io.File;

public class InterpreterState {
	private ModuleResolver resolver;
	
	public InterpreterState(File rootDir) {
		resolver = new ModuleResolver(rootDir);
		resolver.setInterpreterState(this);
	}
	
	public ModuleResolver getResolver() {
		return resolver;
	}
}
