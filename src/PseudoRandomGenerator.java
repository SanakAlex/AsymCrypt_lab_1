import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Alex on 18.09.2016.
 */
public class PseudoRandomGenerator {
    private final static long m = (long)Math.pow(2, 32);
    private final static long a = (long)Math.pow(2, 16) + 1;
    private final static long c = 119L;
//    private final static long randomTime = 100;

    private static long[] nativeJavaGenerator(long[] statistics){
        for (int i = 0; i < statistics.length; i++){
            statistics[i] = Math.abs(new SecureRandom().nextInt());
        }
        return statistics;
    }

    private static long[] lehmerLowGenerator(long x, long[] statistics){
        for (int i = 0; i < statistics.length; i++){
            x = (a*x + c) % m;
            statistics[i] = x % 256;
        }
        return statistics;
    }

    private static long[] lehmerHighGenerator(long x, long[] statistics){
        for (int i = 0; i < statistics.length; i++){
            x = (a*x + c) % m;
            statistics[i] = x >>> 24;
        }
        return statistics;
    }

    private static long[] arrayShift(long[] array, long element) {
        array = Arrays.copyOfRange(array, 1, array.length + 1);
        array[array.length - 1] = element;
        return array;
    }

    private static long[] sequenceGenerator(long[] array){
        for (int i = 0; i < array.length; i++) {
            array[i] = new Random().nextInt(2);
        }
        return array;
    }

    private static long[] l_20Generator(long[] statistics) {
        long[] elementsOfL20 = new long[20];
        elementsOfL20 = sequenceGenerator(elementsOfL20);

        for (int i = 0; i < statistics.length; i++) {
            long x = elementsOfL20[17] ^ elementsOfL20[15] ^ elementsOfL20[11] ^ elementsOfL20[0];
            statistics[i] = x;
            elementsOfL20 = arrayShift(elementsOfL20, x);
        }
        return statistics;
    }

    private static long[] l_89Generator(long[] statistics) {
        long[] elementsOfL89 = new long[89];
        elementsOfL89 = sequenceGenerator(elementsOfL89);

        for (int i = 0; i < statistics.length; i++) {
            long x = elementsOfL89[51] ^ elementsOfL89[0];
            statistics[i] = x;
            elementsOfL89 = arrayShift(elementsOfL89, x);
        }
        return statistics;
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
        return statistics;
    }

    private static long shift(long l, int shift) {
        char[] a = Long.toBinaryString(l).toCharArray();

        char[] b = new char[a.length];

        if (shift > 0) {
            System.arraycopy(a, a.length - shift, b, 0, shift);
            System.arraycopy(a, 0, b, shift, a.length - shift);
        } else {
            shift = Math.abs(shift);
            System.arraycopy(a, shift, b, 0, a.length - shift);
            System.arraycopy(a, 0, b, a.length - shift, shift);
        }
        return Long.parseLong(String.copyValueOf(b), 2);
    }

    private static long[] wolframGenerator(long[] statistics) {
        long r = Math.abs(new Random().nextInt((int)Math.pow(2, 32)));
        for (int i = 0; i < statistics.length; i++) {
            statistics[i] = Math.abs(r % 2);
            r = (shift(r, -1)) ^ (r | (shift(r, 1)));
        }
        return  statistics;
    }

