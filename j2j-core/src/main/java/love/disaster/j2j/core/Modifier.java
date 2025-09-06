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

package love.disaster.j2j.core;


import love.disaster.j2j.core.common.Optional;
import love.disaster.j2j.core.common.tree.MatchedElement;
import love.disaster.j2j.core.common.tree.WalkedPath;
import love.disaster.j2j.core.exception.SpecException;
import love.disaster.j2j.core.modifier.OpMode;
import love.disaster.j2j.core.modifier.TemplatrSpecBuilder;
import love.disaster.j2j.core.modifier.function.*;
import love.disaster.j2j.core.modifier.function.Math;
import love.disaster.j2j.core.modifier.spec.ModifierCompositeSpec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base Templatr transform that to behave differently based on provided opMode
 */
public abstract class Modifier implements SpecDriven, ContextualTransform {

    private static final Map<String, Function> STOCK_FUNCTIONS = new HashMap<>(  );

    static {
        STOCK_FUNCTIONS.put( "toLower", new Strings.toLowerCase() );
        STOCK_FUNCTIONS.put( "toUpper", new Strings.toUpperCase() );
        STOCK_FUNCTIONS.put( "concat", new Strings.concat() );
        STOCK_FUNCTIONS.put( "join", new Strings.join() );
        STOCK_FUNCTIONS.put( "split", new Strings.split() );
        STOCK_FUNCTIONS.put( "substring", new Strings.substring() );
        STOCK_FUNCTIONS.put( "trim", new Strings.trim() );
        STOCK_FUNCTIONS.put( "leftPad", new Strings.leftPad() );
        STOCK_FUNCTIONS.put( "rightPad", new Strings.rightPad() );
        
        // Enhanced String Functions
        STOCK_FUNCTIONS.put( "replace", new Strings.replace() );
        STOCK_FUNCTIONS.put( "replaceRegex", new Strings.replaceRegex() );
        STOCK_FUNCTIONS.put( "stringContains", new Strings.contains() );
        STOCK_FUNCTIONS.put( "startsWith", new Strings.startsWith() );
        STOCK_FUNCTIONS.put( "endsWith", new Strings.endsWith() );
        STOCK_FUNCTIONS.put( "charAt", new Strings.charAt() );
        STOCK_FUNCTIONS.put( "stringIndexOf", new Strings.indexOf() );
        STOCK_FUNCTIONS.put( "stringLength", new Strings.length() );
        STOCK_FUNCTIONS.put( "repeat", new Strings.repeat() );

        STOCK_FUNCTIONS.put( "min", new Math.min() );
        STOCK_FUNCTIONS.put( "max", new Math.max() );
        STOCK_FUNCTIONS.put( "abs", new Math.abs() );
        STOCK_FUNCTIONS.put( "avg", new Math.avg() );
        STOCK_FUNCTIONS.put( "intSum", new Math.intSum() );
        STOCK_FUNCTIONS.put( "doubleSum", new Math.doubleSum() );
        STOCK_FUNCTIONS.put( "longSum", new Math.longSum() );
        STOCK_FUNCTIONS.put( "intSubtract", new Math.intSubtract() );
        STOCK_FUNCTIONS.put( "doubleSubtract", new Math.doubleSubtract() );
        STOCK_FUNCTIONS.put( "longSubtract", new Math.longSubtract() );
        STOCK_FUNCTIONS.put( "divide", new Math.divide() );
        STOCK_FUNCTIONS.put( "divideAndRound", new Math.divideAndRound() );
        
        // Enhanced Math Functions
        STOCK_FUNCTIONS.put( "multiply", new Math.multiply() );
        STOCK_FUNCTIONS.put( "mod", new Math.mod() );
        STOCK_FUNCTIONS.put( "pow", new Math.pow() );
        STOCK_FUNCTIONS.put( "sqrt", new Math.sqrt() );
        STOCK_FUNCTIONS.put( "ceil", new Math.ceil() );
        STOCK_FUNCTIONS.put( "floor", new Math.floor() );
        STOCK_FUNCTIONS.put( "round", new Math.round() );


        STOCK_FUNCTIONS.put( "toInteger", new Objects.toInteger() );
        STOCK_FUNCTIONS.put( "toDouble", new Objects.toDouble() );
        STOCK_FUNCTIONS.put( "toLong", new Objects.toLong() );
        STOCK_FUNCTIONS.put( "toBoolean", new Objects.toBoolean() );
        STOCK_FUNCTIONS.put( "toString", new Objects.toString() );
        STOCK_FUNCTIONS.put( "size", new Objects.size() );

        STOCK_FUNCTIONS.put( "squashNulls", new Objects.squashNulls() );
        STOCK_FUNCTIONS.put( "recursivelySquashNulls", new Objects.recursivelySquashNulls() );
        STOCK_FUNCTIONS.put( "squashDuplicates", new Objects.squashDuplicates() );
        
        // Enhanced Object Functions
        STOCK_FUNCTIONS.put( "isString", new Objects.isString() );
        STOCK_FUNCTIONS.put( "isNumber", new Objects.isNumber() );
        STOCK_FUNCTIONS.put( "isList", new Objects.isList() );
        STOCK_FUNCTIONS.put( "isMap", new Objects.isMap() );
        STOCK_FUNCTIONS.put( "isBoolean", new Objects.isBoolean() );
        STOCK_FUNCTIONS.put( "inRange", new Objects.inRange() );
        STOCK_FUNCTIONS.put( "defaultIfNull", new Objects.defaultIfNull() );
        STOCK_FUNCTIONS.put( "defaultIfEmpty", new Objects.defaultIfEmpty() );
        STOCK_FUNCTIONS.put( "coerceToNumber", new Objects.coerceToNumber() );
        STOCK_FUNCTIONS.put( "coerceToString", new Objects.coerceToString() );
        STOCK_FUNCTIONS.put( "equals", new Objects.equals() );
        STOCK_FUNCTIONS.put( "notEquals", new Objects.notEquals() );
        STOCK_FUNCTIONS.put( "deepClone", new Objects.deepClone() );

        STOCK_FUNCTIONS.put( "noop", Function.noop );
        STOCK_FUNCTIONS.put( "isPresent", Function.isPresent );
        STOCK_FUNCTIONS.put( "notNull", Function.notNull );
        STOCK_FUNCTIONS.put( "isNull", Function.isNull );

        STOCK_FUNCTIONS.put( "firstElement", new Lists.firstElement() );
        STOCK_FUNCTIONS.put( "lastElement", new Lists.lastElement() );
        STOCK_FUNCTIONS.put( "elementAt", new Lists.elementAt() );
        STOCK_FUNCTIONS.put( "toList", new Lists.toList() );
        STOCK_FUNCTIONS.put( "sort", new Lists.sort() );
        
        // Enhanced List Functions
        STOCK_FUNCTIONS.put( "reverse", new Lists.reverse() );
        STOCK_FUNCTIONS.put( "subList", new Lists.subList() );
        STOCK_FUNCTIONS.put( "listContains", new Lists.contains() );
        STOCK_FUNCTIONS.put( "listIndexOf", new Lists.indexOf() );
        STOCK_FUNCTIONS.put( "listConcat", new Lists.concat() );
        STOCK_FUNCTIONS.put( "distinct", new Lists.distinct() );
        STOCK_FUNCTIONS.put( "filterNotNull", new Lists.filterNotNull() );
        STOCK_FUNCTIONS.put( "listJoin", new Lists.join() );
        STOCK_FUNCTIONS.put( "listSize", new Lists.size() );
        
        STOCK_FUNCTIONS.put( "javascript", new Scripts.javascript() );
        STOCK_FUNCTIONS.put( "python", new Scripts.python() );
        STOCK_FUNCTIONS.put( "beetl", new Scripts.beetl() );
        STOCK_FUNCTIONS.put( "jsonata", new Scripts.jsonata() );
        STOCK_FUNCTIONS.put( "beetlAdvanced", new Scripts.beetlAdvanced() );
        
        // Date and Time Functions
        STOCK_FUNCTIONS.put( "now", new DateTime.now() );
        STOCK_FUNCTIONS.put( "formatDate", new DateTime.formatDate() );
        STOCK_FUNCTIONS.put( "parseDate", new DateTime.parseDate() );
        STOCK_FUNCTIONS.put( "addMillis", new DateTime.addMillis() );
        STOCK_FUNCTIONS.put( "addDays", new DateTime.addDays() );
        STOCK_FUNCTIONS.put( "timeDiff", new DateTime.timeDiff() );
        STOCK_FUNCTIONS.put( "formatDateWithTZ", new DateTime.formatDateWithTZ() );
    }

