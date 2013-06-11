package wyvern.DSL.deploy.typedAST.architecture.properties;

import wyvern.DSL.deploy.typedAST.architecture.Connection;
import wyvern.DSL.deploy.types.ConnectionType;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class ConnectionProperty extends Declaration {
	private TypedAST body;
	private String bindingName;

	public ConnectionProperty(TypedAST body, String bindingName) {
		this.body = body;
		this.bindingName = bindingName;
	}

	protected void setBody(TypedAST newBody) {
		body = newBody;
	}

	public static List<ConnectionType> getConnections(TypedAST obj) {
		if (obj == null)
			return new ArrayList<>(0);
		else if (obj instanceof Connection) {
			ConnectionType type = (ConnectionType) obj.getType();
			List<ConnectionType> toReturn = new ArrayList<>(1);
			toReturn.add(type);
			return toReturn;
		} else if (obj instanceof ConnectionProperty ) {
			return getConnections(((ConnectionProperty)obj).body);
		} else if (obj instanceof Sequence) {
			ArrayList<ConnectionType> ret = new ArrayList<>(5);
			for (TypedAST decl : (Sequence)obj) {
				ret.addAll(getConnections(decl));
			}
			return ret;
		} else {
			return new ArrayList<>(0);
		}
	}

	protected void innerTypecheck(Environment env) {
		if (body != null)
			body.typecheck(env.extend(new TypeBinding(bindingName, getType())));
	}

	@Override
	protected Type doTypecheck(Environment env) {
		innerTypecheck(env);
		return getType();
	}

	@Override
	protected Environment doExtend(Environment old) {
		if (body == null)
			return old.extend(new TypeBinding(bindingName, getType()));
		else
			return (body instanceof Declaration)?((Declaration) body).extend(old):old;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return old;
	}


	@Override
	public String getName() {
		return bindingName;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
	}

	public Binding getBinding() {
		return new TypeBinding(bindingName, getType());
	}
}
