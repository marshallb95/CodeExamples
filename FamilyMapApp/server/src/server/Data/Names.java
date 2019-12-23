package server.Data;

import java.util.Arrays;
import java.util.Random;

/**
 * Container class for generating random first and last names for people in services, read in from json
 */
public class Names {
    /**
     * Array of String of names
     */
    String[] data;

    /**
     * Create names object
     * @param data
     */
    public Names(String[] data) {
        this.data = data;
    }

    /**
     * Turn array into a string
     * @return
     */
    public String toString() {
        return Arrays.toString(data);
    }

    /**
     * Gets a random name from the array and return the name
     * @return
     */
    public String getRandomName() {
        Random random = new Random();
        int index = random.nextInt(data.length-1);
        return data[index];
    }
    public String[] getData() {
        return data;
    }
}
