package pbouda.jjf.step;

import pbouda.jjf.Flag;
import pbouda.jjf.MutableContext;
import pbouda.jjf.Output;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class GenerateFlamegraph implements Step {

    private static final String FLAMEGRAPH_COMMAND = """
            /home/pbouda/experiments/FlameGraph/flamegraph.pl \\
            --title jemalloc \\
            --subtitle "memory leaks" \\
            --countname bytes \\
            --nametype Method \\
            --colors java \\
            %s > %s""";

    @Override
    public void execute(MutableContext context) {
        Path stacksPath = context.getFixedStacksPath();
        String outputFlag = context.getFlags().get(Flag.OUTPUT);
        Path flamegraphPath = outputFlag != null
                ? Path.of(outputFlag)
                : Path.of("flame-" + context.getPid() + ".svg");

        int result = 0;
        try {
            Process process = new ProcessBuilder()
                    .command("bash", "-c", FLAMEGRAPH_COMMAND.formatted(stacksPath, flamegraphPath))
                    .inheritIO()
                    .start();

            process.waitFor(10, TimeUnit.SECONDS);

            result = process.exitValue();
        } catch (Exception e) {
            Output.error("Cannot generate a Flamegraph: " + e.getMessage(), true);
        }

        if (result != 0) {
            Output.error("Cannot generate a Flamegraph: stacks="
                         + stacksPath + " flamegraph=" + flamegraphPath, true);
        } else {
            Output.success("Flamegraph created: " + flamegraphPath);
        }
    }
}
