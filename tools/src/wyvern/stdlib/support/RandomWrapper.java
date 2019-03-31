package wyvern.stdlib.support;
import java.util.Random;

public class RandomWrapper {

    public static final Random rand = new Random();;

    RandomWrapper() {
    }

    public int nextInt() {
        return rand.nextInt();
    }
    
    public int nextInt(int bound) {
        return rand.nextInt(bound);
    }

}