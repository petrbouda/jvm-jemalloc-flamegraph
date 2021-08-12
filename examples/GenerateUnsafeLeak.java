import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class GenerateUnsafeLeak {

    private static final Unsafe UNSAFE;

    private static final int FIVE_KB = 5 * 1024;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Cannot get UNSAFE instance", e);
        }
    }

    public static void main(String[] args) throws Exception {
        System.in.read();

        for (int j = 0; j < 1_048_576; j++) {
            long address = allocate1(FIVE_KB);

            long currentAddress = address;
            while (currentAddress < (address + FIVE_KB)) {
                currentAddress = touchAndShiftMemory(currentAddress);
            }

            System.out.println("ALLOCATED AND TOUCHED: " + j++);
        }

        System.in.read();
    }

    private static long touchAndShiftMemory(long address) {
        UNSAFE.putByte(address, (byte) 0);
        return address + UNSAFE.pageSize();
    }

    private static long allocate1(long size) {
        return allocate2(size);
    }

    private static long allocate2(long size) {
        return allocate3(size);
    }

    private static long allocate3(long size) {
        return allocate4(size);
    }

    private static long allocate4(long size) {
        return allocate5(size);
    }

    private static long allocate5(long size) {
        return UNSAFE.allocateMemory(size);
    }
}
