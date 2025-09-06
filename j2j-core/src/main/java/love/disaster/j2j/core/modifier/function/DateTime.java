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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Date and time functions for J2J modifier
 */
@SuppressWarnings("deprecated")
public class DateTime {

    /**
     * Get current timestamp as milliseconds since epoch
     */
    public static final class now extends Function.BaseFunction<Long> {
        @Override
        protected Optional<Object> applyList(final List<Object> input) {
            return Optional.of(System.currentTimeMillis());
        }

        @Override
        protected Optional<Long> applySingle(final Object arg) {
            return Optional.of(System.currentTimeMillis());
        }
    }

    /**
     * Format timestamp to string using pattern
     */
    public static final class formatDate extends Function.ArgDrivenSingleFunction<String, String> {
        @Override
        protected Optional<String> applySingle(String pattern, Object timestamp) {
            if (pattern == null || timestamp == null) {
                return Optional.empty();
            }

            try {
                long time;
                if (timestamp instanceof Number) {
                    time = ((Number) timestamp).longValue();
                } else if (timestamp instanceof String) {
                    time = Long.parseLong((String) timestamp);
                } else {
                    return Optional.empty();
                }

                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                Date date = new Date(time);
                return Optional.of(sdf.format(date));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    /**
     * Parse date string to timestamp
     */
    public static final class parseDate extends Function.ArgDrivenSingleFunction<String, Long> {
        @Override
        protected Optional<Long> applySingle(String pattern, Object dateString) {
            if (pattern == null || !(dateString instanceof String)) {
                return Optional.empty();
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                Date date = sdf.parse((String) dateString);
                return Optional.of(date.getTime());
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    /**
     * Add milliseconds to timestamp
     */
    public static final class addMillis extends Function.ArgDrivenSingleFunction<Number, Long> {
        @Override
        protected Optional<Long> applySingle(Number millis, Object timestamp) {
            if (millis == null || timestamp == null) {
                return Optional.empty();
            }

            try {
                long time;
                if (timestamp instanceof Number) {
                    time = ((Number) timestamp).longValue();
                } else if (timestamp instanceof String) {
                    time = Long.parseLong((String) timestamp);
                } else {
                    return Optional.empty();
                }

                return Optional.of(time + millis.longValue());
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    /**
     * Add days to timestamp
     */
    public static final class addDays extends Function.ArgDrivenSingleFunction<Number, Long> {
        @Override
        protected Optional<Long> applySingle(Number days, Object timestamp) {
            if (days == null || timestamp == null) {
                return Optional.empty();
            }

            try {
                long time;
                if (timestamp instanceof Number) {
                    time = ((Number) timestamp).longValue();
                } else if (timestamp instanceof String) {
                    time = Long.parseLong((String) timestamp);
                } else {
                    return Optional.empty();
                }

                long millisecondsToAdd = days.longValue() * 24 * 60 * 60 * 1000L;
                return Optional.of(time + millisecondsToAdd);
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    /**
     * Get difference between two timestamps in milliseconds
     */
    public static final class timeDiff extends Function.ListFunction {
        @Override
        protected Optional<Object> applyList(final List<Object> argList) {
            if (argList == null || argList.size() != 2) {
                return Optional.empty();
            }

            try {
                long time1, time2;

                Object ts1 = argList.get(0);
                Object ts2 = argList.get(1);

                if (ts1 instanceof Number) {
                    time1 = ((Number) ts1).longValue();
                } else if (ts1 instanceof String) {
                    time1 = Long.parseLong((String) ts1);
                } else {
                    return Optional.empty();
                }

                if (ts2 instanceof Number) {
                    time2 = ((Number) ts2).longValue();
                } else if (ts2 instanceof String) {
                    time2 = Long.parseLong((String) ts2);
                } else {
                    return Optional.empty();
                }

                return Optional.of(Math.abs(time2 - time1));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    /**
     * Format date with timezone
     */
    public static final class formatDateWithTZ extends Function.ArgDrivenListFunction<String> {
        @Override
        protected Optional<Object> applyList(String pattern, List<Object> args) {
            if (pattern == null || args == null || args.size() != 2) {
                return Optional.empty();
            }

            Object timestamp = args.get(0);
            Object timezone = args.get(1);

            if (!(timezone instanceof String)) {
                return Optional.empty();
            }

            try {
                long time;
                if (timestamp instanceof Number) {
                    time = ((Number) timestamp).longValue();
                } else if (timestamp instanceof String) {
                    time = Long.parseLong((String) timestamp);
                } else {
                    return Optional.empty();
                }

                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setTimeZone(TimeZone.getTimeZone((String) timezone));
                Date date = new Date(time);
                return Optional.of(sdf.format(date));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }
}