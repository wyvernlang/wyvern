package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.astvisitor.DeclTypeVisitor;
import wyvern.target.corewyvernIL.astvisitor.DeclarationVisitor;
import wyvern.target.corewyvernIL.astvisitor.TypeVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ForwardDeclaration;
import wyvern.target.corewyvernIL.decl.EffectDeclaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decl.RecConstructDeclaration;
import wyvern.target.corewyvernIL.decl.RecDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public final class QuantificationLifter {
    private static final String MONOMORPHIZED_EFFECT = "__MonomorphizedEffect__";

    private QuantificationLifter() {
    }

    /**
     * Applies the quantification lifting transformation to an expression, if possible
     *
     * @param ctx        The type context in which the expression appears
     * @param expression The expression to try to transform
     * @return The result of applying the quantification lifting transformation to the expression that was passed in, if
     * possible, or null if the transformation is not possible
     */
    public static New liftIfPossible(final GenContext ctx, final IExpr expression, boolean isLifted) {
        if (!(expression instanceof New)) {
            return null;
        }

        final New oldNew = (New) expression;
        final List<Declaration> oldDecls = oldNew.getDecls();

        if (oldDecls.size() != 1) {
            return null;
        }

        final Declaration oldDecl = oldDecls.get(0);

        if (!(oldDecl instanceof DefDeclaration) || !"apply".equals(oldDecl.getName())) {
            return null;
        }

        final DefDeclaration oldFunctor = (DefDeclaration) oldDecl;
        final List<FormalArg> oldFormalArgs = oldFunctor.getFormalArgs();

        //List<DeclType> firstDeclTypes = (oldFormalArgs.get(0).getType().getStructuralType(ctx).getDeclTypes());
        //System.out.println(firstDeclTypes);

        //List<DeclType> secondDeclTypes = (oldFormalArgs.get(1).getType().getStructuralType(ctx).getDeclTypes());
        //System.out.println(secondDeclTypes);

        if (oldFormalArgs.size() == 0) {
            return null;
        }

        final FormalArg oldFirstArg = oldFormalArgs.get(0);

        // We've already lifted
        if (oldFirstArg.getName().equals(
                wyvern.tools.typedAST.core.declarations.DefDeclaration.GENERIC_PREFIX + MONOMORPHIZED_EFFECT)) {
            return null;
        }

        if (!isLifted) {
            return null;
        }

        final DefDeclaration newFunctor = lift(ctx, oldFunctor);

        return new New(newFunctor, expression.getLocation());
    }




    /**
     * Lifts effect polymorphism from the return type of a functor to the functor itself.
     *
     * @param ctx     The type context in which the functor appears
     * @param functor The functor to lift
     * @return The result of applying the quantification lifting transformation to the functor that was passed in
     */
    private static DefDeclaration lift(final GenContext ctx, final DefDeclaration functor) {
        // Construct new formal (generic) argument for the effect polymorphism

        final String genericName = wyvern.tools.typedAST.core.declarations.DefDeclaration.GENERIC_PREFIX + MONOMORPHIZED_EFFECT;
        final BindingSite genericArgSite = new BindingSite(genericName);
        final List<FormalArg> oldFormalArgs = functor.getFormalArgs();

        // Construct effect bounds
        EffectSet lb = new EffectSet(new HashSet<>());
        EffectSet ub = new EffectSet(new HashSet<>());

        final ValueType boundedType = wyvern.tools.typedAST.core.declarations.DefDeclaration.boundedStructuralType(
                MONOMORPHIZED_EFFECT, genericArgSite, lb, ub
        );
        final FormalArg newFormalArg = new FormalArg(genericArgSite, boundedType);

        final Variable newFormalArgVariable = new Variable(newFormalArg.getSite());

        // Construct updated type context

        final GenContext tempCtx = ctx.extend(newFormalArg.getSite(), newFormalArgVariable, newFormalArg.getType());
        final TypeOrEffectGenContext newCtx = new TypeOrEffectGenContext(MONOMORPHIZED_EFFECT, newFormalArg.getSite(), tempCtx);

        // Construct effect to parametrize over

        final Effect e = new Effect(newFormalArgVariable, MONOMORPHIZED_EFFECT, newFormalArgVariable.getLocation());
        final Set<Effect> monomorphicEffects = new HashSet<>();
        monomorphicEffects.add(e);
        final EffectSet monomorphicEffect = new EffectSet(monomorphicEffects);

        // Construct visitors

        final TypeLifter typeLifter = new TypeLifter(newCtx, monomorphicEffect);
        final DeclarationLifter declarationLifter = new DeclarationLifter(typeLifter, monomorphicEffect);

        // Construct new list of formal arguments

        final List<FormalArg> newFormalArgs = new LinkedList<>();
        newFormalArgs.add(newFormalArg);

        for (FormalArg oldFormalArg : oldFormalArgs) {
            ValueType newFormalArgType = oldFormalArg.getType().acceptVisitor(typeLifter, new State());
            newFormalArgs.add(new FormalArg(
                    oldFormalArg.getSite(),
                    newFormalArgType
            ));
        }

        // Construct new functor return type

        final ValueType newReturnType = functor.getType().acceptVisitor(typeLifter, new State());

        // Construct new functor body

        final SeqExpr oldBody = (SeqExpr) functor.getBody();
        final SeqExpr newBody = new SeqExpr();

        for (HasLocation oldElement : oldBody.getElements()) {
            if (oldElement instanceof VarBinding) {
                final VarBinding oldBinding = (VarBinding) oldElement;
                final IExpr boundExpression = oldBinding.getExpression();

                if (!(boundExpression instanceof New)) {
                    newBody.addBinding(oldBinding, false);
                    continue;
                }

                final New oldNew = (New) boundExpression;

                final New newNew = handleNew(newCtx, declarationLifter, oldNew);
                final VarBinding newBinding = new VarBinding(oldBinding.getSite(), newNew.getType(), newNew);

                newBody.addBinding(newBinding, true);
            } else if (oldElement instanceof New) {
                New oldNew = (New) oldElement;
                New newNew = handleNew(newCtx, declarationLifter, oldNew);

                newBody.addExpr(newNew);
            } else {
                throw new RuntimeException(
                        "Found element of SeqExpr of resource module that is neither a VarBinding nor a New: "
                                + oldElement
                );
            }
        }

        // Ensure the functor has an effect bound

        final EffectSet oldEffectSet = functor.getEffectSet();
        final EffectSet newEffectSet = oldEffectSet == null ? new EffectSet(new HashSet<>()) : oldEffectSet;

        // Construct new functor

        final DefDeclaration newFunctor = new DefDeclaration(
                functor.getName(),
                newFormalArgs,
                newReturnType,
                newBody,
                functor.getLocation(),
                newEffectSet
        );

        return newFunctor;
    }


    private static New handleNew(TypeContext ctx, DeclarationLifter declarationLifter, New oldNew) {
        // Construct new declarations
        List<Declaration> oldDeclarations = oldNew.getDecls();
        List<NamedDeclaration> newDeclarations = oldDeclarations.stream().map(
                d -> d.acceptVisitor(declarationLifter, new State())
        ).collect(Collectors.toList());

        // Construct new structural type
        final List<DeclType> newDeclTypes =
                newDeclarations.stream().map(NamedDeclaration::getDeclType).collect(Collectors.toList());
        final StructuralType newType =
                new StructuralType(
                        oldNew.getSelfName(),
                        newDeclTypes,
                        oldNew.getType().isResource(ctx)
                );

        // Construct new New
        return new New(newDeclarations, oldNew.getSelfSite(), newType, oldNew.getLocation());
    }

    public static boolean isMonomorphized(EffectSet effectSet) {
        if (effectSet == null) {
            return false;
        }

        final Set<Effect> effects = effectSet.getEffects();

        if (effects.size() != 1) {
            return false;
        }

        return effects.iterator().next().getName().equals(MONOMORPHIZED_EFFECT);
    }

    private static final class State {
        private final NominalType nominalType;

        private State() {
            this.nominalType = null;
        }

        private State(NominalType nominalType) {
            this.nominalType = nominalType;
        }

        public NominalType getNominalType() {
            return this.nominalType;
        }
    }

    private static final class TypeLifter extends TypeVisitor<State, ValueType> {
        private final GenContext ctx;
        private final DeclTypeLifter declTypeLifter;
        private final EffectSet monomorphicEffect;

        private TypeLifter(final GenContext ctx, final EffectSet monomorphicEffect) {
            super("TypeLifter");
            this.ctx = ctx;
            this.monomorphicEffect = monomorphicEffect;
            this.declTypeLifter = this.new DeclTypeLifter();
        }

        @Override
        // Transforms the nominal type into its structural type
        public ValueType visit(State state, NominalType nominalType) {
            if (nominalType.equals(state.getNominalType())) {
                // TODO (@justinlubin) recursive?
                return nominalType;
            } else if (nominalType.getPath().toString().contains("system")) {
                // TODO (@justinlubin) system types?
                return nominalType;
            } else {
                StructuralType st = nominalType.getStructuralType(this.ctx, null);
                if (st == null) {
                    throw new RuntimeException("Structural type not found for nominal type '" + nominalType + "'");
                }
                return st.acceptVisitor(this, new State(nominalType));
            }
        }

        @Override
        public ValueType visit(State state, StructuralType structuralType) {
            final List<DeclType> oldDeclTypes = structuralType.getDeclTypes();
            final List<DeclType> newDeclTypes = oldDeclTypes.stream().map(dt ->
                    dt.acceptVisitor(this.declTypeLifter, state)
            ).collect(Collectors.toList());
            return new StructuralType(structuralType.getSelfSite(), newDeclTypes, structuralType.isResource(this.ctx));
        }

        @Override
        // Transforms the refinement type into its structural type
        public ValueType visit(State state, RefinementType refinementType) {
            StructuralType st = refinementType.getStructuralType(this.ctx);
            return st == null ? null : st.acceptVisitor(this, state);
        }

        @Override
        public ValueType visit(State state, ValueType valueType) {
            throw new RuntimeException("TypeLifter should not visit abstract type ValueType");
        }

        @Override
        // TODO (@justinlubin)
        public ValueType visit(State state, ExtensibleTagType extensibleTagType) {
            throw new RuntimeException("TypeLifter not yet implemented for ExtensibleTagType");
        }

        @Override
        // TODO (@justinlubin)
        public ValueType visit(State state, DataType dataType) {
            throw new RuntimeException("TypeLifter not yet implemented for DataType");
        }

        private final class DeclTypeLifter extends DeclTypeVisitor<State, DeclType> {
            private DeclTypeLifter() {
                super("DeclTypeLifter");
            }

            @Override
            public DeclType visit(State state, VarDeclType varDeclType) {
                final ValueType oldType = varDeclType.getRawResultType();
                final ValueType newType = oldType.acceptVisitor(TypeLifter.this, state);
                return new VarDeclType(varDeclType.getName(), newType);
            }

            @Override
            public DeclType visit(State state, ValDeclType valDeclType) {
                final ValueType oldType = valDeclType.getRawResultType();
                final ValueType newType = oldType.acceptVisitor(TypeLifter.this, state);
                return new ValDeclType(valDeclType.getName(), newType);
            }

            @Override
            public DeclType visit(State state, DefDeclType defDeclType) {
                // Return type
                final ValueType oldReturnType = defDeclType.getRawResultType();
                final ValueType newReturnType = oldReturnType.acceptVisitor(TypeLifter.this, state);

                // Args
                final List<FormalArg> oldFormalArgs = defDeclType.getFormalArgs();
                final List<FormalArg> newFormalArgs =
                        oldFormalArgs.stream().map(arg ->
                                new FormalArg(
                                        arg.getSite(),
                                        arg.getType().acceptVisitor(TypeLifter.this, state)
                                )
                        ).collect(Collectors.toList());

                // Effects
                final EffectSet oldEffectSet = defDeclType.getEffectSet();
                final EffectSet newEffectSet;
                if (oldEffectSet == null) {
                    // TODO: this is the problem...
                    // This declaration is unannotated, so annotate it with the monomorphized effect
                    newEffectSet = TypeLifter.this.monomorphicEffect;
                    //newEffectSet = null;
                } else {
                    // This declaration is annotated, so don't change it
                    newEffectSet = oldEffectSet;
                }

                // New declaration
                return new DefDeclType(defDeclType.getName(), newReturnType, newFormalArgs, newEffectSet);
            }

            @Override
            public DeclType visit(State state, AbstractTypeMember abstractDeclType) {
                return new AbstractTypeMember(abstractDeclType.getName(), abstractDeclType.isResource());
            }

            @Override
            public DeclType visit(State state, ConcreteTypeMember concreteTypeMember) {
                final ValueType oldType = concreteTypeMember.getRawResultType();
                final ValueType newType = oldType.acceptVisitor(TypeLifter.this, state);
                return new ConcreteTypeMember(
                        concreteTypeMember.getName(),
                        newType,
                        concreteTypeMember.getMetadataValue()
                );
            }

            @Override
            public DeclType visit(State state, EffectDeclType effectDeclType) {
                final EffectSet oldEffectSet = effectDeclType.getEffectSet();
                final EffectSet newEffectSet;
                if (oldEffectSet == null) {
                    // This effect declaration is abstract, so we must monomorphize it
                    newEffectSet = TypeLifter.this.monomorphicEffect;
                } else {
                    // This effect declaration is concrete, so we need not do anything
                    newEffectSet = oldEffectSet;
                }
                return new EffectDeclType(effectDeclType.getName(), newEffectSet, effectDeclType.getLocation());
            }

      @Override
      public DeclType visit(State state, RecDeclaration recDecl) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public DeclType visit(State state, RecConstructDeclaration recConstructDecl) {
        // TODO Auto-generated method stub
        return null;
      }
        }

      @Override
      public ValueType visit(State state, RecDeclaration recDecl) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public ValueType visit(State state, RecConstructDeclaration recConstructDecl) {
        // TODO Auto-generated method stub
        return null;
      }
    }

    private static final class DeclarationLifter extends DeclarationVisitor<State, NamedDeclaration> {
        private final TypeLifter typeLifter;
        private final EffectSet monomorphicEffect;

        private DeclarationLifter(final TypeLifter typeLifter, final EffectSet monomorphicEffect) {
            super("DeclarationLifter");
            this.typeLifter = typeLifter;
            this.monomorphicEffect = monomorphicEffect;
        }

        @Override
        public NamedDeclaration visit(State state, VarDeclaration varDecl) {
            final ValueType oldType = varDecl.getType();
            final ValueType newType = oldType.acceptVisitor(this.typeLifter, state);
            return new VarDeclaration(varDecl.getName(), newType, varDecl.getDefinition(), varDecl.getLocation());
        }

        @Override
        public NamedDeclaration visit(State state, ValDeclaration valDecl) {
            final ValueType oldType = valDecl.getType();
            final ValueType newType = oldType.acceptVisitor(this.typeLifter, state);
            return new ValDeclaration(valDecl.getName(), newType, valDecl.getDefinition(), valDecl.getLocation());
        }

        @Override
        public NamedDeclaration visit(State state, DefDeclaration defDecl) {
            // Return type
            final ValueType oldReturnType = defDecl.getType();
            final ValueType newReturnType = oldReturnType.acceptVisitor(this.typeLifter, state);

            // Args
            final List<FormalArg> oldFormalArgs = defDecl.getFormalArgs();
            final List<FormalArg> newFormalArgs =
                    oldFormalArgs.stream().map(arg ->
                            new FormalArg(
                                    arg.getSite(),
                                    arg.getType().acceptVisitor(this.typeLifter, state)
                            )
                    ).collect(Collectors.toList());

            // Effects
            final EffectSet oldEffectSet = defDecl.getEffectSet();
            final EffectSet newEffectSet;
            if (oldEffectSet == null) {
                // This declaration is unannotated, so annotate it with the monomorphized effect
                newEffectSet = this.monomorphicEffect;
            } else {
                // This declaration is annotated, so don't change it
                newEffectSet = oldEffectSet;
            }

            // New declaration
            return new DefDeclaration(
                    defDecl.getName(),
                    newFormalArgs,
                    newReturnType,
                    defDecl.getBody(),
                    defDecl.getLocation(),
                    newEffectSet
            );
        }

        @Override
        public NamedDeclaration visit(State state, ModuleDeclaration moduleDecl) {
            throw new RuntimeException("DeclarationLifter should not visit abstract type ModuleDeclaration");
        }

        @Override
        public NamedDeclaration visit(State state, ForwardDeclaration forwardDecl) {
            throw new RuntimeException("DeclarationLifter not implemented for non-named declarations");
        }

        @Override
        public NamedDeclaration visit(State state, TypeDeclaration typeDecl) {
            final ValueType oldType = typeDecl.getSourceType().getValueType();
            final ValueType newType = oldType.acceptVisitor(this.typeLifter, state);
            return new TypeDeclaration(typeDecl.getName(), newType, typeDecl.getMeta(), typeDecl.getLocation());
        }

        @Override
        public NamedDeclaration visit(State state, EffectDeclaration effectDeclaration) {
            final EffectSet oldEffectSet = effectDeclaration.getEffectSet();
            final EffectSet newEffectSet;
            if (oldEffectSet == null) {
                // This effect declaration is abstract, so we must monomorphize it
                newEffectSet = this.monomorphicEffect;
            } else {
                // This effect declaration is concrete, so we need not do anything
                newEffectSet = oldEffectSet;
            }
            return new EffectDeclaration(effectDeclaration.getName(), newEffectSet, effectDeclaration.getLocation());
        }

    @Override
    public NamedDeclaration visit(State state, RecDeclaration recDecl) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public NamedDeclaration visit(State state, RecConstructDeclaration recConstructDecl) {
      // TODO Auto-generated method stub
      return null;
    }
  }
}
