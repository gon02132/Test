package Logic.StreamPerfTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class streamPerfTest {

    public static void main(String[] args) {
        for (int j = 0; j < 10; j++) {
            int cnt = 30000000;
            List<Integer> arr = new ArrayList<>();
            for (int i = 0; i < cnt; i++) {
                int rand = (int) (Math.random() * 15000000 + 1);
                arr.add(rand);
            }

            long beforeTime = System.currentTimeMillis();

            List<Integer> resultOfFor = new ArrayList<>();
            for (int num : arr) {
                if (num > 50) {
                    resultOfFor.add(num);
                }
            }
            long afterTime = System.currentTimeMillis();
            System.out.println("For Result:" + (afterTime - beforeTime));

            beforeTime = System.currentTimeMillis();
            arr.stream().filter(num -> num > 50).collect(Collectors.toList());
            afterTime = System.currentTimeMillis();
            System.out.println("Stream Result:" + (afterTime - beforeTime));
        }
    }
}