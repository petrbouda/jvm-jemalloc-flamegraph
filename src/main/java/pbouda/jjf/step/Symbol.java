package pbouda.jjf.step;

public record Symbol(long address, long offset, String symbol, boolean inlined) {
}