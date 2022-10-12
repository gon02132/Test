import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BinarySearchTest {

    public static void main(String[] args) throws Exception {

        int max = 100000;
        int min = 0;

        Integer[] firstCardList = generateRandomNumbers(min, max);
        Integer[] secondCardList = generateRandomNumbers(min, max / 2);

        long beforeTime = System.currentTimeMillis();
        boolean isMatched = false;
        for (int i = 0; i < secondCardList.length; i++) {
            for (int j = 0; j < firstCardList.length; j++) {
                if (firstCardList[j] == secondCardList[i]) {
                    isMatched = true;
                    break;
                }
            }
        }
        long afterTime = System.currentTimeMillis();
        System.out.println("Normal Loop result:" + (afterTime - beforeTime) + "ms");

        beforeTime = System.currentTimeMillis();
        for (int i = 0; i < secondCardList.length; i++) {
            binarySearch(firstCardList, secondCardList[i]);
        }
        afterTime = System.currentTimeMillis();
        System.out.println("Binary search result:" + (afterTime - beforeTime) + "ms");
    }

    private static Integer[] generateRandomNumbers(int min, int max) {
        List<Integer> tempList = new ArrayList<>();
        for (int i = min; i < max; i++) {
            tempList.add(i);
        }
        Collections.shuffle(tempList);
        return tempList.toArray(new Integer[0]);
    }

    private static int binarySearch(Integer[] firstCardList, int searchNumber) {
        int start = 0;
        int end = firstCardList.length - 1;

        while (start <= end) {
            int mid = (start + end) / 2;
            if (firstCardList[mid] == searchNumber) {
                return 1;
            }

            if (firstCardList[mid] < searchNumber) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }
        return 0;
    }
}
