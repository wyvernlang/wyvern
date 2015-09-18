package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.objects.JavaObj;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Ben Chung on 10/21/13.
 */
public class JavaField extends Declaration {
    private NameBinding nameBinding;
    private final Field src;
    private final MethodHandle getter;
    private final Optional<MethodHandle> setter;
	private boolean isClass;

    public JavaField(Field src, MethodHandle getter, Optional<MethodHandle> setter) {
        this.src = src;
        this.getter = getter;
        this.setter = setter;
		this.isClass = Modifier.isStatic(src.getModifiers());

		//Wyvern specific
        nameBinding = new NameBindingImpl(src.getName(), Util.javaToWyvType(src.getType()));
    }

    @Override
    public String getName() {
        return src.getName();
    }

    @Override
    protected Type doTypecheck(Environment env) {
        return Util.javaToWyvType(src.getType());
    }

    @Override
    protected Environment doExtend(Environment old, Environment against) {
        Environment newEnv = old;
        return newEnv;
    }

	private class JavaFieldValueBinding extends ValueBinding {

		public JavaFieldValueBinding(String name, Type type) {
			super(name, type);
		}

		@Override
		public Value getValue(EvaluationEnvironment env) {
			Object value = null;
			try {
				if (Modifier.isStatic(src.getModifiers())) {
					value = src.get(null);
				} else {
					value = src.get(((JavaObj) env.lookup("this").get().getValue(env)).getObj());
				}
				return Util.toWyvObj(value);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}

		}
	}

    @Override
    public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		EvaluationEnvironment newEnv = old.extend(new JavaFieldValueBinding(nameBinding.getName(), nameBinding.getType()));
        return newEnv;
    }


	private boolean binding = false;
    @Override
    public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		//Not actually needed.
    }

    @Override
    public Type getType() {
        return Util.javaToWyvType(src.getType());
    }

	@Override
	public Map<String, TypedAST> getChildren() {
		return new HashMap<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return this;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new WyvernException("Cannot generate IL for a Java field", this);
    }

    @Override
    public FileLocation getLocation() {
        return FileLocation.UNKNOWN;
    }

    @Override
    public void writeArgsToTree(TreeWriter writer) {

    }

	@Override
	public Environment extendType(Environment env, Environment against) {
		return env;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		return env.extend(nameBinding);
	}

	@Override
	public boolean isClassMember() {
		return isClass;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeclType genILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
