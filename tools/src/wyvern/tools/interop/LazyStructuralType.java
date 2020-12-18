package wyvern.tools.interop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import wyvern.stdlib.support.Pure;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.support.ReceiverView;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class LazyStructuralType extends StructuralType {
    private Class<?> javaClass;
    private TypeContext ctx;
    private boolean initialized = false;
    private boolean safe;

    /**
     * @param safe if safe==true, then javaClass doesn't contain mutable state or access system resources
     *             If !safe, all methods in javaClass should be annotated with {system.FFI}
     */
    public LazyStructuralType(Class<?> javaClass, TypeContext ctx, boolean safe) {
        super("java_type", new ArrayList<DeclType>());
        this.javaClass = javaClass;
        this.ctx = ctx;
        this.safe = safe;
    }

    public List<DeclType> getDeclTypes() {
        if (!initialized) {
            fillOutType();
            initialized = true;
        }
        return super.getDeclTypes();
    }

    private void fillOutType() {
        List<DeclType> newDeclTypes = new ArrayList<DeclType>();
        // for each method in javaClass, attempt to convert argument types
        // if we fail, we just leave out that method
        nextMethod: for (Method m : javaClass.getMethods()) {

            // Exclude static methods; they are part of the class, not the object
            // If we want to support calling static methods, in Wyvern we should call them on the class, not on one of its instances
            if (Modifier.isStatic(m.getModifiers())) {
                continue;
            }

            ValueType retType = GenUtil.javaClassToWyvernType(m.getReturnType(), ctx);
            if (retType == null) {
                continue;
            }
            List<FormalArg> argTypes = new LinkedList<FormalArg>();
            Class<?>[] argClasses = m.getParameterTypes();
            for (int i = 0; i < argClasses.length; ++i) {
                ValueType t = GenUtil.javaClassToWyvernType(argClasses[i], ctx);
                if (t == null) {
                    continue nextMethod;
                }
                argTypes.add(new FormalArg(m.getParameters()[i].getName(), t));
            }

            if (safe || m.getAnnotationsByType(Pure.class).length > 0) {
                newDeclTypes.add(new DefDeclType(m.getName(), retType, argTypes));
            } else if (m.getAnnotationsByType(wyvern.stdlib.support.Effect.class).length > 0) {
                wyvern.stdlib.support.Effect[] effects = m.getAnnotationsByType(wyvern.stdlib.support.Effect.class);
                String effect1path = effects[0].value()[0];
                String[] parts = effect1path.split("\\.");
                String modulePath = String.join(".", Arrays.copyOf(parts, parts.length -1));

                Effect effect = new Effect(new Variable("MOD$" + modulePath), parts[parts.length-1], null);

                DefDeclType methodDecl = new DefDeclType(m.getName(), retType, argTypes, new EffectSet(effect));

                newDeclTypes.add(methodDecl);
            } else {
                String effectName = m.getName() + "Effect";
                EffectDeclType ffiEffect = new EffectDeclType(effectName, null, null);
                EffectSet set = new EffectSet(new Effect(new Variable(this.getSelfSite()), effectName, null));
                newDeclTypes.add(ffiEffect);
                DefDeclType methodDecl = new DefDeclType(m.getName(), retType, argTypes, set);
                newDeclTypes.add(methodDecl);
            }
        }

        // TODO: extend to fields

        // update declTypes
        setDeclTypes(newDeclTypes);
    }

}
