
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
public class BubbleSort
{
    static RAM memory = new RAM();
    static RAM stick2 = new RAM();

    public static void main(String[] args) // Assuming that RAM_SIZE is a divisor of DRIVE_SIZE
    {
        int startIndex = Computer.DRIVE_SIZE - Computer.RAM_SIZE;


        // Load up Memory
        memory.load(0);
        stick2.load(0);

        int temp = 0;
        boolean condition = false;

        // Outer loop; each new loop is another full sweep of the entire drive up to lim
        for (int lim = Computer.DRIVE_SIZE; lim != Computer.RAM_SIZE; lim--) {

            // Loads from index 0 to index Computer.RAM_SIZE
            memory.load(0);

            // Does one sweep of memory (highest brought to end)
            for (int y = 0; y != Computer.RAM_SIZE-1; y++) {
                if (memory.get(y) > memory.get(y+1)) {
                    temp = memory.get(y);
                    memory.set(y, memory.get(y+1));
                    memory.set(y+1, temp);
                }
            }



            // Writes memory into stick2
            for (int y = 0; y != Computer.RAM_SIZE; y++) {
                stick2.set(y, memory.get(y));
            }


            // Repeat for other regions, repeats all the way until if accessing the hard drive from x would cause it to
            // load elements beyond lim
            for (int x = Computer.RAM_SIZE; x+Computer.RAM_SIZE <= lim; x += Computer.RAM_SIZE) {

                // Loads starting from last end index + 1 (e.g. first time it would load up Computer.RAM_SIZE, next it
                // would load Computer.RAM_SIZE * 2)
                memory.load(x);

                // concerning the last region of ram (stick2; or from hard drive index x - Computer.RAM_SIZE to x-1)
                // to current region of ram (memory; or from hard drive index x to x + Computer.RAM_SIZE - 1).
                // It compares the last element of stick2 to memory so that the sweep effectively continues
                if (stick2.get(Computer.RAM_SIZE-1) > memory.get(0)) {
                    temp = stick2.get(Computer.RAM_SIZE-1);
                    stick2.set(Computer.RAM_SIZE-1, memory.get(0));
                    memory.set(0, temp);

                }

                // Saves to memory
                stick2.save(x-Computer.RAM_SIZE);

                // Does one sweep across the elements loaded in memory
                for (int y = 0; y != Computer.RAM_SIZE-1; y++) {
                    if (memory.get(y) > memory.get(y+1)) {
                        temp = memory.get(y);
                        memory.set(y, memory.get(y+1));
                        memory.set(y+1, temp);
                    }
                }

                // Copies memory to stick2
                for (int y = 0; y != Computer.RAM_SIZE; y++) {
                    stick2.set(y, memory.get(y));
                }

            }

            // Saves stick2 at the end
            stick2.save(lim - (lim % Computer.RAM_SIZE) - Computer.RAM_SIZE);

            // For a bit that will be missed since x+Computer.RAM_SIZE <= lim ignored that bit
            // Does a "sweep" starting from the end index in stick2 (so if lim is 7, and our ram is 3, this'll do a
            // sweep from 6 to 7; and when lim is 8, and our ram is 3, this'll sweep from 6 to 8. When lim is 9 this
            // won't need to happen)
            // Better explained through the example given in the document
            if (lim % Computer.RAM_SIZE != 0) {

                memory.load(lim - Computer.RAM_SIZE);

                for (int y = Computer.RAM_SIZE - 1 - (lim % Computer.RAM_SIZE) ; y != Computer.RAM_SIZE - 1 && lim % Computer.RAM_SIZE != 1; y++) {
                    if (memory.get(y) > memory.get(y + 1)) {
                        temp = memory.get(y);
                        memory.set(y, memory.get(y + 1));
                        memory.set(y + 1, temp);
                    }
                }
                if (lim % Computer.RAM_SIZE == 1) {
                    if (memory.get(Computer.RAM_SIZE - 2) > memory.get(Computer.RAM_SIZE - 1)) {
                        temp = memory.get(Computer.RAM_SIZE - 1);
                        memory.set(Computer.RAM_SIZE - 1, memory.get(Computer.RAM_SIZE - 2));
                        memory.set(Computer.RAM_SIZE - 2, temp);
                    }
                }
                // Saves memory
                memory.save(lim - Computer.RAM_SIZE);
            }

        }

        // Sorts the first region (index 0 to RAM_SIZE), does all the sweeps needed for it to sort.
        memory.load(0);
        for (int x = Computer.RAM_SIZE-1; x != 0; x--) {
            for (int y = 0; y != x; y++) {
                if (memory.get(y) > memory.get(y+1)) {
                    temp = memory.get(y);
                    memory.set(y, memory.get(y+1));
                    memory.set(y+1, temp);
                }
            }
        }
        memory.save(0);
        // Should be sorted now
        memory.driveDump();
        memory.printStats();
        stick2.printStats();
    }
}
