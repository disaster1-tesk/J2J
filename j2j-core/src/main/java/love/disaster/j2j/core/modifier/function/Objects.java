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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Objects {

    /**
     * Given any object, returns, if possible. its Java number equivalent wrapped in Optional
     * Interprets String as Number
     *
     * toNumber("123") == Optional.of(123)
     * toNumber("-123") == Optional.of(-123)
     * toNumber("12.3") == Optional.of(12.3)
     *
     * toNumber("abc") == Optional.empty()
     * toNumber(null) == Optional.empty()
     *
     * also, see: MathTest#testNitPicks
     *
     */
    public static Optional<? extends Number> toNumber(Object arg) {
        if ( arg instanceof Number ) {
            return Optional.of( ( (Number) arg ));
        }
        else if(arg instanceof String) {
            try {
                return Optional.of( (Number) Integer.parseInt( (String) arg ) );
            }
            catch(Exception ignored) {}
            try {
                return Optional.of( (Number) Long.parseLong( (String) arg ) );
            }
            catch(Exception ignored) {}
            try {
                return Optional.of( (Number) Double.parseDouble( (String) arg ) );
            }
            catch(Exception ignored) {}
            return Optional.empty();
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns int value of argument, if possible, wrapped in Optional
     * Interprets String as Number
     */
    public static Optional<Integer> toInteger(Object arg) {
        if ( arg instanceof Number ) {
            return Optional.of( ( (Number) arg ).intValue() );
        }
        else if(arg instanceof String) {
            Optional<? extends Number> optional = toNumber( arg );
            if ( optional.isPresent() ) {
                return Optional.of( optional.get().intValue() );
            }
            else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns long value of argument, if possible, wrapped in Optional
     * Interprets String as Number
     */
    public static Optional<Long> toLong(Object arg) {
        if ( arg instanceof Number ) {
            return Optional.of( ( (Number) arg ).longValue() );
        }
        else if(arg instanceof String) {
            Optional<? extends Number> optional = toNumber( arg );
            if ( optional.isPresent() ) {
                return Optional.of( optional.get().longValue() );
            }
            else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns double value of argument, if possible, wrapped in Optional
     * Interprets String as Number
     */
    public static Optional<Double> toDouble(Object arg) {
        if ( arg instanceof Number ) {
            return Optional.of( ( (Number) arg ).doubleValue() );
        }
        else if(arg instanceof String) {
            Optional<? extends Number> optional = toNumber( arg );
            if ( optional.isPresent() ) {
                return Optional.of( optional.get().doubleValue() );
            }
            else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns boolean value of argument, if possible, wrapped in Optional
     * Interprets Strings "true" & "false" as boolean
     */
    public static Optional<Boolean> toBoolean(Object arg) {
        if ( arg instanceof Boolean ) {
            return Optional.of( (Boolean) arg );
        }
        else if(arg instanceof String) {
            if("true".equalsIgnoreCase( (String)arg )) {
                return Optional.of( Boolean.TRUE );
            }
            else if("false".equalsIgnoreCase( (String)arg )) {
                return Optional.of( Boolean.FALSE );
            }
        }
        return Optional.empty();
    }

    /**
     * Returns String representation of argument, wrapped in Optional
     *
     * for array argument, returns Arrays.toString()
     * for others, returns Objects.toString()
     *
     * Note: this method does not return Optional.empty()
     */
    public static Optional<String> toString(Object arg) {
        if ( arg instanceof String ) {
            return Optional.of( (String) arg );
        }
        else if ( arg instanceof Object[] ) {
            return Optional.of( Arrays.toString( (Object[] )arg ) );
        }
        else {
            return Optional.of( java.util.Objects.toString( arg ) );
        }
    }

    /**
     * Squashes nulls in a list or map.
     *
     * Modifies the data.
     */
    public static void squashNulls( Object input ) {
        if ( input instanceof List ) {
            List inputList = (List) input;
            inputList.removeIf( java.util.Objects::isNull );
        }
        else if ( input instanceof Map ) {
            Map<String,Object> inputMap = (Map<String,Object>) input;

            List<String> keysToNuke = new ArrayList<>();
            for (Map.Entry<String,Object> entry : inputMap.entrySet()) {
                if ( entry.getValue() == null ) {
                    keysToNuke.add( entry.getKey() );
                }
            }

            inputMap.keySet().removeAll( keysToNuke );
        }
    }

    /**
     * Recursively squash nulls in maps and lists.
     *
     * Modifies the data.
     */
    public static void recursivelySquashNulls(Object input) {

        // Makes two passes thru the data.
        Objects.squashNulls( input );

        if ( input instanceof List ) {
            List inputList = (List) input;
            inputList.forEach( i -> recursivelySquashNulls( i ) );
        }
        else if ( input instanceof Map ) {
            Map<String,Object> inputMap = (Map<String,Object>) input;

            for (Map.Entry<String,Object> entry : inputMap.entrySet()) {
                recursivelySquashNulls( entry.getValue() );
            }
        }
    }

    /**
     * Squashes/Deletes duplicates in lists.
     *
     * Modifies the data.
     */
    public static Optional<Object> squashDuplicates( Object input ) {
        if ( input instanceof List ) {
            List inputList = (List) input;
            return Optional.of(inputList.stream().distinct().collect(Collectors.toList()));
        }
        return Optional.of(input);
    }

    public static final class toInteger extends Function.SingleFunction<Integer> {
        @Override
        protected Optional<Integer> applySingle( final Object arg ) {
            return toInteger( arg );
        }
    }

    public static final class toLong extends Function.SingleFunction<Long> {
        @Override
        protected Optional<Long> applySingle( final Object arg ) {
            return toLong( arg );
        }
    }

    public static final class toDouble extends Function.SingleFunction<Double> {
        @Override
        protected Optional<Double> applySingle( final Object arg ) {
            return toDouble( arg );
        }
    }

    public static final class toBoolean extends Function.SingleFunction<Boolean> {
        @Override
        protected Optional<Boolean> applySingle( final Object arg ) {
            return toBoolean( arg );
        }
    }

    public static final class toString extends Function.SingleFunction<String> {
        @Override
        protected Optional<String> applySingle( final Object arg ) {
            return Objects.toString( arg );
        }
    }

    public static final class squashNulls extends Function.SquashFunction<Object> {
        @Override
        protected Optional<Object> applySingle( final Object arg ) {
            Objects.squashNulls( arg );
            return Optional.of( arg );
        }
    }

    public static final class recursivelySquashNulls extends Function.SquashFunction<Object> {
        @Override
        protected Optional<Object> applySingle( final Object arg ) {
            Objects.recursivelySquashNulls( arg );
            return Optional.of( arg );
        }
    }

    public static final class squashDuplicates extends Function.SquashFunction<Object> {
        @Override
        protected Optional<Object> applySingle( final Object arg ) {
            return Objects.squashDuplicates( arg );
        }
    }

    /**
     * Size is a special snowflake and needs specific care
     */
    public static final class size implements Function {

        @Override
        public Optional<Object> apply(Object... args) {
            if(args.length == 0) {
                return Optional.empty();
            }
            else if(args.length == 1) {
                if(args[0] == null) {
                    return Optional.empty();
                }
                else if(args[0] instanceof List ) {
                    return Optional.of(((List) args[0]).size());
                }
                else if(args[0] instanceof String) {
                    return Optional.of( ((String) args[0]).length() );
                }
                else if(args[0] instanceof Map) {
                    return Optional.of( ((Map) args[0]).size() );
                }
                else {
                    return Optional.empty();
                }
            }
            else {
                return Optional.of(args.length);
            }
        }
    }

    // Enhanced Object Functions - Validation and Utility Operations
    
    /**
     * Type checking functions
     */
    public static final class isString extends Function.SingleFunction<Boolean> {
        @Override
        protected Optional<Boolean> applySingle(final Object arg) {
            return Optional.of(arg instanceof String);
        }
    }
    
    public static final class isNumber extends Function.SingleFunction<Boolean> {
        @Override
        protected Optional<Boolean> applySingle(final Object arg) {
            return Optional.of(arg instanceof Number || 
                (arg instanceof String && toNumber(arg).isPresent()));
        }
    }
    
    public static final class isList extends Function.SingleFunction<Boolean> {
        @Override
        protected Optional<Boolean> applySingle(final Object arg) {
            return Optional.of(arg instanceof List);
        }
    }
    
    public static final class isMap extends Function.SingleFunction<Boolean> {
        @Override
        protected Optional<Boolean> applySingle(final Object arg) {
            return Optional.of(arg instanceof Map);
        }
    }
    
    public static final class isBoolean extends Function.SingleFunction<Boolean> {
        @Override
        protected Optional<Boolean> applySingle(final Object arg) {
            return Optional.of(arg instanceof Boolean || 
                (arg instanceof String && toBoolean(arg).isPresent()));
        }
    }
    
    /**
     * Range validation functions
     */
    public static final class inRange extends Function.ArgDrivenListFunction<Number> {
        @Override
        protected Optional<Object> applyList(Number value, List<Object> args) {
            if (args == null || args.size() != 2) {
                return Optional.empty();
            }
            
            Optional<? extends Number> minOpt = toNumber(args.get(0));
            Optional<? extends Number> maxOpt = toNumber(args.get(1));
            
            if (!minOpt.isPresent() || !maxOpt.isPresent()) {
                return Optional.empty();
            }
            
            double val = value.doubleValue();
            double min = minOpt.get().doubleValue();
            double max = maxOpt.get().doubleValue();
            
            return Optional.of(val >= min && val <= max);
        }
    }
    
    /**
     * Empty/null checking with defaults
     */
    public static final class defaultIfNull extends Function.ArgDrivenSingleFunction<Object, Object> {
        @Override
        protected Optional<Object> applySingle(Object value, Object defaultValue) {
            return Optional.of(value != null ? value : defaultValue);
        }
    }
    
    public static final class defaultIfEmpty extends Function.ArgDrivenSingleFunction<Object, Object> {
        @Override
        protected Optional<Object> applySingle(Object value, Object defaultValue) {
            if (value == null) {
                return Optional.of(defaultValue);
            }
            
            if (value instanceof String && ((String) value).isEmpty()) {
                return Optional.of(defaultValue);
            }
            
            if (value instanceof List && ((List<?>) value).isEmpty()) {
                return Optional.of(defaultValue);
            }
            
            if (value instanceof Map && ((Map<?, ?>) value).isEmpty()) {
                return Optional.of(defaultValue);
            }
            
            return Optional.of(value);
        }
    }
    
    /**
     * Coerce value to a specific type with validation
     */
    public static final class coerceToNumber extends Function.SingleFunction<Number> {
        @Override
        protected Optional<Number> applySingle(final Object arg) {
            return (Optional<Number>) toNumber(arg);
        }
    }
    
    public static final class coerceToString extends Function.SingleFunction<String> {
        @Override
        protected Optional<String> applySingle(final Object arg) {
            if (arg == null) {
                return Optional.empty();
            }
            return Optional.of(String.valueOf(arg));
        }
    }
    
    /**
     * Object comparison functions
     */
    public static final class equals extends Function.ArgDrivenSingleFunction<Object, Boolean> {
        @Override
        protected Optional<Boolean> applySingle(Object value1, Object value2) {
            return Optional.of(java.util.Objects.equals(value1, value2));
        }
    }
    
    public static final class notEquals extends Function.ArgDrivenSingleFunction<Object, Boolean> {
        @Override
        protected Optional<Boolean> applySingle(Object value1, Object value2) {
            return Optional.of(!java.util.Objects.equals(value1, value2));
        }
    }
    
    /**
     * Deep clone function for objects
     */
    public static final class deepClone extends Function.SingleFunction<Object> {
        @Override
        protected Optional<Object> applySingle(final Object arg) {
            if (arg == null) {
                return Optional.of(null);
            }
            
            try {
                // Use existing DeepCopy utility if available
                return Optional.of(love.disaster.j2j.core.common.DeepCopy.simpleDeepCopy(arg));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }
}