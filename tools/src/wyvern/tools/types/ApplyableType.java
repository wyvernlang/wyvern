package wyvern.tools.types;

import wyvern.tools.typedAST.Application;

public interface ApplyableType extends Type {
	/**
	 * The Applyable is the "function" part of the application (it need not actually be a function).
	 * The Applyable is asked to typecheck the entire application, including the "argument" part of
	 * the application.
	 * 
	 * @param application
	 * @param context
	 * @param linearContext
	 * @return
	 */
	Type checkApplication(Application application);
}
