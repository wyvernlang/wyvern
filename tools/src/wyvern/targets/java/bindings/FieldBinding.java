package wyvern.targets.java.bindings;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class FieldBinding implements JavaBinding {
	private String realname;

	public FieldBinding(String realname, Type javaType) {
		this.realname = realname;

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void writeGetValue(InsnList toProduce) {

	}
}