    private static long[] librarianGenerator(long[] statistics, StringBuilder text) {
        if (text.length() < statistics.length) {
            System.out.println("ERROR\n TEXT IS TOO SHORT");
            return null;
        } else {
            for (int i = 0; i < statistics.length; i++) {
                statistics[i] = text.charAt(i) % 256;
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
        return statistics;
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
        return statistics;
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

    public static void main(String[] args) throws IOException {
        int count = 1000000;
        String fileName = System.getProperty("user.dir")+"//src//ayvengo.txt";
        StringBuilder text = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "windows-1251"));
        try {
            String s;

            while ((s = in.readLine()) != null) {
                text.append(s);
                text.append("\n");
            }
        } finally {
            in.close();
        }

        long startTime = System.currentTimeMillis();
        long[] nativeJavaGenerator = new long[count];
        nativeJavaGenerator = nativeJavaGenerator(nativeJavaGenerator);
        long endTime = System.currentTimeMillis();
        System.out.println("nativeJavaGenerator = " + (endTime - startTime) + " milliseconds");
//        System.out.println("nativeJavaGenerator = " + Arrays.toString(nativeJavaGenerator) + "\n");

        startTime = System.currentTimeMillis();
        long[] lehmerLow = new long[count];
        lehmerLow = lehmerLowGenerator(Math.abs(new Random(5).nextInt() + 1), lehmerLow);
        System.out.println("LehmerLow = " + (endTime - startTime) + " milliseconds");
//        System.out.println("LehmerLow = " + Arrays.toString(lehmerLow) + "\n");

        startTime = System.currentTimeMillis();
        long[] lehmerHigh = new long[count];
        lehmerHigh = lehmerHighGenerator(Math.abs(new Random(5).nextInt() + 1), lehmerHigh);
        endTime = System.currentTimeMillis();
        System.out.println("LehmerHigh = " + (endTime - startTime) + " milliseconds");
//        System.out.println("LehmerHigh = " + Arrays.toString(lehmerHigh) + "\n");

        startTime = System.currentTimeMillis();
        long[] l_20 = new long[count];
        l_20 = l_20Generator(l_20);
        endTime = System.currentTimeMillis();
        System.out.println("L20 = " + (endTime - startTime) + " milliseconds");
//        System.out.println("L20 = " + Arrays.toString(l_20) + "\n");

        startTime = System.currentTimeMillis();
        long[] l_89 = new long[count];
        l_89 = l_89Generator(l_89);
        endTime = System.currentTimeMillis();
        System.out.println("L89 = " + (endTime - startTime) + " milliseconds");
//        System.out.println("L89 = " + Arrays.toString(l_89) + "\n");

        startTime = System.currentTimeMillis();
        long[] jeffe = new long[count];
        jeffe = jeffeGenerator(jeffe);
        endTime = System.currentTimeMillis();
        System.out.println("Jeffe = " + (endTime - startTime) + " milliseconds");
//        System.out.println("Jeffe = " + Arrays.toString(jeffe) + "\n");

        startTime = System.currentTimeMillis();
        long[] wolfram = new long[count];
        wolfram = wolframGenerator(wolfram);
        endTime = System.currentTimeMillis();
        System.out.println("Wolfram = " + (endTime - startTime) + " milliseconds");
//        System.out.println("Wolfram = " + Arrays.toString(wolfram) + "\n");

        startTime = System.currentTimeMillis();
        long[] lib = new long[count];
        lib = librarianGenerator(lib, text);
        endTime = System.currentTimeMillis();
        System.out.println("Librarian = " + (endTime - startTime) + " milliseconds");
//        System.out.println("Librarian = " + Arrays.toString(lib) + "\n");

        startTime = System.currentTimeMillis();
        long[] bM = new long[count];
        bM = bMGenerator(bM);
        endTime = System.currentTimeMillis();
        System.out.println("Blum-Micali = " + (endTime - startTime) + " milliseconds");
//        System.out.println("Blum-Micali = " + Arrays.toString(bM) + "\n");

        startTime = System.currentTimeMillis();
        long[] bMByte = new long[count];
        bMByte = bMByteGenerator(bMByte);
        endTime = System.currentTimeMillis();
        System.out.println("Blum-Micali-Byte = " + (endTime - startTime) + " milliseconds");
//        System.out.println("Blum-Micali-Byte = " + Arrays.toString(bMByte) + "\n");

        startTime = System.currentTimeMillis();
        long[] bBS = new long[count];
        bBS = bBSGenerator(bBS);
        endTime = System.currentTimeMillis();
        System.out.println("Blum-Blum-Micali = " + (endTime - startTime) + " milliseconds");
//        System.out.println("Blum-Blum-Micali = " + Arrays.toString(bBS) + "\n");

        startTime = System.currentTimeMillis();
        long[] bBSByte = new long[count];
        bBSByte = bBSByteGenerator(bBSByte);
        endTime = System.currentTimeMillis();
        System.out.println("Blum-Blum-Micali-Byte = " + (endTime - startTime) + " milliseconds");
//        System.out.println("Blum-Blum-Micali-Byte = " + Arrays.toString(bBSByte) + "\n");
        /*System.out.println(Long.toBinaryString(20));
        System.out.println(Long.toBinaryString(Long.rotateLeft(20, 1)));

        long a = 200;
        System.out.println(Long.toString(a, 2));*/
        /*System.out.println(toBinaryString(32));
        System.out.println(toBinaryString(32));
        Long l = 256L;
        System.out.println(toBinaryString(l)+"  "+l+"    "+toBinaryString(l).length());
        l = (l % 256);
        System.out.println(toBinaryString(l)+"  "+l);

        System.out.println();

        System.out.println();*/

//        long[] array = new long[] {5, 6, 7, 8, 10, 5050};
//        array = arrayShift(array, 10);
//        System.out.println(Arrays.toString(array));
    }
}
