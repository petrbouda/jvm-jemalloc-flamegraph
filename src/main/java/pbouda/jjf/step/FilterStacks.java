package pbouda.jjf.step;

import pbouda.jjf.Flag;
import pbouda.jjf.MutableContext;
import pbouda.jjf.Output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.CREATE;

public class FilterStacks implements Step {

    @Override
    public void execute(MutableContext context) {
        Map<Flag, String> flags = context.getFlags();

        if (flags.containsKey(Flag.ALL_STACKS)) {
            Output.info("Stack filtering skipped, all stacks are involved");
            return;
        }

        Pattern matcher;
        if (flags.containsKey(Flag.LATEST)) {
            String pattern = flags.get(Flag.FILTER);
            matcher = Pattern.compile(pattern);
        } else {
            // contains any Java Frame
            matcher = Pattern.compile(".*_[j]");
        }

        execute0(context.getFixedStacksPath(), matcher);
    }

    private static void execute0(Path fixedStacksPath, Pattern matcher) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("filter-stacks", ".txt");
            filterStacks(fixedStacksPath, tempFile, matcher);
            Files.copy(tempFile, fixedStacksPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            Output.error("Cannot fix missing symbols: fixed_symbols=" + fixedStacksPath
                         + " error=" + e.getMessage(), true);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    Output.error("Cannot remove a temp file for filtering java stacks: temp_file=" + tempFile
                                 + " error=" + e.getMessage(), true);
                }
            }
        }

        Output.info("Filter out non-java stacks: " + fixedStacksPath);
    }

    private static void filterStacks(Path stackPath, Path tempPath, Pattern matcher) throws Exception {
        try (BufferedReader reader = Files.newBufferedReader(stackPath);
             BufferedWriter writer = Files.newBufferedWriter(tempPath, CREATE)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (containsJavaFrame(line, matcher)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    private static boolean containsJavaFrame(String line, Pattern matcher) {
        for (String frame : line.split(";")) {
            if (matcher.matcher(frame).matches()) {
                return true;
            }
        }
        return false;
    }
}
