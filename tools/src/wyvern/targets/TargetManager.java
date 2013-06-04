package wyvern.targets;

import wyvern.targets.Java.Java;
import wyvern.targets.JavaScript.JavaScript;

import java.util.HashMap;
import java.util.Map;

public class TargetManager {
	private static Map<String, Target> targets = new HashMap<>();
	static {
		targets.put("JavaScript", new JavaScript());
		targets.put("Java", new Java());
	}

	public Target getTarget(String targetName) {
		return targets.get(targetName);
	}
}
