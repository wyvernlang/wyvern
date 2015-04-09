package wyvern.tools.imports;

import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

public interface ImportBinder {
	//Typechecking
	public Environment extendTypes(Environment in);
	public Environment extendNames(Environment in);
	public Environment extend(Environment in);
	public Type typecheck(Environment env);

	//Evaluation
	public EvaluationEnvironment extendVal(EvaluationEnvironment env);
	public EvaluationEnvironment bindVal(EvaluationEnvironment env);
}
