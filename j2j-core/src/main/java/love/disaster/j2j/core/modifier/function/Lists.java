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

@SuppressWarnings( "deprecated" )
public class Lists {

    /**
     * Given a list, return the first element
     */
    public static final class firstElement extends Function.ListFunction {

        @Override
        protected Optional applyList( final List argList ) {
            return argList.size() > 0 ?
                    Optional.of( argList.get( 0 ) ) :
                    Optional.empty();
        }
    }

    /**
     * Given a list, return the last element
     */
    public static final class lastElement extends Function.ListFunction {

        @Override
        protected Optional applyList( final List argList ) {
            return argList.size() > 0 ?
                    Optional.of( argList.get( argList.size() - 1 ) ) :
                    Optional.empty();
        }
    }

    /**
     * Given an index at arg[0], and a list at arg[1] or args[1...N], return element at index of list or array
     */
    public static final class elementAt extends Function.ArgDrivenListFunction<Integer> {

        @Override
        protected Optional<Object> applyList( final Integer specialArg, final List<Object> args ) {
            if ( specialArg != null && args != null && args.size() > specialArg ) {
                return Optional.of( args.get( specialArg ) );
            }
            return Optional.empty();
        }
    }

    /**
     * Given an arbitrary number of arguments, return them as list
     */
    public static final class toList extends Function.BaseFunction<List> {
        @Override
        protected Optional<Object> applyList( final List input ) {
            return Optional.<Object>of( input );
        }

        @Override
        protected Optional<List> applySingle( final Object arg ) {
            return Optional.<List>of( Arrays.asList( arg ) );
        }
    }

    /**
     * Given an arbitrary list of items, returns a new array of them in sorted state
     */
    public static final class sort extends Function.BaseFunction {

        @Override
        protected Optional applyList( final List argList ) {
            try {
                Object[] dest = argList.toArray();
                Arrays.sort( dest );
                return Optional.<Object>of( dest );
            }
            // if any of the elements are not Comparable<?> it'll throw a ClassCastException
            catch(Exception ignored) {
                return Optional.empty();
            }
        }

        @Override
        protected Optional applySingle( final Object arg ) {
            return Optional.of( arg );
        }
    }

    // Enhanced List Functions - Additional Operations
    
    /**
     * Reverse the order of elements in a list
     */
    public static final class reverse extends Function.SingleFunction<List> {
        @Override
        protected Optional<List> applySingle(final Object arg) {
            if (!(arg instanceof List)) {
                return Optional.empty();
            }
            
            List<Object> inputList = (List<Object>) arg;
            List<Object> result = new ArrayList<>(inputList);
            java.util.Collections.reverse(result);
            return Optional.of(result);
        }
    }
    
    /**
     * Get a sublist from start to end index
     */
    public static final class subList extends Function.ArgDrivenListFunction<List> {
        @Override
        protected Optional<Object> applyList(List sourceList, List<Object> args) {
            if (sourceList == null || args == null || args.size() != 2) {
                return Optional.empty();
            }
            
            if (!(args.get(0) instanceof Integer && args.get(1) instanceof Integer)) {
                return Optional.empty();
            }
            
            int start = (Integer) args.get(0);
            int end = (Integer) args.get(1);
            
            if (start < 0 || end < start || end > sourceList.size()) {
                return Optional.empty();
            }
            
            return Optional.of(sourceList.subList(start, end));
        }
    }
    
    /**
     * Check if list contains an element
     */
    public static final class contains extends Function.ArgDrivenSingleFunction<List, Boolean> {
        @Override
        protected Optional<Boolean> applySingle(List sourceList, Object searchValue) {
            if (sourceList == null) {
                return Optional.of(false);
            }
            
            return Optional.of(sourceList.contains(searchValue));
        }
    }
    
    /**
     * Find index of element in list
     */
    public static final class indexOf extends Function.ArgDrivenSingleFunction<List, Integer> {
        @Override
        protected Optional<Integer> applySingle(List sourceList, Object searchValue) {
            if (sourceList == null) {
                return Optional.empty();
            }
            
            int index = sourceList.indexOf(searchValue);
            return index >= 0 ? Optional.of(index) : Optional.empty();
        }
    }
    
    /**
     * Concatenate multiple lists
     */
    public static final class concat extends Function.ListFunction {
        @Override
        protected Optional<Object> applyList(final List<Object> argList) {
            List<Object> result = new ArrayList<>();
            
            for (Object arg : argList) {
                if (arg instanceof List) {
                    result.addAll((List<?>) arg);
                } else {
                    result.add(arg);
                }
            }
            
            return Optional.of(result);
        }
    }
    
    /**
     * Remove duplicates from list while preserving order
     */
    public static final class distinct extends Function.SingleFunction<List> {
        @Override
        protected Optional<List> applySingle(final Object arg) {
            if (!(arg instanceof List)) {
                return Optional.empty();
            }
            
            List<Object> inputList = (List<Object>) arg;
            List<Object> result = new ArrayList<>();
            
            for (Object item : inputList) {
                if (!result.contains(item)) {
                    result.add(item);
                }
            }
            
            return Optional.of(result);
        }
    }
    
    /**
     * Filter list by keeping only non-null elements
     */
    public static final class filterNotNull extends Function.SingleFunction<List> {
        @Override
        protected Optional<List> applySingle(final Object arg) {
            if (!(arg instanceof List)) {
                return Optional.empty();
            }
            
            List<Object> inputList = (List<Object>) arg;
            List<Object> result = new ArrayList<>();
            
            for (Object item : inputList) {
                if (item != null) {
                    result.add(item);
                }
            }
            
            return Optional.of(result);
        }
    }
    
    /**
     * Join list elements with a separator
     */
    public static final class join extends Function.ArgDrivenSingleFunction<String, String> {
        @Override
        protected Optional<String> applySingle(String separator, Object arg) {
            if (!(arg instanceof List)) {
                return Optional.empty();
            }
            
            List<Object> inputList = (List<Object>) arg;
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < inputList.size(); i++) {
                Object item = inputList.get(i);
                if (item != null) {
                    sb.append(item.toString());
                    if (i < inputList.size() - 1) {
                        sb.append(separator);
                    }
                }
            }
            
            return Optional.of(sb.toString());
        }
    }
    
    /**
     * Get list size (same as Objects.size but specifically for lists)
     */
    public static final class size extends Function.SingleFunction<Integer> {
        @Override
        protected Optional<Integer> applySingle(final Object arg) {
            if (arg instanceof List) {
                return Optional.of(((List<?>) arg).size());
            }
            return Optional.empty();
        }
    }
}
