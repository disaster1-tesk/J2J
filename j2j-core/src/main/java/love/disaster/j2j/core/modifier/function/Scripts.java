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
package love.disaster.j2j.core.modifier.function;


import love.disaster.j2j.core.common.Optional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JOLT Modifier function to execute JavaScript, Python scripts, Beetl templates, and JSONata queries
 */
@SuppressWarnings( "deprecated" )
public class Scripts {
    
    // Reuse ScriptEngineManager for better performance
    private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    
    // Logger for the class
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Scripts.class.getName());

    /**
     * Execute JavaScript code with given arguments
     */
    public static final class javascript extends Function.ListFunction {
        @Override
        protected Optional<Object> applyList(final List<Object> argList ) {
            // Need at least the script code
            if ( argList.isEmpty() ) {
                return Optional.empty();
            }

            try {
                ScriptEngine engine = scriptEngineManager.getEngineByName( "javascript" );
                if (engine == null) {
                    logger.warning("JavaScript engine not available");
                    return Optional.empty();
                }
                
                // Put arguments into the engine scope (arg1, arg2, etc.)
                for ( int i = 1; i < argList.size(); i++ ) {
                    engine.put( "arg" + i, argList.get( i ) );
                }
                
                // Execute the script and return result
                Object result = engine.eval( (String) argList.get( 0 ) );
                return Optional.of( result );
            }
            catch ( ScriptException e ) {
                logger.severe("JavaScript execution error: " + e.getMessage());
                return Optional.empty();
            }
            catch ( Exception e ) {
                logger.severe("JavaScript error: " + e.getMessage());
                return Optional.empty();
            }
        }
    }

    /**
     * Execute Python code with given arguments
     */
    public static final class python extends Function.ListFunction {
        @Override
        protected Optional<Object> applyList( final List<Object> argList ) {
            // Need at least the script code
            if ( argList.isEmpty() ) {
                return Optional.empty();
            }

            try {
                ScriptEngine engine = scriptEngineManager.getEngineByName( "python" );
                // Python engine may not be available depending on JVM
                if (engine == null) {
                    // Python engine not available, this is not an error
                    logger.info("Python engine not available");
                    return Optional.empty();
                }
                
                // Put arguments into the engine scope (arg1, arg2, etc.)
                for ( int i = 1; i < argList.size(); i++ ) {
                    engine.put( "arg" + i, argList.get( i ) );
                }
                
                // Execute the script and return result
                Object result = engine.eval( (String) argList.get( 0 ) );
                return Optional.of( result );
            }
            catch ( ScriptException e ) {
                logger.severe("Python execution error: " + e.getMessage());
                return Optional.empty();
            }
            catch ( Exception e ) {
                logger.severe("Python error: " + e.getMessage());
                return Optional.empty();
            }
        }
    }

    /**
     * Execute Beetl template with given context data
     * 
     * Usage: beetl(template, contextData)
     * - template: Beetl template string
     * - contextData: Map or JSON object to use as template context
     * 
     * Example: beetl("Hello ${name}!", {"name": "World"})
     */
    public static final class beetl extends Function.ListFunction {
        // Static GroupTemplate for better performance
        private static org.beetl.core.GroupTemplate staticGroupTemplate;
        
        static {
            try {
                org.beetl.core.resource.StringTemplateResourceLoader resourceLoader = 
                    new org.beetl.core.resource.StringTemplateResourceLoader();
                org.beetl.core.Configuration cfg = org.beetl.core.Configuration.defaultConfiguration();
                staticGroupTemplate = new org.beetl.core.GroupTemplate(resourceLoader, cfg);
            } catch (Exception e) {
                logger.severe("Failed to initialize Beetl GroupTemplate: " + e.getMessage());
            }
        }
        
        @Override
        protected Optional<Object> applyList(final List<Object> argList) {
            // Need at least the template and context
            if (argList.size() < 2) {
                logger.warning("Insufficient arguments for Beetl template execution");
                return Optional.empty();
            }

            String templateStr = null;
            Object contextData = null;
            
            try {
                templateStr = (String) argList.get(0);
                contextData = argList.get(1);
            } catch (ClassCastException e) {
                logger.severe("Invalid argument types for Beetl template: " + e.getMessage());
                return Optional.empty();
            }

            try {
                // Use the static GroupTemplate for better performance
                if (staticGroupTemplate == null) {
                    logger.severe("Beetl GroupTemplate not initialized");
                    return Optional.empty();
                }
                
                // Create template from string
                org.beetl.core.Template template = staticGroupTemplate.getTemplate(templateStr);
                
                // Set context variables using Beetl's binding method
                if (contextData instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> contextMap = (Map<String, Object>) contextData;
                    for (Map.Entry<String, Object> entry : contextMap.entrySet()) {
                        template.binding(entry.getKey(), entry.getValue());
                    }
                } else {
                    // If not a map, use as single variable
                    template.binding("data", contextData);
                }
                
                // Render template
                String result = template.render();
                logger.fine("Beetl template executed successfully");
                return Optional.of(result);
                
            } catch (NoClassDefFoundError e) {
                logger.severe("Beetl library not available: " + e.getMessage());
                return Optional.empty();
            } catch (Exception e) {
                logger.severe("Beetl template execution error with template '" + templateStr + "': " + e.getMessage());
                return Optional.empty();
            }
        }
    }

    /**
     * Execute JSONata query on JSON data
     * 
     * Usage: jsonata(query, jsonData)
     * - query: JSONata query string
     * - jsonData: JSON data to query (Map, List, or JSON string)
     * 
     * Example: jsonata("name", {"name": "John", "age": 30})
     */
    public static final class jsonata extends Function.ListFunction {
        @Override
        protected Optional<Object> applyList(final List<Object> argList) {
            // Need at least the query
            if (argList.isEmpty()) {
                logger.warning("Insufficient arguments for JSONata query execution: " + argList.size());
                return Optional.empty();
            }

            try {
                logger.info("JSONata function called with args: " + argList);
                
                // Use JSONata4Java's recommended approach with Expression class
                String queryStr = (String) argList.get(0);
                
                logger.info("JSONata query: " + queryStr);
                
                // Parse the JSONata expression using the static parse method
                com.api.jsonata4java.Expression expr = 
                    com.api.jsonata4java.Expression.jsonata(queryStr);
                
                // If we have data to query, use it; otherwise, create an empty object
                Object jsonData = null;
                if (argList.size() > 1) {
                    jsonData = argList.get(1);
                    logger.info("JSONata data: " + jsonData);
                } else {
                    // Create empty object for evaluation
                    jsonData = new HashMap<>();
                    logger.info("No data provided, using empty object");
                }
                
                // Convert data to JsonNode using Jackson ObjectMapper
                com.fasterxml.jackson.databind.ObjectMapper mapper = 
                    new com.fasterxml.jackson.databind.ObjectMapper();
                
                com.fasterxml.jackson.databind.JsonNode jsonNode;
                if (jsonData instanceof String) {
                    // Try to parse as JSON string
                    try {
                        jsonNode = mapper.readTree((String) jsonData);
                    } catch (Exception e) {
                        // If parsing fails, treat as simple string and convert
                        jsonNode = mapper.valueToTree(jsonData);
                    }
                } else {
                    // Convert other types to JsonNode
                    jsonNode = mapper.valueToTree(jsonData);
                }
                
                logger.info("JSONata JSON node: " + jsonNode);
                
                // Evaluate the JSONata expression
                com.fasterxml.jackson.databind.JsonNode result = expr.evaluate(jsonNode);
                
                logger.info("JSONata result: " + result);
                logger.info("JSONata result class: " + (result != null ? result.getClass().getName() : "null"));
                
                // Convert result back to appropriate Java object
                if (result == null || result.isNull()) {
                    logger.fine("JSONata query returned null result");
                    return Optional.empty();
                } else if (result.isTextual()) {
                    logger.info("JSONata result is textual: " + result.asText());
                    return Optional.of(result.asText());
                } else if (result.isNumber()) {
                    if (result.isInt()) {
                        logger.info("JSONata result is int: " + result.asInt());
                        return Optional.of(result.asInt());
                    } else if (result.isLong()) {
                        logger.info("JSONata result is long: " + result.asLong());
                        return Optional.of(result.asLong());
                    } else {
                        logger.info("JSONata result is double: " + result.asDouble());
                        return Optional.of(result.asDouble());
                    }
                } else if (result.isBoolean()) {
                    logger.info("JSONata result is boolean: " + result.asBoolean());
                    return Optional.of(result.asBoolean());
                } else if (result.isArray()) {
                    logger.info("JSONata result is array");
                    // Convert array to List
                    Object converted = mapper.treeToValue(result, Object.class);
                    logger.info("JSONata array result: " + converted);
                    return Optional.of(converted);
                } else if (result.isObject()) {
                    logger.info("JSONata result is object");
                    // Convert object to Map
                    Object converted = mapper.treeToValue(result, Object.class);
                    logger.info("JSONata object result: " + converted);
                    return Optional.of(converted);
                } else {
                    logger.info("JSONata result is other type");
                    // For complex objects, return as Map or List
                    Object converted = mapper.treeToValue(result, Object.class);
                    logger.info("JSONata converted result: " + converted);
                    return Optional.of(converted);
                }
                
            } catch (NoClassDefFoundError e) {
                logger.severe("JSONata4Java library not available: " + e.getMessage());
                e.printStackTrace();
                return Optional.empty();
            } catch (Exception e) {
                logger.severe("JSONata query execution error: " + e.getMessage());
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }

    /**
     * Advanced Beetl template execution with multiple data sources
     * 
     * Usage: beetlAdvanced(template, mainContext, additionalContext1, additionalContext2, ...)
     * - template: Beetl template string
     * - mainContext: Primary context data
     * - additionalContexts: Additional context objects that will be merged
     */
    public static final class beetlAdvanced extends Function.ListFunction {
        // Static GroupTemplate for better performance
        private static org.beetl.core.GroupTemplate staticGroupTemplate;
        
        static {
            try {
                org.beetl.core.resource.StringTemplateResourceLoader resourceLoader = 
                    new org.beetl.core.resource.StringTemplateResourceLoader();
                org.beetl.core.Configuration cfg = org.beetl.core.Configuration.defaultConfiguration();
                staticGroupTemplate = new org.beetl.core.GroupTemplate(resourceLoader, cfg);
            } catch (Exception e) {
                logger.severe("Failed to initialize Beetl GroupTemplate for advanced usage: " + e.getMessage());
            }
        }
        
        @Override
        protected Optional<Object> applyList(final List<Object> argList) {
            // Need at least template and one context
            if (argList.size() < 2) {
                logger.warning("Insufficient arguments for advanced Beetl template execution");
                return Optional.empty();
            }

            try {
                // Use the static GroupTemplate for better performance
                if (staticGroupTemplate == null) {
                    logger.severe("Beetl GroupTemplate not initialized for advanced usage");
                    return Optional.empty();
                }
                
                String templateStr = (String) argList.get(0);
                
                // Create template from string
                org.beetl.core.Template template = staticGroupTemplate.getTemplate(templateStr);
                
                // Merge all context data
                Map<String, Object> mergedContext = new HashMap<>();
                
                for (int i = 1; i < argList.size(); i++) {
                    Object contextData = argList.get(i);
                    
                    if (contextData instanceof Map) {
                        Map<?, ?> contextMap = (Map<?, ?>) contextData;
                        for (Map.Entry<?, ?> entry : contextMap.entrySet()) {
                            mergedContext.put(entry.getKey().toString(), entry.getValue());
                        }
                    } else {
                        // Use index as key for non-map objects
                        mergedContext.put("ctx" + (i - 1), contextData);
                    }
                }
                
                // Set all variables in template using Beetl's binding method
                for (Map.Entry<String, Object> entry : mergedContext.entrySet()) {
                    template.binding(entry.getKey(), entry.getValue());
                }
                
                // Render template
                String result = template.render();
                logger.fine("Advanced Beetl template executed successfully");
                return Optional.of(result);
                
            } catch (NoClassDefFoundError e) {
                logger.severe("Beetl library not available: " + e.getMessage());
                return Optional.empty();
            } catch (Exception e) {
                logger.severe("Beetl advanced template execution error: " + e.getMessage());
                return Optional.empty();
            }
        }
    }
}