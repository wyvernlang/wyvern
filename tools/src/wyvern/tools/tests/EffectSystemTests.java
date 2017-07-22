package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;

//import wyvern.target.corewyvernIL.support.Util;
//import wyvern.tools.parsing.coreparser.ParseException;
//import wyvern.tools.tests.suites.RegressionTests;

/**
 * Test suite for the effect system (adapted from ExampleTests.java).
 * Test cases numbered with "x0" (ex. 10) do not have effect annotations; 
 * those numbered with "xn" (ex. 12) are versions of "x0" test cases 
 * annotated with effects. 
 * 
 * Successful test cases have the following printout format:
 * "data sent: Network%d%d with(out) effects
 * data received"
 * 
 * Comments related to effects: "declaration, definition, method annotation"
 * Appearance in Wyvern:
 * effect "declared_effect" = {"its_defined_effects"}
 * def method_name() : {"method_annotation_of_effects"} return_type 
 * 
 * @author vzhao
 */
@Category(RegressionTests.class)
public class EffectSystemTests {
    private static final String PATH = TestUtil.BASE_PATH;
    
    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }
    
    @Test
    public void testEffectNetwork00() throws ParseException {
    	/* Type & module def with no annotations. */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork00", Util.unitType(), Util.unitValue());
    }

    @Test
     public void testEffectNetwork01() throws ParseException {
    	/* Declared in type + module def;
    	 * Defined in module def;
    	 * Method annotations in both. */ 
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork01", Util.unitType(), Util.unitValue());
	}
    
    @Test
    public void testEffectNetwork02() throws ParseException {
    	/* No declarations. Method annotations in type & module def. */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork02", Util.unitType(), Util.unitValue());
	}
  
    @Test
    public void testEffectNetwork03() throws ParseException {
    	/* In addition to declarations (not defined) & method annotations in type, additional declaration &
    	 * definition in module def. */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork03", Util.unitType(), Util.unitValue());
	}
    
    @Test
    @Category(CurrentlyBroken.class) // Parse error
    public void testEffectNetwork04() throws ParseException {
    	/* "gibberish" where "{}" should be in type's method header annotation. */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork04", Util.unitType(), Util.unitValue());
	}
    
    @Test
    @Category(CurrentlyBroken.class) // Parse error
    public void testEffectNetwork05() throws ParseException {
    	/* "gibberish" where "{}" should be in module def's method header annotation. */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork05", Util.unitType(), Util.unitValue());
	}
    
    @Test
    @Category(CurrentlyBroken.class) // Parse error
    public void testEffectNetwork06() throws ParseException {
    	/* Bogus declaration ("effect send = stdout") in module def. */ 
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork06", Util.unitType(), Util.unitValue());
	}
    
    @Test
    public void testEffectNetwork07() throws ParseException {
    	/* Declarations + 1 defined in type;
    	 * Declarations + definitions in module def;
    	 * Method annotations in both.
    	 */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork07", Util.unitType(), Util.unitValue());
	}
    
    @Test
    @Category(CurrentlyBroken.class) // Invalid effect (actually DSL block instead)
    public void testEffectNetwork08() throws ParseException {
    	/* Like network07, but "effect receive = {{}}" */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork08", Util.unitType(), Util.unitValue());
	}
    
    @Test
    public void testEffectNetwork09() throws ParseException {
    	/* No module declarations despite declarations in type + module def. */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork09", Util.unitType(), Util.unitValue());
    }
    
    @Test
    @Category(CurrentlyBroken.class) // Actually returns parse error (taken care of by the parser)
    public void testEffectNetwork0A() throws ParseException {
    	/* Effect undefined in module def. */
    	TestUtil.doTestScriptModularly(PATH, "effects.testNetwork0A", Util.unitType(), Util.unitValue());
    }
    
    @Test
    public void testEffectObjNetwork00() throws ParseException {
    	/* Object notation with no effect annotations. */
    	TestUtil.doTestScriptModularly(PATH, "effects.objNetwork00", Util.unitType(), Util.unitValue());
	}

    @Test
    public void testEffectObjNetwork01() throws ParseException {
    	/* Except for the "new" notation, should otherwise use the same a parser code as modules. */
    		TestUtil.doTestScriptModularly(PATH, "effects.objNetwork01", Util.unitType(), Util.unitValue());
	}
}