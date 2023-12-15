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
public class LinearSearch
{
    static RAM memory = new RAM();
    static RAM stick2 = new RAM();


    public static void main(String[] args) // Assuming that RAM_SIZE is a divisor of DRIVE_SIZE
    {
        Scanner s = new Scanner(System.in);

        memory.driveDump();

        int x = s.nextInt(), index;
        s.nextLine();

        index = linearSearch(x);

        if (index != -1) {
            System.out.println("Found at index " + index);
        } else {
            System.out.println("No instance found");
        }

        stick2.printStats();
    }

    /**
     * finds int x in the drive
     *
     * @author TYLER!!!!
     * @param x value wanted from the drive
     * @return index of the value, returns -1 if not found
     */
    public static int linearSearch (int x) {
        memory.load(0);
        int valueExamined;

        for (int y = 0; y != Computer.DRIVE_SIZE; y++) {
            if (y % Computer.RAM_SIZE == 0) memory.load(y);
            valueExamined = memory.get(y % Computer.RAM_SIZE);
            System.out.println("comparing " + x + " with " + y);
            if (valueExamined == x) return y;
            else if (valueExamined > x) return -1;
        }

        return -1;
    }
}
