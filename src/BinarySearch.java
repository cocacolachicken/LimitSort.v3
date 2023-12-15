import java.util.Scanner;

/**
 * This is the drive class where you will implement a storing algorithm where you have a VERY large data set and
 * limit memory to access all the data.  The Computer interface contains the constants that you can adjust to allow
 * you to adjust the size of the memory and the hard drive for testing purposes. However, your final work should
 * be able to sort 1 billion (1 000 000 000) integers when you only have two sticks of RAM that can only 1000
 * integers.
 *
 * You will be assessed on correctness and effeciency.
 *
 * YOU ARE NOT ALLOWED TO MODIFY THE RAM CLASS!!!!
 *
 * @author Tyler
 * @version 14/10/22
 */
public class BinarySearch
{
    static RAM memory = new RAM();
    static RAM stick2 = new RAM();


    public static void main(String[] args) // Assuming that RAM_SIZE is a divisor of DRIVE_SIZE
    {
        Scanner s = new Scanner(System.in);

        memory.driveDump();

        int x = s.nextInt(), index;
        s.nextLine();

        index = binarySearch(x);

        if (index != -1) {
            System.out.println("Found at index " + index);
        } else {
            System.out.println("No instance found");
        }

        stick2.printStats();
    }

    /**
     * Returns index matching element x, if not in array then returns -1
     * Uses binary search: takes a middle element, if higher takes the upper half, if lower takes the lower half, if equal
     * then returns middle. Stops until left < right, and if the element isn't found by then it returns -1
     *
     * @param x value wanted
     * @return index if found
     */
    public static int binarySearch (int x) {
        int left = 0, right = Computer.DRIVE_SIZE-1, middle = 0;
        while (left < right) {
            middle = (left+right)/2;
            System.out.println("left: " + left + "right: " + right + "middle: " + middle);
            if (x < fetch(middle)) {
                right = middle - 1;
            } else if (x > fetch(middle)) {
                left = middle + 1;
            } else {
                return middle;
            }
        }
        middle = (left+right)/2; // Calculate again because it doesn't calculate the last middle thing
        if (fetch(middle) == x)
        return middle; else return -1;
    }

    /**
     * returns index number index from the hard drive
     *
     * @author tylerr
     * @param index index of number wanted
     * @return drive[index]
     */
    public static int fetch (int index) {
        if (index + Computer.RAM_SIZE < Computer.DRIVE_SIZE) {
            stick2.load(index);
            return stick2.get(0);
        } else {
            stick2.load(Computer.DRIVE_SIZE-Computer.RAM_SIZE);
            return stick2.get((index % Computer.RAM_SIZE));
        }
    }
}
