import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 03.10.2016.
 */
class Test {

    private static double getXBoundary (long l, double z) {
        return (Math.sqrt(2 * l)) * z + l;
    }

    private static boolean equalProbabilityTest(long[] statistics, double z) {
        double n = (double)statistics.length / 256;
        Map<Long, Integer> map = new HashMap<>();
        for (long element : statistics) {
            if (map.containsKey(element)) {
                map.put(element, map.get(element) + 1);
            } else {
                map.put(element, 1);
            }
        }
        double x = 0;
        for (Long l : map.keySet()) {
            double v = (double) map.get(l);
            x += (v - n)*(v - n) / n;
        }
        return x <= getXBoundary(255, z);
    }

    private static boolean elementIndependenceTest(long[] statistics, double z) {
        Map<Long[], Integer> pairMap = new HashMap<>();
        Map<Long, Integer> firstElementMap = new HashMap<>();
        Map<Long, Integer> secondElementMap = new HashMap<>();
        for (int i = 0; i < statistics.length / 2; i++) {
            Long[] element = {statistics[i * 2], statistics[i * 2 + 1]};
            if (pairMap.containsKey(element)) {
                pairMap.put(element, pairMap.get(element) + 1);
            } else {
                pairMap.put(element, 1);
            }
            if (firstElementMap.containsKey(statistics[i * 2])) {
                firstElementMap.put(statistics[i * 2], firstElementMap.get(statistics[i * 2]) + 1);
            } else {
                firstElementMap.put(statistics[i * 2], 1);
            }
            if (secondElementMap.containsKey(statistics[i * 2 + 1])) {
                secondElementMap.put(statistics[i * 2 + 1], secondElementMap.get(statistics[i * 2 + 1]) + 1);
            } else {
                secondElementMap.put(statistics[i * 2 + 1], 1);
            }
        }
        int n = statistics.length / 2;
        double x = 0;
        for (Long[] pair : pairMap.keySet()) {
            int vIQuantity = firstElementMap.get(pair[0]);
            int vJQuantity = secondElementMap.get(pair[1]);
            int vPairQuantity = pairMap.get(pair);
            x += Math.pow(vPairQuantity, 2) / (vIQuantity * vJQuantity);
        }
        x = n * (x - 1);
        return x <= getXBoundary(255 * 255, z);
    }

    private static boolean sequenceHomogeneityTest(long[] statistics, int r, double z) {
        int mLength = statistics.length / r;
        int n = mLength * r;
        double x = 0;
        HashMap<Long, Integer>[] sequenceMap = new HashMap[r];
        Map<Long, Integer> singleElementMap = new HashMap<>();

        for (long statistic : statistics) {
            if (singleElementMap.containsKey(statistic)) {
                singleElementMap.put(statistic, singleElementMap.get(statistic) + 1);
            } else {
                singleElementMap.put(statistic, 1);
            }

        }
        for (int i = 0; i < r; i++) {
            sequenceMap[i] = new HashMap<>();
            for (int j = 0; j < mLength; j++) {
                if (sequenceMap[i].containsKey(statistics[i * mLength + j])) {
                    sequenceMap[i].put(statistics[i * mLength + j], (sequenceMap[i]).get(statistics[i * mLength + j]) + 1);
                } else {
                    sequenceMap[i].put(statistics[i * mLength + j], 1);
                }
            }
        }

        for (Long singleElement : singleElementMap.keySet()) {
            int singleElementQuantity = singleElementMap.get(singleElement);
            for (int i = 0; i < r; i++) {
                if (sequenceMap[i].keySet().contains(singleElement)) {
                    int elementQuantityInSequence = sequenceMap[i].get(singleElement);
                    x += Math.pow(elementQuantityInSequence, 2) / (singleElementQuantity * mLength);
                }
            }
        }
        x = n * (x - 1);
        return x <= getXBoundary(255 * (r - 1), z);
    }

    private static boolean[] allTests(long[] statistics, int r, double z) {
        return new boolean[] {equalProbabilityTest(statistics, z), elementIndependenceTest(statistics, z),
        sequenceHomogeneityTest(statistics, r, z)};
    }

    static void completeTests(long[] statistics, int r){
        double z001 = 2.3263478740408408;
        boolean status[] = allTests(statistics, r, z001);
        System.out.println("---0.01\nfirst test: " + status[0] + ";  second test: " + status[1] + ";  third test: " + status[2]);
        double z005 = 1.6448536269514722;
        status = allTests(statistics, r, z005);
        System.out.println("---0.05\nfirst test: " + status[0] + ";  second test: " + status[1] + ";  third test: " + status[2]);
        double z01 = 1.2815515655446004;
        status = allTests(statistics, r, z01);
        System.out.println("---0.1\nfirst test: " + status[0] + ";  second test: " + status[1] + ";  third test: " + status[2]);
    }

    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
