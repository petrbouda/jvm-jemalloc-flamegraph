package pbouda.jjf.strategy;

import pbouda.jjf.step.Symbol;

import java.util.NavigableMap;

public class NoOpSymbolResolvingStrategy implements SymbolResolvingStrategy {

    @Override
    public Symbol resolve(NavigableMap<Long, Symbol> symbols, long address) {
        return symbols.get(address);
    }
}
