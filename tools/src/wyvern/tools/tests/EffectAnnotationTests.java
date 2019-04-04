package wyvern.tools.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

/**
 * Test suite for effect annotation checking.
 */
@Category(RegressionTests.class)
public class EffectAnnotationTests {
    private static final String PATH = TestUtil.BASE_PATH;
    private static final String HEADER =
            "import wyvern.collections.list\ntype MyType\n  type A\n  effect B\n";

    private static ValueType buildStructuralType(String... declarations) throws ParseException {
        StringBuilder b = new StringBuilder(HEADER);
        b.append("new\n  type T");
        for (String d : declarations) {
            b.append("\n    ").append(d);
        }
        return TestUtil.evaluate(b.toString()).getType();
    }

    private static void assertAnnotationStatus(
            boolean annotated, boolean unannotated, String... declarations
    ) throws ParseException {
        Assert.assertEquals(annotated,
                buildStructuralType(declarations).isEffectAnnotated(Globals.getStandardTypeContext())
        );
        Assert.assertEquals(unannotated,
                buildStructuralType(declarations).isEffectUnannotated(Globals.getStandardTypeContext())
        );
    }

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
    public void testBasic1() throws ParseException {
        assertAnnotationStatus(true, false,
                "def foo() : {} Unit"
        );
    }

    @Test
    public void testBasic2() throws ParseException {
        assertAnnotationStatus(true, false,
                "def foo() : {system.FFI} Unit"
        );
    }

    @Test
    public void testBasic3() throws ParseException {
        assertAnnotationStatus(true, false,
                "effect E"
        );
    }

    @Test
    public void testBasic4() throws ParseException {
        assertAnnotationStatus(true, false,
                "effect E = {}"
        );
    }

    @Test
    public void testBasic5() throws ParseException {
        assertAnnotationStatus(true, false,
                "effect E = {system.FFI}"
        );
    }

    @Test
    public void testBasic6() throws ParseException {
        assertAnnotationStatus(false, true,
                "def bar() : Unit"
        );
    }

    @Test
    public void testBasic7() throws ParseException {
        assertAnnotationStatus(true, true,
                "val x : Int"
        );
    }

    @Test
    public void testBasic8() throws ParseException {
        assertAnnotationStatus(true, true,
                "val x : Int"
        );
    }

    @Test
    public void testBasic9() throws ParseException {
        assertAnnotationStatus(true, true,
                "val x : Int",
                "val y : String",
                "type U",
                "type V = Float"
        );
    }

    @Test
    public void testBasic10() throws ParseException {
        assertAnnotationStatus(false, false,
                "def foo() : {system.FFI} Unit",
                "def bar() : Unit"
        );
    }

    @Test
    public void testBasic11() throws ParseException {
        assertAnnotationStatus(false, false,
                "def bar() : Unit",
                "effect E"
        );
    }

    @Test
    public void testBasic12() throws ParseException {
        assertAnnotationStatus(false, false,
                "def bar() : Unit",
                "effect E = {}"
        );
    }

    @Test
    public void testBasic13() throws ParseException {
        assertAnnotationStatus(false, false,
                "def bar() : Unit",
                "effect E = {system.FFI}"
        );
    }

    @Test
    public void testBasic14() throws ParseException {
        assertAnnotationStatus(true, false,
                "type U\n      effect E"
        );

    }

    @Test
    public void testBasic15() throws ParseException {
        assertAnnotationStatus(true, true,
                "type U = list.List[Int]"
        );
    }

    @Test
    public void testBasic16() throws ParseException {
        assertAnnotationStatus(true, true,
                "type U = MyType"
        );
    }

    @Test
    public void testBasic17() throws ParseException {
        assertAnnotationStatus(true, true,
                "type U = MyType[Int]"
        );
    }

    @Test
    public void testBasic18() throws ParseException {
        assertAnnotationStatus(true, false,
                "type U = MyType[Int, {}]"
        );
    }

    @Test
    public void testBasic19() throws ParseException {
        assertAnnotationStatus(true, false,
                "type U = MyType[Int, {system.FFI}]"
        );
    }
}
