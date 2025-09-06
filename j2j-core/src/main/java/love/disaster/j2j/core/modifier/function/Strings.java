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

import java.util.Arrays;
import java.util.List;

@SuppressWarnings( "deprecated" )
public class Strings {

    public static final class toLowerCase extends Function.SingleFunction<String> {
        @Override
        protected Optional<String> applySingle( final Object arg ) {

            if ( ! (arg instanceof String) ) {
                return Optional.empty();
            }

            String argString = (String) arg;

            return Optional.of( argString.toLowerCase() );
        }
    }

    public static final class toUpperCase extends Function.SingleFunction<String> {
        @Override
        protected Optional<String> applySingle( final Object arg ) {

            if ( ! (arg instanceof String) ) {
                return Optional.empty();
            }

            String argString = (String) arg;

            return Optional.of( argString.toUpperCase() );
        }
    }

    public static final class trim extends Function.SingleFunction<String> {
        @Override
        protected Optional<String> applySingle( final Object arg ) {

            if ( ! (arg instanceof String) ) {
                return Optional.empty();
            }

            String argString = (String) arg;

            return Optional.of( argString.trim() );
        }
    }

    public static final class concat extends Function.ListFunction {
        @Override
        protected Optional<Object> applyList( final List<Object> argList ) {
            StringBuilder sb = new StringBuilder(  );
            for(Object arg: argList ) {
                if ( arg != null ) {
                    sb.append(arg.toString() );
                }
            }
            return Optional.of(sb.toString());
        }
    }

    public static final class substring extends Function.ListFunction {

        @Override
        protected Optional<Object> applyList(List<Object> argList) {

            // There is only one path that leads to success and many
            //  ways for this to fail.   So using a do/while loop
            //  to make the bailing easy.
            do {

                // if argList is null or not the right size; bail
                if(argList == null || argList.size() != 3 ) {
                    break;
                }

                if ( ! ( argList.get(0) instanceof String &&
                         argList.get(1) instanceof Integer &&
                         argList.get(2) instanceof Integer ) ) {
                    break;
                }

                // If we get here, then all these casts should work.
                String tuna = (String) argList.get(0);
                int start = (Integer) argList.get(1);
                int end = (Integer) argList.get(2);

                // do start and end make sense?
                if ( start >= end || start < 0 || end < 1 || end > tuna.length() ) {
                    break;
                }

                return Optional.of(tuna.substring(start, end));

            } while( false );

            // if we got here, then return an Optional.empty.
            return Optional.empty();
        }
    }

    @SuppressWarnings( "unchecked" )
    public static final class join extends Function.ArgDrivenListFunction<String> {

        @Override
        protected Optional<Object> applyList( final String specialArg, final List<Object> args ) {
            StringBuilder sb = new StringBuilder(  );
            for(int i=0; i < args.size(); i++) {
                Object arg = args.get(i);
                if (arg != null ) {
                    String argString = arg.toString();
                    if( !("".equals( argString ))) {
                        sb.append( argString );
                        if ( i < args.size() - 1 ) {
                            sb.append( specialArg );
                        }
                    }
                }
            }
            return Optional.of( sb.toString() );
        }
    }

    public static final class split extends Function.ArgDrivenSingleFunction<String, List> {
      @Override
      protected Optional<List> applySingle(final String separator, final Object source) {
        if (source == null || separator == null) {
          return Optional.empty();
        }
        else if ( source instanceof String ) {
          // only try to split input strings
          String inputString = (String) source;
          return Optional.of( Arrays.asList(inputString.split(separator)) );
        }
        else {
          return Optional.empty();
        }
      }
    }


    public static final class leftPad extends Function.ArgDrivenListFunction<String> {
        @Override
        protected Optional<Object> applyList(String source, List<Object> args) {

            return padString( true, source, args );
        }
    }

    public static final class rightPad extends Function.ArgDrivenListFunction<String> {
        @Override
        protected Optional<Object> applyList(String source, List<Object> args) {

            return padString( false, source, args );
        }
    }

    private static Optional<Object> padString( boolean leftPad, String source, List<Object> args ) {

        // There is only one path that leads to success and many
        //  ways for this to fail.   So using a do/while loop
        //  to make the bailing easy.
        do {

            if(source == null || args == null ) {
                break;
            }

            if ( ! ( args.get(0) instanceof Integer &&
                     args.get(1) instanceof String ) ) {
                break;
            }

            Integer width = (Integer) args.get(0);

            // if the width param is stupid; bail
            if ( width <= 0 || width > 500 ) {
                break;
            }

            String filler = (String) args.get(1);

            // filler can only be a single char
            //  otherwise the math becomes hard
            if ( filler.length() != 1 ) {
                break;
            }

            char fillerChar = filler.charAt( 0 );

            // if the desired width of the overall padding is smaller than
            //  the source string, then just return the source string.
            if( width <= source.length() ) {
                return Optional.of( source );
            }

            int padLength = width - source.length();
            char[] padArray = new char[padLength];

            Arrays.fill( padArray, fillerChar );

            StringBuilder sb = new StringBuilder();

            if ( leftPad ) {
                sb.append( padArray ).append( source );
            }
            else {
                sb.append( source ).append( padArray );
            }

            return Optional.of( sb.toString() );

        } while ( false );

        return Optional.empty();
    }

