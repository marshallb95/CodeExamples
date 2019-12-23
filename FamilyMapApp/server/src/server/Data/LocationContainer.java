package server.Data;

import java.util.Arrays;
import java.util.Random;

/**
 * Container class for location object, used to read location objects out of json
 */
public class LocationContainer {
    /**
     * Array of location objects
     */
    Location[] data;

    /**
     * Create location container object
     * @param data
     */
    public LocationContainer(Location[] data) {
        this.data = data;
    }

    /**
     * Turns location array into string
     * @return
     */
    public String toString() {
        return Arrays.toString(data);
    }

    /**
     * Gets random location from array and returns location object
     * @return Random Location object from array
     */
    public Location getRandomLocation() {
        Random random = new Random();
        int index = random.nextInt(data.length -1);
        return data[index];
    }
}
