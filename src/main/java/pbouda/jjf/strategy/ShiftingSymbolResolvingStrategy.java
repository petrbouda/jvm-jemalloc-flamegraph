package pbouda.jjf.strategy;

import pbouda.jjf.step.Symbol;

import java.util.Map;
import java.util.NavigableMap;

public class ShiftingSymbolResolvingStrategy implements SymbolResolvingStrategy {

    private static final int SYMBOL_SHIFTING_TOLERANCE = 5;

    private final int tolerance;

    public ShiftingSymbolResolvingStrategy() {
        this(SYMBOL_SHIFTING_TOLERANCE);
    }

    public ShiftingSymbolResolvingStrategy(int tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public Symbol resolve(NavigableMap<Long, Symbol> symbols, long address) {
        Map.Entry<Long, Symbol> lower = symbols.lowerEntry(address);
        Map.Entry<Long, Symbol> higher = symbols.higherEntry(address);

        if (lower != null && higher != null) {
            Symbol lowerSymbol = lower.getValue();
            Symbol higherSymbol = higher.getValue();

            boolean isShifted = fixedShiftedAddress(address, lowerSymbol, higherSymbol, tolerance);
            return address < (lowerSymbol.address() + lowerSymbol.offset()) && !isShifted
                    ? lowerSymbol
                    : higherSymbol;
        } else {
            return higher == null ? lower.getValue() : higher.getValue();
        }
    }

    /**
     * In some cases jemalloc reports shifted address about a couple of bits:
     * <p>
     * PERF output:
     * <pre>
     * java  3251   156.826606:    3385163 cycles:
     * 	    7f75a0f1a8f0 Unsafe_AllocateMemory0+0x40 (/home/pbouda/.sdkman/candidates/java/16.0.1-zulu/lib/server/libjvm.so)
     * 	    7f758df7c6db Ljdk/internal/misc/Unsafe;::allocateMemory0+0xbb (/tmp/perf-3250.map)
     * 	    7f758df97a58 Lpbouda/GenerateUnsafeStrictLeak;::main(GenerateUnsafeLeak.java:24)
     * 	    ->Lpbouda/GenerateUnsafeStrictLeak;::allocate1(GenerateUnsafeLeak.java:46)
     * 	    ->Lpbouda/GenerateUnsafeStrictLeak;::allocate2(GenerateUnsafeLeak.java:50)
     * 	    ->Lpbouda/GenerateUnsafeStrictLeak;::allocate3(GenerateUnsafeLeak.java:54)
     * 	    ->Lpbouda/GenerateUnsafeStrictLeak;::allocate4(GenerateUnsafeLeak.java:58)
     * 	    ->Lpbouda/GenerateUnsafeStrictLeak;::allocate5(GenerateUnsafeLeak.java:62)
     * 	    ->Lsun/misc/Unsafe;::allocateMemory(Unsafe.java:462)
     * 	    ->Ljdk/internal/misc/Unsafe;::allocateMemory(Unsafe.java:622)+0x0 (/tmp/perf-3250.map)
     * 	    7f758641ccc9 call_stub+0x8a (/tmp/perf-3250.map)
     * </pre>
     * <p>
     * PERF MAP file:
     * <pre>
     *      7f758df979f7 61 Lpbouda/GenerateUnsafeStrictLeak;::main(GenerateUnsafeLeak.java:24)
     *      7f758df97a58 26 Lpbouda/GenerateUnsafeStrictLeak;::main(GenerateUnsafeLeak.java:24)
     *      ->Lpbouda/GenerateUnsafeStrictLeak;::allocate1(GenerateUnsafeLeak.java:46)
     *      ->Lpbouda/GenerateUnsafeStrictLeak;::allocate2(GenerateUnsafeLeak.java:50)
     *      ->Lpbouda/GenerateUnsafeStrictLeak;::allocate3(GenerateUnsafeLeak.java:54)
     *      ->Lpbouda/GenerateUnsafeStrictLeak;::allocate4(GenerateUnsafeLeak.java:58)
     *      ->Lpbouda/GenerateUnsafeStrictLeak;::allocate5(GenerateUnsafeLeak.java:62)
     *      ->Lsun/misc/Unsafe;::allocateMemory(Unsafe.java:462)
     *      ->Ljdk/internal/misc/Unsafe;::allocateMemory(Unsafe.java:622)
     *      7f758df97a7e 2a Lpbouda/GenerateUnsafeStrictLeak;::main(GenerateUnsafeLeak.java:24)
     * </pre>
     * <p>
     * JProf output:
     * <pre>
     *      -- JVM frames --
     *      JavaCalls::call_helper(JavaValue*, methodHandle const&, JavaCallArguments*, Thread*)<0000000000865a20>;
     *      0x00007f758641ccc7;0x00007f758df97a56;0x00007f758df7c6d9;
     *      Unsafe_AllocateMemory0<0000000000f1e8b0>;
     *      -- jemalloc frames --
     * </pre>
     * <p>
     * Try to find and match the inlined frames the {@code lower} ... at least make visible that there are some inlined methods
     * in the worst case they will be repeated with not-inlined higher in the sample.
     */
    private static boolean fixedShiftedAddress(long addr, Symbol lower, Symbol higher, int tolerance) {
        long absLower = Math.abs(addr - lower.address());
        long absHigher = Math.abs(addr - higher.address());

        // lower -> 62 bits -> addr -> 2 bits -> higher
        // lower: Lpbouda/GenerateUnsafeStrictLeak;::main(GenerateUnsafeLeak.java:24)
        // higher: Lpbouda/GenerateUnsafeStrictLeak;::main(GenerateUnsafeLeak.java:24)
        //         ->Lpbouda/GenerateUnsafeStrictLeak;::allocate1(GenerateUnsafeLeak.java:46)
        return absHigher < absLower
               && absHigher <= tolerance
               && higher.symbol().startsWith(lower.symbol());
    }
}
