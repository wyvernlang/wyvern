package wyvern.tools.tests.suites;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import wyvern.tools.tests.FileTest;
import wyvern.tools.tests.HigherOrderTests;
import wyvern.tools.tests.BackEndTests;
import wyvern.tools.tests.CoreParserTests;
import wyvern.tools.tests.DemoTests;
import wyvern.tools.tests.EffectAnnotationTests;
import wyvern.tools.tests.EffectSystemTests;
import wyvern.tools.tests.ExampleTests;
import wyvern.tools.tests.ExamplesNoPrelude;
import wyvern.tools.tests.FFITests;
import wyvern.tools.tests.Figures;
import wyvern.tools.tests.FreeVars;
import wyvern.tools.tests.ILTests;
import wyvern.tools.tests.ILTestsWithPrelude;
import wyvern.tools.tests.Illustrations;
import wyvern.tools.tests.LexingTests;
import wyvern.tools.tests.ModuleSystemTests;
import wyvern.tools.tests.OIRTests;
import wyvern.tools.tests.PolymorphicEffectTests;
import wyvern.tools.tests.ReflectionTests;
import wyvern.tools.tests.RossettaCodeTests;
import wyvern.tools.tests.StdlibTests;
import wyvern.tools.tests.TransformTests;
import wyvern.tools.tests.EffectSeparationTests;

/**
 * This test suite includes all working tests (as of this writing) that
 * can be run successfully with Ant.  Some tests are commented out as
 * they are currently broken.
 *
 * @author aldrich
 *
 */
@RunWith(Categories.class)
@IncludeCategory(RegressionTests.class)
@ExcludeCategory(CurrentlyBroken.class)
@SuiteClasses(
        {
            // CURRENTLY IMPORTANT TESTS HERE
            LexingTests.class,      // tests the new lexer
            CoreParserTests.class,  // tests the new parser
            ILTests.class,          // tests the new IL, not using the prelude
            ILTestsWithPrelude.class,          // tests the new IL and the standard prelude
            RossettaCodeTests.class, // a few of these are out of date, but some use new everything
            FFITests.class,
            OIRTests.class,
            Figures.class,
            Illustrations.class,        // tests the new IL
            DemoTests.class,
            ReflectionTests.class,
            TransformTests.class,
            FreeVars.class,
            StdlibTests.class,      // tests the standard library with the new IL
            ExampleTests.class,      // tests the examples
            ExamplesNoPrelude.class,      // tests the examples that don't require the prelude
            EffectSystemTests.class,
            PolymorphicEffectTests.class,
            EffectAnnotationTests.class,
            ModuleSystemTests.class,        // tests the new IL
            HigherOrderTests.class,
            EffectSeparationTests.class,
            FileTest.class,
            BackEndTests.class
        }
        )
public class AntRegressionTestSuite { }
