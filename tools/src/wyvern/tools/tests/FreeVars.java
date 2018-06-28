package wyvern.tools.tests;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class FreeVars {

    private static final String BASE_PATH = TestUtil.BASE_PATH;
    private static final String PATH = BASE_PATH + "modules/module/";

    private NominalType systemInt() {
        return new NominalType("system", "Int");
    }

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
    public void testMethodDefinition() throws ParseException {

        // def meth() : system.Int
        //    x

        DefDeclaration defDecl = new DefDeclaration("method", new ArrayList<>(), systemInt(),
                new Variable("x"), FileLocation.UNKNOWN);

        Set<String> freeVars = defDecl.getFreeVariables();
        Assert.assertTrue("x is a free variable in this method.", freeVars.contains("x"));
        Assert.assertEquals("x is the only free variable in this method.", freeVars.size(), 1);
    }

    @Test
    public void testMethodWithFormalArgs() throws ParseException {

        // def meth(x : Int) : system.Int
        //     x = y
        //     x

        Let varAssign = new Let("x", systemInt(), new Variable("y"), new Variable("x"));
        DefDeclaration defDecl = new DefDeclaration("method", new ArrayList<>(), systemInt(), varAssign, FileLocation.UNKNOWN);

        Set<String> freeVars = defDecl.getFreeVariables();

        Assert.assertTrue("y is a free variable in this method.", freeVars.contains("y"));
        Assert.assertFalse("x is not a free variable in this method.", freeVars.contains("x"));
        Assert.assertEquals("y is the only free variable in this method.", freeVars.size(), 1);
    }

    @Test
    public void testDefInsideLet() {

        // var y : system.Int = 5
        // def meth (x : Int) : system.Int
        //     x = y
        //     x

        // The body of the method.
        Let assignYtoX = new Let("x", systemInt(), new Variable("y"), new Variable("x"));

        // The method declaration, wrapped inside a New expression.
        LinkedList<Declaration> decls = new LinkedList<>();
        DefDeclaration defDecl = new DefDeclaration("method", new ArrayList<>(), systemInt(), assignYtoX, FileLocation.UNKNOWN);
        decls.add(defDecl);

        // The type of the declaration sequence.
        DeclType varDeclType = new VarDeclType("y", systemInt());
        List<DeclType> declTypes = new LinkedList<>();
        declTypes.add(varDeclType);
        StructuralType typeOfDecls = new StructuralType("this", declTypes, true);

        // Turn into a New expression.
        New newDecls = new New(decls, typeOfDecls.getSelfSite(), typeOfDecls, null);

        // Enclose with the declaration of y.
        Let assign5toY = new Let("y", systemInt(), new IntegerLiteral(5), newDecls);

        // Check the free variables.
        Set<String> freeVars = assign5toY.getFreeVariables();
        Assert.assertTrue("There are no free variables in this program.", freeVars.isEmpty());
    }

}
