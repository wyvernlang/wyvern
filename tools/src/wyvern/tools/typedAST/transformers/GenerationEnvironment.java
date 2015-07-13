package wyvern.tools.typedAST.transformers;

import wyvern.target.corewyvernIL.expression.*;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.WyvernException;

import java.util.HashMap;
import java.util.Optional;

public class GenerationEnvironment {
    private static int gen = 0;
    public static String generateVariableName() {
        return (gen++) + "gen";
    }

    private Optional<Path> basis = Optional.empty();
    private Optional<GenerationEnvironment> parent = Optional.empty();
    private HashMap<String, Path> mapping = new HashMap<>();

    public GenerationEnvironment() {} //Root context
    public GenerationEnvironment(GenerationEnvironment parent, String name) { // Child (in-context) context
        basis = Optional.of(Optional.ofNullable(parent).flatMap(p -> p.basis)
                .<Path>map(p -> new FieldGet((Expression) p, name))
                .orElseGet(() -> new Variable(name)));
        this.parent = Optional.ofNullable(parent);
    }
    public GenerationEnvironment(GenerationEnvironment parent) { // Child (out-of-context) context
        this.parent = Optional.of(parent);
    }

    private Optional<String> getName() {
        return basis.map(b->{
            if (b instanceof FieldGet) {
                return ((FieldGet)b).getFieldName();
            } else {
                return ((Variable)b).getName();
            }
        });
    }

    private Path addFirst(String newVar, Path oldpath) {
        if (oldpath instanceof FieldGet) {
            return new FieldGet((Expression)addFirst(newVar, (Path)((FieldGet) oldpath).getObjectExpr()), ((FieldGet) oldpath).getFieldName());
        } else {
            Variable old = (Variable)oldpath;
            return new FieldGet(new Variable(newVar), old.getName());
        }
    }

    private void register(String vname, Path nb) {
        getName().ifPresent(name -> parent.ifPresent(p -> p.register(vname, addFirst(name, nb))));
        mapping.put(vname, nb);
    }

    public void register(String vname, ValueType type) {
        getName().ifPresent(name -> parent.ifPresent(p -> p.register(vname, new FieldGet(new Variable(name), vname))));
        mapping.put(vname, basis.<Path>map(b->new FieldGet((Expression)b, vname)).orElse(new Variable(vname)));
    }

    public Path lookup(String vname) {
        if (mapping.containsKey(vname))
            return mapping.get(vname);
        return parent.map(p->p.lookup(vname)).orElse(null);
    }

    public Variable lookupVar(String vname, ILWriter writer) {
        Path lookup = lookup(vname);
        if (lookup instanceof Variable) {
            return (Variable) lookup;
        } else if (lookup != null) {
            String nvname = generateVariableName();
            writer.wrap(outer -> new Let(nvname, (Expression)lookup, (Expression)outer));
            return new Variable(nvname);
        }
        throw new WyvernException("Variable not found in codegen.");
    }
}
