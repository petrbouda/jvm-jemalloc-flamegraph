package pbouda.jjf.checker;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Predicate;

class EnvVariableCheckerTest {

    @Test
    public void envDoesNotExist() {
        Predicate<String> predicate = line -> line.startsWith("Environment variable is not properly set");
        try (AssertOutput assertion = new AssertOutput(predicate)) {
            EnvVariableChecker checker = new EnvVariableChecker("MY_VARIABLE");
            checker.check(null);
            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }

    @Test
    public void envDoesExist() {
        Predicate<String> predicate = line -> line.startsWith("Environment variable is not properly set");
        try (AssertOutput assertion = new AssertOutput(predicate)) {

            assertion.waitForAssertion(Duration.ofSeconds(1));
        }
    }
}