    private final ModifierCompositeSpec rootSpec;

    @SuppressWarnings( "unchecked" )
    private Modifier(Object spec, OpMode opMode, Map<String, Function> functionsMap ) {
        if ( spec == null ){
            throw new SpecException( opMode.name() + " expected a spec of Map type, got 'null'." );
        }
        if ( ! ( spec instanceof Map ) ) {
            throw new SpecException( opMode.name() + " expected a spec of Map type, got " + spec.getClass().getSimpleName() );
        }

        if(functionsMap == null || functionsMap.isEmpty()) {
            throw new SpecException( opMode.name() + " expected a populated functions' map type, got " + (functionsMap == null?"null":"empty") );
        }

        functionsMap = Collections.unmodifiableMap( functionsMap );
        TemplatrSpecBuilder templatrSpecBuilder = new TemplatrSpecBuilder( opMode, functionsMap );
        rootSpec = new ModifierCompositeSpec( ROOT_KEY, (Map<String, Object>) spec, opMode, templatrSpecBuilder );
    }

    @Override
    public Object transform( final Object input, final Map<String, Object> context ) {

        Map<String, Object> contextWrapper = new HashMap<>(  );
        contextWrapper.put( ROOT_KEY, context );

        MatchedElement rootLpe = new MatchedElement( ROOT_KEY );
        WalkedPath walkedPath = new WalkedPath();
        walkedPath.add( input, rootLpe );

        rootSpec.apply( ROOT_KEY, Optional.of( input), walkedPath, null, contextWrapper );
        return input;
    }

    /**
     * This variant of modifier creates the key/index is missing,
     * and overwrites the value if present
     */
    public static final class Overwritr extends Modifier {

        public Overwritr( Object spec ) {
            this( spec, STOCK_FUNCTIONS );
        }

        public Overwritr( Object spec, Map<String, Function> functionsMap ) {
            super( spec, OpMode.OVERWRITR, functionsMap );
        }
    }

    /**
     * This variant of modifier only writes when the key/index is missing
     */
    public static final class Definr extends Modifier {

        public Definr( final Object spec ) {
            this( spec, STOCK_FUNCTIONS );
        }

        public Definr( Object spec, Map<String, Function> functionsMap ) {
            super( spec, OpMode.DEFINER, functionsMap );
        }
    }

    /**
     * This variant of modifier only writes when the key/index is missing or the value is null
     */
    public static class Defaultr extends Modifier {

        public Defaultr( final Object spec ) {
            this( spec, STOCK_FUNCTIONS );
        }

        public Defaultr( Object spec, Map<String, Function> functionsMap ) {
            super( spec, OpMode.DEFAULTR, functionsMap );
        }
    }
}
