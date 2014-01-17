package wyvern.targets;


import java.util.HashMap;
import java.util.Map;

public class TargetManager {
	private static Map<String, Target> targets = new HashMap<>();
	static {
	}

	public Target getTarget(String targetName) {
		return targets.get(targetName);
	}
}
