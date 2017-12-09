package wyvern.tools.imports;

import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

public interface ImportBinder {
	//Evaluation
	public EvaluationEnvironment extendVal(EvaluationEnvironment env);
	public EvaluationEnvironment bindVal(EvaluationEnvironment env);
}
