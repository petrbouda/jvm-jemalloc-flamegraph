package pbouda.jjf.checker;

import pbouda.jjf.Flag;
import pbouda.jjf.Output;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JeprofChecker implements Checker {

    @Override
    public void check(Map<Flag, String> flags) {
        try {
            Process process = new ProcessBuilder()
                    .command("bash", "-c", "jeprof --version")
                    .start();

            process.waitFor(1, TimeUnit.SECONDS);

            try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = output.readLine();
                if (!line.contains("jeprof")) {
                    Output.error("`jeprof` tool is not properly installed (https://github.com/jemalloc/jemalloc)", true);
                }
            }
        } catch (Exception e) {
            Output.error("Cannot check jeprof existence: " + e.getMessage(), true);
        }
    }
}
