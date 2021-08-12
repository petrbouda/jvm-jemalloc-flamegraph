package pbouda.jjf.strategy;

import pbouda.jjf.MutableContext;
import pbouda.jjf.step.Symbol;

import java.util.Arrays;
import java.util.NavigableMap;
import java.util.stream.Collectors;

public class CollapsedStackFixingStrategy implements StackFixingStrategy {

    private static final String FRAME_DELIMITER = ";";

    private final SymbolResolvingStrategy symbolResolvingStrategy;
    private final NavigableMap<Long, Symbol> symbols;
    private final boolean addOffset;

    public CollapsedStackFixingStrategy(MutableContext context, SymbolResolvingStrategy symbolResolvingStrategy) {
        this.symbolResolvingStrategy = symbolResolvingStrategy;
        this.symbols = context.getJvmSymbols();
        this.addOffset = context.getAddOffsets();
    }

    @Override
    public String fix(String line) {
        return Arrays.stream(line.split(FRAME_DELIMITER))
                .map(symbolOrAddress -> {
                    if (symbolOrAddress.startsWith("0x")) {
                        String hexAddress = symbolOrAddress;
                        long address = Long.parseLong(hexAddress.replaceFirst("0x", ""), 16);
                        Symbol resolvedSymbol = symbolResolvingStrategy.resolve(symbols, address);
                        return resolvedSymbol != null
                                ? formatSymbol(resolvedSymbol, hexAddress, addOffset)
                                : "<no-symbol>";
                    } else {
                        return symbolOrAddress;
                    }
                })
                .collect(Collectors.joining(FRAME_DELIMITER));
    }

    private static String formatSymbol(Symbol symbol, String hexAddress, boolean addOffset) {
        String offset = addOffset ? "<" + hexAddress + ">" : "";
        return symbol.symbol()
                .replaceFirst("_\\[j]", offset + "_[j]")
                .replaceFirst("_\\[k]", offset + "_[k]");
    }
}
