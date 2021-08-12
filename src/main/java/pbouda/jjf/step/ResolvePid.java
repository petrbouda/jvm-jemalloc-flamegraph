package pbouda.jjf.step;

import pbouda.jjf.MutableContext;
import pbouda.jjf.Flag;
import pbouda.jjf.Output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class ResolvePid implements Step {

    @Override
    public void execute(MutableContext context) {
        String pid;
        Map<Flag, String> flags = context.getFlags();
        if (flags.containsKey(Flag.LATEST)) {
            Optional<String> optPid = resolveLatestPid(context.getWorkingPath());
            if (optPid.isEmpty()) {
                Output.error("Cannot resolve the LATEST pid (using the latest PERF_FILE in the temp folder)", true);
                return;
            }
            pid = optPid.get();
        } else {
            pid = flags.get(Flag.PID);
        }

        if (pid == null || pid.isBlank()) {
            Output.error("Cannot resolve a pid (use --pid or --latest to resolve it using the latest PERF_FILE in the temp folder)",  true);
        } else {
            context.setPid(pid);
            Output.info("Resolved PID: " + pid);
        }
    }

    private static Optional<String> resolveLatestPid(Path workingPath) {
        try {
            return Files.find(workingPath, 1, (path, attributes) ->
                            path.toString().startsWith(workingPath.resolve("perf-").toString()))
                    .max(Comparator.comparing(path -> path.toFile().lastModified()))
                    .map(path -> {
                        String dash = path.toString().split("-")[1];
                        return dash.split("\\.")[0];
                    });
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
