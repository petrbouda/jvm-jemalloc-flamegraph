package pbouda.jjf;

import pbouda.jjf.checker.Checker;
import pbouda.jjf.checker.EnvVariableChecker;
import pbouda.jjf.checker.JeprofChecker;
import pbouda.jjf.step.*;
import pbouda.jjf.strategy.CollapsedStackFixingStrategy;
import pbouda.jjf.strategy.ShiftingSymbolResolvingStrategy;
import pbouda.jjf.strategy.StackFixingStrategy;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Application {

    private static final Path TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir"));

    private static final List<Checker> CHECKERS = List.of(
            new EnvVariableChecker("FLAMEGRAPH_HOME"),
            new EnvVariableChecker("JAVA_HOME"),
            new JeprofChecker()
    );

    private static final List<Step> STEPS = List.of(
            new ResolvePid(),
            new ParseJvmSymbols(),
            new CollapseStacks(),
            new FixMissingSymbols(),
            new GenerateFlamegraph()
    );

    public static void main(String[] args) {
        Map<Flag, String> flags = Flag.resolve(args);

        for (Checker checker : CHECKERS) {
            checker.check(flags);
        }

        Function<MutableContext, StackFixingStrategy> fixingStrategyFactory = context ->
                new CollapsedStackFixingStrategy(context, new ShiftingSymbolResolvingStrategy());

        boolean addOffsets = flags.containsKey(Flag.OFFSET);

        MutableContext context = new MutableContext(flags, TEMP_DIR, fixingStrategyFactory, addOffsets);
        for (Step step : STEPS) {
            step.execute(context);
        }
    }
}
