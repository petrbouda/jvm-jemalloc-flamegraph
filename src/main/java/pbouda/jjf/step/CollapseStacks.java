package pbouda.jjf.step;

import pbouda.jjf.MutableContext;
import pbouda.jjf.Output;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class CollapseStacks implements Step {

    private static final String HEAP_FILE_PATTERN = "jeprof.%s.*.f.heap";
    private static final String JEPROF_COMMAND = "jeprof --collapsed $JAVA_HOME/bin/java %s > %s";

    @Override
    public void execute(MutableContext context) {
        try {
            String pid = context.getPid();
            Path targetPath = context.getWorkingPath()
                    .resolve("collapsed-" + pid + ".txt");
            Path profiles = context.getWorkingPath()
                    .resolve(HEAP_FILE_PATTERN.formatted(pid));

            Process process = new ProcessBuilder()
                    .command("bash", "-c", JEPROF_COMMAND.formatted(profiles, targetPath))
                    .inheritIO()
                    .start();

            process.waitFor(1, TimeUnit.MINUTES);

            int result = process.exitValue();

            if (result != 0) {
                Output.error("Cannot generate Collapsed stacks: " + targetPath, true);
            } else {
                context.setCollapsedStacksPath(targetPath);
                Output.info("Collapsed stacks created: " + targetPath);
            }
        } catch (Exception e) {
            Output.error("Cannot create a file with collapsed stacks: " + e.getMessage());
        }
    }
}
