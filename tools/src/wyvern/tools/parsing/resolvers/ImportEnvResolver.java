package wyvern.tools.parsing.resolvers;

import wyvern.tools.parsing.ImportResolver;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Reference;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Ben Chung on 12/1/13.
 */
public interface ImportEnvResolver extends ImportResolver {
	Environment doExtend(Environment old, String src, Reference<TypedAST> typedAST);
	Environment extendWithValue(Environment old, TypedAST typedAST);
	void evalDecl(Environment evalEnv, Environment declEnv, TypedAST typedAST);
}
