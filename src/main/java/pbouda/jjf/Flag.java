package pbouda.jjf;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Flag {

    PID("--pid"),
    PROFILE("--profile_dir"),
    OUTPUT("--output"),
    FILTER("--filter"),
    LATEST("--latest", true),
    ALL_STACKS("--allstacks", true),
    OFFSET("--offsets", true);

    private static final String COMMAND_DELIMITER = "--";

    private final String name;
    private final boolean valueless;

    private static final Map<String, Flag> FLAGS;

    static {
        FLAGS = Arrays.stream(values())
                .collect(Collectors.toUnmodifiableMap(flag -> flag.name, Function.identity()));
    }

    Flag(String name) {
        this(name, false);
    }

    Flag(String name, boolean valueless) {
        this.name = name;
        this.valueless = valueless;
    }

    public static Map<Flag, String> resolve(String[] args) {
        Map<Flag, String> params = new EnumMap<>(Flag.class);
        for (int i = 0; i < args.length; i++) {
            String name = args[i];
            if (COMMAND_DELIMITER.equals(name)) {
                break;
            }

            Flag flag = Flag.FLAGS.get(name);
            if (flag.valueless) {
                params.put(flag, null);
            } else {
                i += 1;
                String value = args[i];
                params.put(flag, value);
            }
        }
        return params;
    }
}