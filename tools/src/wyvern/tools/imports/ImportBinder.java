package wyvern.tools.imports;

import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

public interface ImportBinder {
	//Typechecking
	public Environment extendTypes(Environment in);
	public Environment extendNames(Environment in);
	public Environment extend(Environment in);
	public Type typecheck(Environment env);
}