    // Enhanced String Functions - Additional Operations
    
    /**
     * Replace all occurrences of a substring with another string
     */
    public static final class replace extends Function.ArgDrivenListFunction<String> {
        @Override
        protected Optional<Object> applyList(String source, List<Object> args) {
            if (source == null || args == null || args.size() != 2) {
                return Optional.empty();
            }
            
            if (!(args.get(0) instanceof String && args.get(1) instanceof String)) {
                return Optional.empty();
            }
            
            String target = (String) args.get(0);
            String replacement = (String) args.get(1);
            
            return Optional.of(source.replace(target, replacement));
        }
    }
    
    /**
     * Replace using regular expressions
     */
    public static final class replaceRegex extends Function.ArgDrivenListFunction<String> {
        @Override
        protected Optional<Object> applyList(String source, List<Object> args) {
            if (source == null || args == null || args.size() != 2) {
                return Optional.empty();
            }
            
            if (!(args.get(0) instanceof String && args.get(1) instanceof String)) {
                return Optional.empty();
            }
            
            try {
                String regex = (String) args.get(0);
                String replacement = (String) args.get(1);
                
                return Optional.of(source.replaceAll(regex, replacement));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }
    
    /**
     * Check if string contains substring
     */
    public static final class contains extends Function.ArgDrivenSingleFunction<String, Boolean> {
        @Override
        protected Optional<Boolean> applySingle(String source, Object arg) {
            if (source == null || !(arg instanceof String)) {
                return Optional.empty();
            }
            
            String substring = (String) arg;
            return Optional.of(source.contains(substring));
        }
    }
    
    /**
     * Check if string starts with prefix
     */
    public static final class startsWith extends Function.ArgDrivenSingleFunction<String, Boolean> {
        @Override
        protected Optional<Boolean> applySingle(String source, Object arg) {
            if (source == null || !(arg instanceof String)) {
                return Optional.empty();
            }
            
            String prefix = (String) arg;
            return Optional.of(source.startsWith(prefix));
        }
    }
    
    /**
     * Check if string ends with suffix
     */
    public static final class endsWith extends Function.ArgDrivenSingleFunction<String, Boolean> {
        @Override
        protected Optional<Boolean> applySingle(String source, Object arg) {
            if (source == null || !(arg instanceof String)) {
                return Optional.empty();
            }
            
            String suffix = (String) arg;
            return Optional.of(source.endsWith(suffix));
        }
    }
    
    /**
     * Get character at specific index
     */
    public static final class charAt extends Function.ArgDrivenSingleFunction<String, String> {
        @Override
        protected Optional<String> applySingle(String source, Object arg) {
            if (source == null || !(arg instanceof Integer)) {
                return Optional.empty();
            }
            
            int index = (Integer) arg;
            if (index < 0 || index >= source.length()) {
                return Optional.empty();
            }
            
            return Optional.of(String.valueOf(source.charAt(index)));
        }
    }
    
    /**
     * Find index of substring
     */
    public static final class indexOf extends Function.ArgDrivenSingleFunction<String, Integer> {
        @Override
        protected Optional<Integer> applySingle(String source, Object arg) {
            if (source == null || !(arg instanceof String)) {
                return Optional.empty();
            }
            
            String substring = (String) arg;
            int index = source.indexOf(substring);
            return index >= 0 ? Optional.of(index) : Optional.empty();
        }
    }
    
    /**
     * Get string length
     */
    public static final class length extends Function.SingleFunction<Integer> {
        @Override
        protected Optional<Integer> applySingle(final Object arg) {
            if (!(arg instanceof String)) {
                return Optional.empty();
            }
            
            String str = (String) arg;
            return Optional.of(str.length());
        }
    }
    
    /**
     * Repeat string n times
     */
    public static final class repeat extends Function.ArgDrivenSingleFunction<String, String> {
        @Override
        protected Optional<String> applySingle(String source, Object arg) {
            if (source == null || !(arg instanceof Integer)) {
                return Optional.empty();
            }
            
            int times = (Integer) arg;
            if (times < 0 || times > 1000) { // Prevent excessive memory usage
                return Optional.empty();
            }
            
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < times; i++) {
                sb.append(source);
            }
            
            return Optional.of(sb.toString());
        }
    }
}