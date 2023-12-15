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
public class InterpolationSearch
{
    static RAM memory = new RAM();
    static RAM stick2 = new RAM();


    public static void main(String[] args) // Assuming that RAM_SIZE is a divisor of DRIVE_SIZE
    {
        Scanner s = new Scanner(System.in);

        memory.driveDump();

        int x = s.nextInt(), index;
        s.nextLine();

        index = interpolationSearch(x);

        if (index != -1) {
            System.out.println("Found at index " + index);
        } else {
            System.out.println("No instance found");
        }

        stick2.printStats();
    }

    /**
     * Returns index matching element x, if not in array then returns -1
     * Uses something like binary search: takes a middle element, if higher takes the upper half, if lower takes the lower half, if equal
     * then returns middle. Stops until left < right, and if the element isn't found by then it returns -1.
     * However, unlike binary search, interpolation search uses a more precise ""pivot"" (in the function named middle) value
     * (i.e. it uses the ratio of x - lower to the max of the values of the region selected to find the elements)
     * While I'm not sure of the actual run time complexity, I'm guessing that best case is O(1) when the element is found on the first try
     * and worst case is O(n) when the elements increase in a way that isn't linear
     *
     * According to searches online, it is usually O(loglog(n)), which I don't know how they got that number.
     * I know that it's going to run faster than binary search because of the fact that it uses more than just the value
     * of right and left / 2, and it actually takes into account how big the middle is in relation to the range.
     *
     * @param x value wanted
     * @return index if found
     */
    public static int interpolationSearch (int x) {
        int left = 0, right = Computer.DRIVE_SIZE-1, middle = 0;

        while (x != middle) {
            if (left > right) {
                return -1;
            }

            middle = left + (int) ((((double) x - fetch(left)) / ((double) fetch(right) - fetch(left))) * ((double) right - left));
            if (x > fetch(middle)) {
                left = middle + 1;
            } else if (x < fetch(middle)) {
                right = middle - 1;
            } else return middle;
        }
        return middle;
    }

    /**
     * returns index number index from the hard drive
     *
     * @author tylerr!!
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
