import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

//import static Test.*;


/**
 * Created by Alex on 18.09.2016.
 */
public class PseudoRandomGenerator {
    private final static long m = (long) Math.pow(2, 32);
    private final static long a = (long) Math.pow(2, 16) + 1;
    private final static long c = 119L;

    private static long[] nativeJavaGenerator(long[] statistics) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[statistics.length];
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        secureRandom.nextBytes(bytes);
        for (int i = 0; i < statistics.length; i++) {
            statistics[i] = bytes[i] & 0xFF;
        }
        return statistics;
    }

    private static long[] lehmerLowGenerator(long x, long[] statistics) {
        for (int i = 0; i < statistics.length; i++) {
            x = (a * x + c) % m;
            statistics[i] = x % 256;
        }
        return statistics;
    }

    private static long[] lehmerHighGenerator(long x, long[] statistics) {
        for (int i = 0; i < statistics.length; i++) {
            x = (a * x + c) % m;
            statistics[i] = x >>> 24;
        }
        return statistics;
    }

    private static long[] arrayShift(long[] array, long element) {
        array = Arrays.copyOfRange(array, 1, array.length + 1);
        array[array.length - 1] = element;
        return array;
    }

    private static long[] sequenceGenerator(long[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = new Random().nextInt(2);
        }
        return array;
    }

    private static long[] bitToByteArray(long[] statistics) {
        long[] longs = new long[statistics.length / 8];
        for (int i = 0; i < statistics.length / 8; i++) {
            longs[i] = (128 * statistics[i * 8] + 64 * statistics[i * 8 + 1] + 32 * statistics[i * 8 + 2] + 16 * statistics[i * 8 + 3] + 8 * statistics[i * 8 + 4] + 4 * statistics[i * 8 + 5] + 2 * statistics[i * 8 + 6] + statistics[i * 8 + 7]);
        }
        return longs;
    }

    private static long[] l_20Generator(long[] statistics) {
        long[] elementsOfL20 = new long[20];
        elementsOfL20 = sequenceGenerator(elementsOfL20);

        for (int i = 0; i < statistics.length; i++) {
            long x = elementsOfL20[17] ^ elementsOfL20[15] ^ elementsOfL20[11] ^ elementsOfL20[0];
            statistics[i] = x;
            elementsOfL20 = arrayShift(elementsOfL20, x);
        }
        return bitToByteArray(statistics);
    }

    private static long[] l_89Generator(long[] statistics) {
        long[] elementsOfL89 = new long[89];
        elementsOfL89 = sequenceGenerator(elementsOfL89);

        for (int i = 0; i < statistics.length; i++) {
            long x = elementsOfL89[51] ^ elementsOfL89[0];
            statistics[i] = x;
            elementsOfL89 = arrayShift(elementsOfL89, x);
        }
        return bitToByteArray(statistics);
    }

    private static long[] jeffeGenerator(long[] statistics) {
        long[] l_11 = new long[11];
        l_11 = sequenceGenerator(l_11);
        long[] l_9 = new long[9];
        l_9 = sequenceGenerator(l_9);
        long[] l_10 = new long[10];
        l_10 = sequenceGenerator(l_10);

        for (int i = 0; i < statistics.length; i++) {
            long x = l_11[2] ^ l_11[0];
            long y = l_9[4] ^ l_9[3] ^ l_9[1] ^ l_9[0];
            long s = l_10[3] ^ l_10[0];

            long z = (s * x) ^ ((1 ^ s) * y);
            statistics[i] = z;

            l_11 = arrayShift(l_11, x);
            l_9 = arrayShift(l_9, y);
            l_10 = arrayShift(l_10, s);
        }
        return bitToByteArray(statistics);
    }

    private static long shift(long l, int shift) {
        char[] a = Long.toBinaryString(l).toCharArray();

        char[] b = new char[a.length];

        if (shift > 0) {
            System.arraycopy(a, a.length - shift - 1, b, 0, shift);
            System.arraycopy(a, 0, b, shift, a.length - shift);
        } else {
            shift = Math.abs(shift);
            System.arraycopy(a, shift, b, 0, a.length - shift);
            System.arraycopy(a, 0, b, a.length - shift, shift);
        }
        return Long.parseLong(String.copyValueOf(b), 2);
    }

    private static long[] wolframGenerator(long[] statistics) {
        long r = Math.abs(new SecureRandom().nextInt()) + 1;
        for (int i = 0; i < statistics.length; i++) {
            statistics[i] = r % 2;
            r = (shift(r, -1)) ^ (r | (shift(r, 1)));
        }
        return bitToByteArray(statistics);
    }

    private static long[] librarianGenerator(long[] statistics, StringBuilder text) {
        if (text.length() < statistics.length) {
            System.out.println("ERROR\nTEXT IS TOO SHORT");
            return null;
        } else {
            byte[] bytes = text.toString().getBytes();
            for (int i = 0; i < statistics.length; i++) {
                statistics[i] = bytes[i] & 0xFF;
            }
            return statistics;
        }
    }

    private static long[] bMGenerator(long[] statistics) {
        BigInteger p = new BigInteger("CEA42B987C44FA642D80AD9F51F10457690DEF10C83D0BC1BCEE12FC3B6093E3", 16);
        BigInteger a = new BigInteger("5B88C41246790891C095E2878880342E88C79974303BD0400B090FE38A688356", 16);
        BigInteger q = new BigInteger("675215CC3E227D3216C056CFA8F8822BB486F788641E85E0DE77097E1DB049F1", 16);

        Random rand = new Random();
        BigInteger t = new BigInteger(32, rand);

        for (int i = 0; i < statistics.length; i++) {
            if (t.compareTo(q) == -1) {
                statistics[i] = 1;
            } else {
                statistics[i] = 0;
            }
            t = a.modPow(t, p);
        }
        return bitToByteArray(statistics);
    }

    private static long[] bMByteGenerator(long[] statistics) {
        BigInteger p = new BigInteger("CEA42B987C44FA642D80AD9F51F10457690DEF10C83D0BC1BCEE12FC3B6093E3", 16);
        BigInteger a = new BigInteger("5B88C41246790891C095E2878880342E88C79974303BD0400B090FE38A688356", 16);
        BigInteger s = (p.subtract(BigInteger.valueOf(1))).divide(BigInteger.valueOf(256));

        Random rand = new Random();
        BigInteger t = new BigInteger(32, rand);

        for (int i = 0; i < statistics.length; i++) {
            statistics[i] = t.divide(s).longValue();
            t = a.modPow(t, p);
        }
        return statistics;
    }

    private static long[] bBSGenerator(long[] statistics) {
        BigInteger p = new BigInteger("D5BBB96D30086EC484EBA3D7F9CAEB07", 16);
        BigInteger q = new BigInteger("425D2B9BFDB25B9CF6C416CC6E37B59C1F", 16);
        BigInteger n = p.multiply(q);

        Random rand = new Random();
        BigInteger r = new BigInteger(32, rand);

        for (int i = 0; i < statistics.length; i++) {
            r = r.modPow(BigInteger.valueOf(2), n);
            statistics[i] = r.mod(BigInteger.valueOf(2)).longValue();
        }
        return bitToByteArray(statistics);
    }

    private static long[] bBSByteGenerator(long[] statistics) {
        BigInteger p = new BigInteger("D5BBB96D30086EC484EBA3D7F9CAEB07", 16);
        BigInteger q = new BigInteger("425D2B9BFDB25B9CF6C416CC6E37B59C1F", 16);
        BigInteger n = p.multiply(q);

        Random rand = new Random();
        BigInteger r = new BigInteger(32, rand);

        for (int i = 0; i < statistics.length; i++) {
            r = r.modPow(BigInteger.valueOf(2), n);
            statistics[i] = r.mod(BigInteger.valueOf(256)).longValue();
        }
        return statistics;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        int count = 1000000;
        String fileName = System.getProperty("user.dir") + "//src//ayvengo.txt";
        StringBuilder text = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "windows-1251"))) {
            String s;

            while ((s = in.readLine()) != null) {
                text.append(s);
                text.append("\n");
            }
        }

        long startTime = System.currentTimeMillis();
        System.out.println("--------nativeJavaGenerator");
        long[] nativeJavaGenerator = new long[count];
        nativeJavaGenerator = nativeJavaGenerator(nativeJavaGenerator);
        Test.completeTests(nativeJavaGenerator, 20);
        long endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("nativeJavaGenerator = " + Arrays.toString(nativeJavaGenerator) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------LehmerLow");
        long[] lehmerLow = new long[count];
        lehmerLow = lehmerLowGenerator(Math.abs(new Random(5).nextInt() + 1), lehmerLow);
        Test.completeTests(lehmerLow, 20);
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("LehmerLow = " + Arrays.toString(lehmerLow) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------LehmerHigh");
        long[] lehmerHigh = new long[count];
        lehmerHigh = lehmerHighGenerator(Math.abs(new Random(5).nextInt() + 1), lehmerHigh);
        Test.completeTests(lehmerHigh, 20);
        endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("LehmerHigh = " + Arrays.toString(lehmerHigh) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------L20");
        long[] l_20 = new long[count];
        l_20 = l_20Generator(l_20);
        Test.completeTests(l_20, 20);
        endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("L20 = " + Arrays.toString(l_20) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------L89");
        long[] l_89 = new long[count];
        l_89 = l_89Generator(l_89);
        Test.completeTests(l_89, 20);
        endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("L89 = " + Arrays.toString(l_89) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------Jeffe");
        long[] jeffe = new long[count];
        jeffe = jeffeGenerator(jeffe);
        Test.completeTests(jeffe, 20);
        endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("Jeffe = " + Arrays.toString(jeffe) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------Wolfram");
        long[] wolfram = new long[count];
        wolfram = wolframGenerator(wolfram);
        Test.completeTests(wolfram, 20);
        endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("Wolfram = " + Arrays.toString(wolfram) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------Librarian");
        long[] lib = new long[count];
        lib = librarianGenerator(lib, text);
        Test.completeTests(lib, 20);
        endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("Librarian = " + Arrays.toString(lib) + "\n");

//        startTime = System.currentTimeMillis();
//        System.out.println("--------Blum-Micali");
//        long[] bM = new long[count];
//        bM = bMGenerator(bM);
//        Test.completeTests(bM, 20);
//        endTime = System.currentTimeMillis();
//        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
////        System.out.println("Blum-Micali = " + Arrays.toString(bM) + "\n");
//
//        startTime = System.currentTimeMillis();
//        System.out.println("--------Blum-Micali-Byte");
//        long[] bMByte = new long[count];
//        bMByte = bMByteGenerator(bMByte);
//        Test.completeTests(bMByte, 20);
//        endTime = System.currentTimeMillis();
//        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
////        System.out.println("Blum-Micali-Byte = " + Arrays.toString(bMByte) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------Blum-Blum-Shyb");
        long[] bBS = new long[count];
        bBS = bBSGenerator(bBS);
        Test.completeTests(bBS, 20);
        endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("Blum-Blum-Shyb = " + Arrays.toString(bBS) + "\n");

        startTime = System.currentTimeMillis();
        System.out.println("--------Blum-Blum-Shyb-Byte");
        long[] bBSByte = new long[count];
        bBSByte = bBSByteGenerator(bBSByte);
        Test.completeTests(bBSByte, 20);
        endTime = System.currentTimeMillis();
        System.out.println("time = " + (endTime - startTime) + " milliseconds\n");
//        System.out.println("Blum-Blum-Shyb-Byte = " + Arrays.toString(bBSByte) + "\n");

    }
}
