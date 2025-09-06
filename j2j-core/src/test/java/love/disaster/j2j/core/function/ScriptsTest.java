/*
 * Copyright 2013 Bazaarvoice, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package love.disaster.j2j.core.function;


import love.disaster.j2j.core.JoltTestUtil;
import love.disaster.j2j.core.Modifier;
import love.disaster.j2j.core.modifier.function.Scripts;
import love.disaster.j2j.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptsTest {

    private ScriptEngine jsEngine;
    private ScriptEngine pyEngine;

    @BeforeClass
    @SuppressWarnings( "unused" )
    public void setup() {
        ScriptEngineManager manager = new ScriptEngineManager();
        jsEngine = manager.getEngineByName("javascript");
        pyEngine = manager.getEngineByName("python");
    }

    @Test
    public void testCanLoadScriptEngines() {
        // This test is informational only - engines may or may not be available depending on JVM
        System.out.println("JavaScript engine: " + (jsEngine != null ? "Available" : "Not available"));
        System.out.println("Python engine: " + (pyEngine != null ? "Available" : "Not available"));
    }

    @Test
    public void testJavaScriptFunction() {
        // Skip test if JavaScript engine is not available
        if (jsEngine == null) {
            System.out.println("Skipping JavaScript test - engine not available");
            return;
        }

        Scripts.javascript jsFunc = new Scripts.javascript();

        // Test simple addition
        List<Object> args = Lists.newArrayList();
        args.add("arg1 + arg2");
        args.add(5);
        args.add(3);

        Assert.assertTrue(jsFunc.apply(args.toArray()).isPresent());
        Assert.assertEquals(jsFunc.apply(args.toArray()).get(), 8.0);

        // Test string operation
        args.clear();
        args.add("arg1.toUpperCase()");
        args.add("hello");

        Assert.assertTrue(jsFunc.apply(args.toArray()).isPresent());
        Assert.assertEquals(jsFunc.apply(args.toArray()).get(), "HELLO");
    }

    @Test
    public void testPythonFunction() {
        // Skip test if Python engine is not available
        if (pyEngine == null) {
            System.out.println("Skipping Python test - engine not available");
            return;
        }

        Scripts.python pyFunc = new Scripts.python();

        // Test simple multiplication
        List<Object> args = Lists.newArrayList();
        args.add("arg1 * arg2");
        args.add(5);
        args.add(3);

        Assert.assertTrue(pyFunc.apply(args.toArray()).isPresent());
        Assert.assertEquals(pyFunc.apply(args.toArray()).get(), 15);

        // Test string operation
        args.clear();
        args.add("arg1.lower()");
        args.add("HELLO");

        Assert.assertTrue(pyFunc.apply(args.toArray()).isPresent());
        Assert.assertEquals(pyFunc.apply(args.toArray()).get(), "hello");
    }

    @Test
    public void testModifierWithScripts() throws IOException {
        // Skip test if JavaScript engine is not available
        if (new ScriptEngineManager().getEngineByName("javascript") == null) {
            System.out.println("Skipping modifier test - JavaScript engine not available");
            return;
        }
        
        String testPath = "/json/modifier/scriptTest.json";
        Map<String, Object> testUnit = (Map<String, Object>) JsonUtils.classpathToMap( testPath );

        Object input = testUnit.get( "input" );
        Object spec = testUnit.get( "spec" );
        Object expected = testUnit.get( "expected" );

        Modifier modifier = new Modifier.Overwritr( spec );
        Object actual = modifier.transform( JsonUtils.cloneJson(input), new HashMap<String, Object>() );

        JoltTestUtil.runArrayOrderObliviousDiffy( "failed case " + testPath, expected, actual );
    }
}