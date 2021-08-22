package pbouda.jjf.step;

import pbouda.jjf.MutableContext;
import pbouda.jjf.Output;
import pbouda.jjf.strategy.StackFixingStrategy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class FixMissingSymbols implements Step {

    @Override
    public void execute(MutableContext context) {
        Path workingPath = context.getWorkingPath();
        Path targetPath = workingPath.resolve("replaced-" + context.getPid() + ".txt");
        StackFixingStrategy fixingStrategy = context.getFixingStrategyFactory().apply(context);

        try {
            replaceJavaSymbols(targetPath, context.getCollapsedStacksPath(), fixingStrategy);
        } catch (Exception e) {
            Output.error("Cannot fix missing symbols: collapsed_stacks=" + context.getCollapsedStacksPath()
                         + " fixed_symbols=" + targetPath
                         + " error=" + e.getMessage(), true);
        }

        context.setFixedStacksPath(targetPath);
        Output.info("Symbols successfully replaced: " + targetPath);
    }

    private static void replaceJavaSymbols(Path targetPath, Path collapsedPath, StackFixingStrategy fixingStrategy) throws Exception {
        try (BufferedReader reader = Files.newBufferedReader(collapsedPath);
             BufferedWriter writer = Files.newBufferedWriter(targetPath, CREATE, TRUNCATE_EXISTING)) {

            for (String line; (line = reader.readLine()) != null; ) {
                int i = line.lastIndexOf(" ");

                String stack = line.substring(0, i);
                String occurrences = line.substring(i + 1);

                String replacedStack = fixingStrategy.fix(stack);
                writer.write(replacedStack + " " + occurrences);
                writer.newLine();
            }
        }
    }
}
