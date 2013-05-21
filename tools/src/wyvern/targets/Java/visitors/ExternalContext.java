package wyvern.targets.Java.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;

public class ExternalContext {
	private HashMap<String, Pair<Integer,Type>> variableMap = new HashMap<String, Pair<Integer,Type>>();
	
	public static int EXTERNAL = 0;
	public static int INTERNAL = 1;
	
	public void setVariables(Iterable<Pair<String, Type>> list, int declLoc) {
		for (Pair<String, Type> s : list)
			variableMap.put(s.first, new Pair<Integer,Type>(declLoc,s.second));
	}
	
	public void setVariable(String varName, Type type, int declarationLocation) {
		variableMap.put(varName, new Pair<Integer,Type>(declarationLocation, type));
	}
	
	public Pair<Integer,Type> getVariable(String varName) {
		return variableMap.get(varName);
	}
	
	public Iterable<Pair<String,Type>> getExternalDecls() {
		ArrayList<Pair<String,Type>> exDecls = new ArrayList<Pair<String,Type>>();
		for (Entry<String, Pair<Integer, Type>> entry : variableMap.entrySet()) {
			if (entry.getValue().first == INTERNAL)
				continue;
			exDecls.add(new Pair<String,Type>(entry.getKey(), entry.getValue().second));
		}
		return exDecls;
	}
	
	public Iterable<Pair<String, Type>> getDecls() {
		ArrayList<Pair<String,Type>> exDecls = new ArrayList<Pair<String,Type>>();
		for (Entry<String, Pair<Integer, Type>> entry : variableMap.entrySet()) {
			exDecls.add(new Pair<String,Type>(entry.getKey(), entry.getValue().second));
		}
		return exDecls;
	}

}
