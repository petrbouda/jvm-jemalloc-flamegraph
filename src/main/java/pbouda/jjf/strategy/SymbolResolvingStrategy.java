package pbouda.jjf.strategy;

import pbouda.jjf.step.Symbol;

import java.util.NavigableMap;

@FunctionalInterface
public interface SymbolResolvingStrategy {

    Symbol resolve(NavigableMap<Long, Symbol> symbols, long address);

}
