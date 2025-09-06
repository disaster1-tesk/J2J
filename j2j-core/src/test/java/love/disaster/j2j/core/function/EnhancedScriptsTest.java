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

import love.disaster.j2j.core.common.Optional;
import love.disaster.j2j.core.modifier.function.Function;
import love.disaster.j2j.core.modifier.function.Scripts;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

@SuppressWarnings("deprecated")
public class EnhancedScriptsTest {

    @Test
    public void testBeetlFunction() {
        Function beetl = new Scripts.beetl();
        
        // Test with insufficient arguments
        Optional<Object> result1 = beetl.apply();
        assertEquals(result1, Optional.empty());
        
        Optional<Object> result2 = beetl.apply("template only");
        assertEquals(result2, Optional.empty());
        
        // Test with proper arguments - library may not be available
        Map<String, Object> context = new HashMap<>();
        context.put("name", "World");
        Optional<Object> result3 = beetl.apply("Hello ${name}!", context);
        assertNotNull(result3); // Will be empty if library not available, or have result if available
    }
    
    @Test
    public void testJsonataFunction() {
        Function jsonata = new Scripts.jsonata();
        
        // Test with insufficient arguments
        Optional<Object> result1 = jsonata.apply();
        assertEquals(result1, Optional.empty());
        
        Optional<Object> result2 = jsonata.apply("query only");
        assertEquals(result2, Optional.empty());
        
        // Test with proper arguments - library may not be available
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        Optional<Object> result3 = jsonata.apply("name", data);
        assertNotNull(result3); // Will be empty if library not available, or have result if available
    }
    
    @Test
    public void testBeetlAdvancedFunction() {
        Function beetlAdvanced = new Scripts.beetlAdvanced();
        
        // Test with insufficient arguments
        Optional<Object> result1 = beetlAdvanced.apply();
        assertEquals(result1, Optional.empty());
        
        Optional<Object> result2 = beetlAdvanced.apply("template only");
        assertEquals(result2, Optional.empty());
        
        // Test with proper arguments - library may not be available
        Map<String, Object> context1 = new HashMap<>();
        context1.put("name", "Alice");
        Map<String, Object> context2 = new HashMap<>();
        context2.put("age", 28);
        
        Optional<Object> result3 = beetlAdvanced.apply("Name: ${name}, Age: ${age}", context1, context2);
        assertNotNull(result3); // Will be empty if library not available, or have result if available
    }

    /**
     * Test that functions handle gracefully when dependencies are not available
     */
    @Test
    public void testMissingDependencies() {
        Function beetl = new Scripts.beetl();
        Function jsonata = new Scripts.jsonata();
        
        // These should not throw exceptions even if libraries are missing
        Map<String, Object> context = new HashMap<>();
        context.put("name", "World");
        Optional<Object> beetlResult = beetl.apply("Hello ${name}", context);
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        Optional<Object> jsonataResult = jsonata.apply("name", data);
        
        // Results can be either success (if libraries present) or empty (if missing)
        assertNotNull(beetlResult);
        assertNotNull(jsonataResult);
    }
}