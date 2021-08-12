package pbouda.jjf.checker;

import pbouda.jjf.Flag;
import pbouda.jjf.Output;

import java.util.Map;

public record EnvVariableChecker(String variable) implements Checker {

    @Override
    public void check(Map<Flag, String> flags) {
        String value = System.getenv().get(variable);
        if (value == null || value.isBlank()) {
            Output.error("Environment variable is not properly set: " + variable, true);
        }
    }
}
