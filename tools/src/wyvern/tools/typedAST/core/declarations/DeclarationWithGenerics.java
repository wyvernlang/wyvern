package wyvern.tools.typedAST.core.declarations;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.generics.GenericParameter;
import wyvern.tools.typedAST.abs.Declaration;

public abstract class DeclarationWithGenerics extends Declaration {
    protected List<GenericParameter> generics;
    protected LinkedList<BindingSite> genericSites = new LinkedList<BindingSite>();

    public static final String GENERIC_PREFIX = "__generic__";
    public static final String GENERIC_MEMBER = "T";

    /**
     * Adds a list of generic parameters to a list of formal arguments and updates the entries of an array of contexts
     * with this new information.
     *
     * @param contexts The contexts to update (modified by method)
     * @param formalArgs The list of formal arguments to which the new generic parameter arguments will be added (modified by method)
     * @param generics The generic parameters to be added to the list of formal arguments (not modified by method)
     */
    public void addGenericParameters(
            GenContext[] contexts, List<FormalArg> formalArgs) {
        if (genericSites.size() == 0) {
            for (GenericParameter gp : generics) {
                String s = gp.getName();
                String genName = GENERIC_PREFIX + s;
                BindingSite argSite = new BindingSite(genName);
                genericSites.addLast(argSite);
            }
        }
        int j = 0;
        for (GenericParameter gp : generics) {
            String s = gp.getName();

            String genName = GENERIC_PREFIX + s;
            BindingSite argSite = genericSites.get(j);
            //BindingSite argSite =  ? genericnew BindingSite(genName);
            ValueType type = DefDeclaration.genericStructuralType(s, argSite, gp.getKind());
            formalArgs.add(new FormalArg(argSite, type));

            for (int i = 0; i < contexts.length; i++) {
                contexts[i] = contexts[i].extend(argSite, new Variable(argSite), type);
                contexts[i] = new TypeOrEffectGenContext(s, argSite, contexts[i]);
            }
            j++;
        }
    }


}
