package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.objects.JavaObj;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import javax.tools.Tool;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Ben Chung on 10/21/13.
 */
public class JavaField extends Declaration {
    private NameBinding nameBinding;
    private final Field src;
    private final MethodHandle getter;
    private final MethodHandle setter;

    public JavaField(Field src, MethodHandle getter, MethodHandle setter) {
        this.src = src;
        this.getter = getter;
        this.setter = setter;

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
    protected Environment doExtend(Environment old) {
        Environment newEnv = old.extend(nameBinding);
        return newEnv;
    }

    @Override
    public Environment extendWithValue(Environment old) {
        Environment newEnv = old.extend(new ValueBinding(nameBinding.getName(), nameBinding.getType()));
        return newEnv;
    }

    @Override
    public void evalDecl(Environment evalEnv, Environment declEnv) {
        ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
        try {
            Object value = null;
            if (Modifier.isStatic(src.getModifiers())) {
                value = src.get(null);
            } else {
                value = src.get(((JavaObj)evalEnv.lookup("this").getValue(evalEnv)).getObj());
            }
            vb.setValue(Util.toWyvObj(value));
        } catch (Throwable t) {
            ToolError.reportError(ErrorMessage.JAVA_INVOCATION_ERROR, src.getName(), t.toString(), this);
        }
    }

    @Override
    public Type getType() {
        return Util.javaToWyvType(src.getType());
    }

    @Override
    public FileLocation getLocation() {
        return FileLocation.UNKNOWN;
    }

    @Override
    public void writeArgsToTree(TreeWriter writer) {

    }
}
