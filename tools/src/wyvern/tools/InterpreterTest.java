package wyvern.tools;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.tools.Interpreter;
import wyvern.tools.tests.suites.RegressionTests;

/* BEGIN: Metadata-related imports */
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Invokable;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
/* END: Metadata-related imports */

@Category(RegressionTests.class)
public class InterpreterTest {
    protected static void setEnv(Map<String, String> newenv) {
        try {
            // Works on Windows. Doesn't work on Linux/Mac OS X.
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            try {
                // Works on Linux/Mac OS X. Doesn't work on Windows.
                Class[] classes = Collections.class.getDeclaredClasses();
                Map<String, String> env = System.getenv();
                for (Class cl : classes) {
                    if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                        Field field = cl.getDeclaredField("m");
                        field.setAccessible(true);
                        Object obj = field.get(env);
                        Map<String, String> map = (Map<String, String>) obj;
                        map.clear();
                        map.putAll(newenv);
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void testInterpreter() throws java.io.IOException {
        // We assume here that this test is run from the wyvern/tools directory.
        String workingDir = System.getProperty("user.dir");

        Map<String, String> newEnv = new HashMap<String, String>();
        //newEnv.put("WYVERN_HOME", workingDir + "/..");
        newEnv.put("WYVERN_HOME", "/Users/sychoo/wyvern-forked");
        setEnv(newEnv);

        String[] args = new String[1];
        args[0] = "/Users/sychoo/wyvern-code/val2.wyv";

        Interpreter.main(args);

        System.exit(0);
    }
}
