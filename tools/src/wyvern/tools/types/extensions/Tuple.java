package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.*;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Tuple extends AbstractTypeImpl implements OperatableType, TypeResolver.Resolvable {
	private Type[] types;
	
	public Tuple(Type[] types) {
		this.types = types;
	}

	public Tuple(Type first, Type last) {
		types = new Type[] {first, last};
	}
	
	public Tuple(List<NameBinding> bindings) {
		this.types = new Type[bindings.size()];
		for (int i = 0; i < bindings.size(); i++) {
			this.types[i] = bindings.get(i).getType();
		}
	}

	public Type[] getTypeArray() {
		return types;
	}


	@Override
	public Map<String, Type> getTypes() {
		HashMap<String, Type> typesMap = new HashMap<>();
		int idx = 0;
		for (Type t : types)
			typesMap.put(idx++ +"", t);
		return typesMap;
	}

	@Override
	public Type setTypes(Map<String, Type> newTypes) {
		Type[] res = new Type[newTypes.size()];
		for (Map.Entry<String,Type> entry : newTypes.entrySet()) {
			res[Integer.parseInt(entry.getKey())] = entry.getValue();
		}
		return new Tuple(res);
	}

	public Type getFirst() {
        return types[0];
    }

    public boolean isEmpty() {
        return types.length > 0;
    }

    public Tuple getRest() {
        Type[] newT = new Type[types.length-1];
        for (int i = 1; i < types.length; i++)
            newT[i-1] = types[i];
        return new Tuple(newT);
    }
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(types);	
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(types.length + 2);
		
		if (types.length > 1) {
			if (!types[0].isSimple())
				builder.append('(');
			builder.append(types[0].toString());
			if (!types[0].isSimple())
				builder.append(')');
		}
		for (int i = 1; i < types.length; i++) {
			builder.append('*');
			if (!types[i].isSimple())
				builder.append('(');
			builder.append(types[i].toString());
			if (!types[i].isSimple())
				builder.append(')');
		}
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object otherT) {
		if (!(otherT instanceof Tuple))
			return false;
		
		if (((Tuple)otherT).types.length != types.length)
			return false;
		
		for (int i = 0; i < types.length; i++) {
			if (!(((Tuple)otherT).types[i].equals(types[i])))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 23;
		for (Type type : types)
			hash = hash*37 + type.hashCode();
		
		return hash;
	}	

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		// FIXME: Implement S-RcdWidth, S-RcdDepth, and S-RcdPerm I suppose. (Ben: This is factually wrong)
        if (other == this)
            return true;



        if (!(other instanceof Tuple))
            return false;

        Tuple otherTuple = (Tuple)other;

        //n+k = types.length
        //n = otherTuple.types.length
        if (types.length != otherTuple.types.length) // n+k != n
            return false;
        //=>k=0=>n+k=n

        boolean sat = true;
        for (int i = 0; i < otherTuple.types.length && sat; i++) {
            Type Si = types[i];
            Type Ti = otherTuple.types[i];
            if (!Si.subtype(Ti)) // S_i <: T_i
                sat = false;
        }
        return sat;
	}
	
	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		String name = opExp.getOperationName();
		if (name.length() < 2)
			ToolError.reportError(ErrorMessage.CANNOT_INVOKE, opExp.getLocation());
		if (!name.startsWith("n"))
			ToolError.reportError(ErrorMessage.CANNOT_INVOKE, opExp.getLocation());
		int num = Integer.valueOf(name.substring(1));
		if (num >= types.length)
			ToolError.reportError(ErrorMessage.CANNOT_INVOKE, opExp.getLocation());
		return types[num];
	}
	@Override
	public Map<String, Type> getChildren() {
		HashMap<String, Type> map = new HashMap<>();
		for (int i = 0; i < types.length; i++) {
			map.put(i +"", types[i]);
		}
		return map;
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		Type[] result = new Type[types.length];
		for (int i = 0; i < types.length; i++) {
			result[i] = newChildren.get(i + "");
		}
		return new Tuple(result);
	}

    @Override
    public ValueType generateILType() {
        throw new WyvernException("Tuple type unimplemented", FileLocation.UNKNOWN); //TODO
    }

	@Override
	public ValueType getILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}