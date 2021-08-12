package pbouda.jjf;

import pbouda.jjf.strategy.StackFixingStrategy;
import pbouda.jjf.step.Symbol;

import java.nio.file.Path;
import java.util.Map;
import java.util.NavigableMap;
import java.util.function.Function;

public class MutableContext {

    private final Map<Flag, String> flags;
    private final Path workingPath;
    private final Function<MutableContext, StackFixingStrategy> fixingStrategyFactory;
    private final boolean addOffsets;
    private String pid;
    private NavigableMap<Long, Symbol> jvmSymbols;
    private Path collapsedStacksPath;
    private Path fixedStacksPath;

    public MutableContext(
            Map<Flag, String> flags,
            Path workingPath,
            Function<MutableContext, StackFixingStrategy> fixingStrategyFactory,
            boolean addOffsets) {

        this.fixingStrategyFactory = fixingStrategyFactory;
        this.flags = flags;
        this.workingPath = workingPath;
        this.addOffsets = addOffsets;
    }

    public Map<Flag, String> getFlags() {
        return flags;
    }

    public Path getWorkingPath() {
        return workingPath;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public NavigableMap<Long, Symbol> getJvmSymbols() {
        return jvmSymbols;
    }

    public void setJvmSymbols(NavigableMap<Long, Symbol> jvmSymbols) {
        this.jvmSymbols = jvmSymbols;
    }

    public Path getCollapsedStacksPath() {
        return collapsedStacksPath;
    }

    public void setCollapsedStacksPath(Path collapsedStacksPath) {
        this.collapsedStacksPath = collapsedStacksPath;
    }

    public Path getFixedStacksPath() {
        return fixedStacksPath;
    }

    public void setFixedStacksPath(Path fixedStacksPath) {
        this.fixedStacksPath = fixedStacksPath;
    }

    public Function<MutableContext, StackFixingStrategy> getFixingStrategyFactory() {
        return fixingStrategyFactory;
    }

    public boolean getAddOffsets() {
        return addOffsets;
    }
}
