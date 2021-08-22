package pbouda.jjf.checker;

import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.time.Duration;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.fail;

public class AssertOutput implements AutoCloseable {

    private final PrintStream old;
    private final Predicate<String> predicate;
    private final ByteArrayOutputStream content;

    public AssertOutput(Predicate<String> predicate) {
        this.predicate = predicate;
        this.content = new ByteArrayOutputStream();
        this.old = System.out;
        System.setOut(new PrintStream(content));
    }

    public void waitForAssertion(Duration duration) {
        try {
            long start = System.nanoTime();
            BufferedReader reader = new BufferedReader(new StringReader(content.toString()));
            for (String line = reader.readLine(); line != null && !predicate.test(line); line = reader.readLine()) {
                long now = System.nanoTime();
                if ((now - start) > duration.toNanos()) {
                    fail();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        System.setOut(old);
    }
}