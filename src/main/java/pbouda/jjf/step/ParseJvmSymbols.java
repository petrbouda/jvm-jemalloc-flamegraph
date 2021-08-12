package pbouda.jjf.step;

import pbouda.jjf.MutableContext;
import pbouda.jjf.Output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ParseJvmSymbols implements Step {

    private static final String PERF_MAP_FILE_PATH = "/tmp/perf-%s.map";

    @Override
    public void execute(MutableContext context) {
        String pid = context.getPid();
        Path symbolsPath = Path.of(PERF_MAP_FILE_PATH.formatted(pid));
        if (Files.exists(symbolsPath)) {
            Output.info("PERF_MAP Symbols exist:" + symbolsPath);
        } else {
            Output.error("PERF_MAP Symbols does not exist: " + symbolsPath, true);
        }

        try {
            NavigableMap<Long, Symbol> symbols = parseJavaSymbols(symbolsPath);
            context.setJvmSymbols(symbols);
            Output.info("All symbols parsed into an internal Map: " + symbolsPath);
        } catch (IOException ex) {
            Output.error("Cannot parse JVM Symbols: symbol_file=" + symbolsPath + " error=" + ex.getMessage(), true);
        }
    }

    private static NavigableMap<Long, Symbol> parseJavaSymbols(Path symbolsPath) throws IOException {
        NavigableMap<Long, Symbol> symbols = new TreeMap<>();
        Files.lines(symbolsPath)
                // replaces delimiters to avoid conflicts in a collapsed format
                .map(line -> line.replace(";::", ":::"))
                // creates a frame object with address and provided symbol
                .map(line -> {
                    String[] parts = line.split(" ");
                    long address = Long.parseLong(parts[0], 16);
                    long offset = Long.parseLong(parts[1], 16);
                    return new Symbol(address, offset, parts[2], false);
                })
                // detects inlined frames and falls them apart to other virtual frames
                .map(frame -> {
                    if (frame.symbol().contains("->")) {
                        String newSymbols =
                                frame.symbol()
                                        // replaces entire chain of inlined method
                                        .replace("->", "_[i];")
                                        // puts back the first one as non-inlined and adds inline tag to the last one
                                        .replaceFirst("_\\[i];", "_[j];") + "_[i]";
                        return new Symbol(frame.address(), frame.offset(), newSymbols, true);
                    } else {
                        return frame.symbol().startsWith("L")
                                ? new Symbol(frame.address(), frame.offset(), frame.symbol() + "_[j]", false)
                                : new Symbol(frame.address(), frame.offset(), frame.symbol() + "_[k]", false);
                    }
                })
                .forEach(frame -> symbols.put(frame.address(), frame));

        return symbols;
    }
}
