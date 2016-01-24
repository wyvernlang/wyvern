package wyvern.target.corewyvernIL.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class LazyStructuralType extends StructuralType {

	private Class<?> javaClass;
	private TypeContext ctx;

	public LazyStructuralType(Class<?> javaClass, TypeContext ctx) {
		super("java_type", new ArrayList<DeclType>());
		this.javaClass = javaClass;
		this.ctx = ctx;
	}

	public List<DeclType> getDeclTypes() {
		fillOutType();
		return super.getDeclTypes();
	}

	private void fillOutType() {
		List<DeclType> declTypes = super.getDeclTypes();
		// for each method in javaClass, attempt to convert argument types
		// if we fail, we just leave out that method
		nextMethod: for (Method m : javaClass.getMethods()) {
			
			ValueType retType = GenUtil.javaClassToWyvernType(m.getReturnType(), ctx);
			if (retType == null)
				continue;
			List<FormalArg> argTypes = new LinkedList<FormalArg>();
			Class<?> argClasses[] = m.getParameterTypes(); 
			for (int i = 0; i < argClasses.length; ++i) {
				ValueType t = GenUtil.javaClassToWyvernType(argClasses[i], ctx);
				if (t == null)
					continue nextMethod;
				argTypes.add(new FormalArg(m.getParameters()[i].getName(), t));
			}
			declTypes.add(new DefDeclType(m.getName(), retType, argTypes));
		}
		
		// TODO: extend to fields
	}
	
}
