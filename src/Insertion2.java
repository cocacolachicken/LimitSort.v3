
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
 * @author (your name)
 * @version (date of completion)
 */
public class Insertion2
{
    static RAM memory = new RAM();
    static RAM stick2 = new RAM();

    public static void main(String[] args)
    {
        // Sorts the first three elements via bubble sort
        int temp = 0;
        memory.load(0);
        int pastUpper = 0, pastLower = 0, middle = 0, right = 0, left = 0;
        int indexTo = 0;

        memory.load(15);
        memory.set(4, 3);
        memory.save(15);


        memory.driveDump();

        insert(0, 19, 3);

        System.exit(0);

        for (int x = 0; x != 2; x++) {
            memory.memoryDump();
            if (memory.get(x) > memory.get(x + 1)) {
                temp = memory.get(x);
                memory.set(x, memory.get(x+1));
                memory.set(x+1, temp);
            }
        }

        memory.memoryDump();

        if (memory.get(0) > memory.get(1)) {
            temp = memory.get(0);
            memory.set(0, memory.get(1));
            memory.set(1, temp);
        }
        memory.save(0);

        memory.memoryDump();


        // Sorts from index 3 to Computer.DRIVE_SIZE - 1
        for (int x = 3; x != Computer.DRIVE_SIZE; x++) {
            memory.load(x);
            temp = memory.get(0);

            left = 0;
            right = x-1;

            while (left < right) {
                middle = (right + left)/2;
                if (temp > fetch(middle, stick2)) {
                    left = middle + 1;
                } else if (temp < fetch(middle, stick2)) {
                    right = middle;
                }
            }

            if (temp > fetch(x-1, stick2)) left = x;

            System.out.println("Inserting " + temp + " (element " + x + ") into index " + left);
            insert(left, x, temp);

        }

        memory.driveDump();
    }

    public static void insert (int insertIndex, int shiftTo, int insert) {
        System.out.println("Before inserting");
        stick2.driveDump();

        if (insertIndex + Computer.RAM_SIZE < Computer.DRIVE_SIZE) {
            if (shiftTo - insertIndex < Computer.RAM_SIZE) {
                System.out.println("Insertion: 1 memory stick");

                stick2.load(insertIndex);
                int e = insert, temp = 0;
                for (int x = 0; x != shiftTo - insertIndex; x++) {
                    temp = stick2.get(x);
                    stick2.set(x, e);
                    e = temp;
                }

                stick2.set(shiftTo - insertIndex, e);

                stick2.save(insertIndex);
            } else { //@todo fix the insert: case where the

                int e = 0;

                memory.load(shiftTo - shiftTo % Computer.RAM_SIZE);

                e = memory.get(shiftTo % Computer.RAM_SIZE);
                System.out.println(e);
                memory.memoryDump();

                for (int x = shiftTo % Computer.RAM_SIZE; x != 0; x--) {
                    memory.set(x, memory.get(x-1));
                }

                memory.memoryDump();

                for (int x = 0; x != Computer.RAM_SIZE; x++) {
                    stick2.set(x, memory.get(x));
                }

                stick2.memoryDump();

                for (int x  = shiftTo - shiftTo % Computer.RAM_SIZE - Computer.RAM_SIZE; x >= insertIndex; x-= Computer.RAM_SIZE) {
                    memory.load(x);
                    stick2.set(0, memory.get(Computer.RAM_SIZE - 1));
                    stick2.memoryDump();
                    stick2.save(x + Computer.RAM_SIZE);

                    stick2.driveDump();

                    for (int y = Computer.RAM_SIZE-1; y != 0; y--) {
                        memory.memoryDump();
                        memory.set(y, memory.get(y-1));
                    }
                    memory.memoryDump();

                    for (int y = 0; y != Computer.RAM_SIZE; y++) {
                        stick2.set(y, memory.get(y));
                    }
                }

                System.out.println(";last");
                if (insertIndex % Computer.RAM_SIZE != 0) {
                    memory.load(insertIndex - insertIndex % Computer.RAM_SIZE);
                    stick2.set(0, memory.get(Computer.RAM_SIZE - 1));
                    stick2.memoryDump();
                    stick2.save(insertIndex - insertIndex % Computer.RAM_SIZE + Computer.RAM_SIZE);

                    for (int y = Computer.RAM_SIZE-1; y != insertIndex % Computer.RAM_SIZE; y--) {
                        memory.memoryDump();
                        memory.set(y, memory.get(y-1));
                    }

                    memory.set(insertIndex % Computer.RAM_SIZE, insert);

                    memory.save(insertIndex - insertIndex % Computer.RAM_SIZE);

                } else {
                    stick2.set(0, insert);
                    stick2.save(insertIndex);
                }



            }

        } else { // Case where shiftTo and insertIndex are entirely within the region of DRIVE_SIZE - RAM_SIZE to DRIVE_SIZE-1

            stick2.load(Computer.DRIVE_SIZE - Computer.RAM_SIZE);


            int e = insert;
            int temp = 0;

            for (int x = insertIndex % Computer.RAM_SIZE; x != shiftTo % Computer.RAM_SIZE && x != Computer.RAM_SIZE; x++) {
                temp = stick2.get(x);
                stick2.set(x, e);
                e = temp;
            }
            stick2.set(shiftTo % Computer.RAM_SIZE, e);
            stick2.save(Computer.DRIVE_SIZE - Computer.RAM_SIZE);
        }

        stick2.driveDump();
    }

    public static int fetch (int index, RAM stick2) {
        if (index + Computer.RAM_SIZE < Computer.DRIVE_SIZE) {
            stick2.load(index);
            return stick2.get(0);
        } else {
            stick2.load(Computer.DRIVE_SIZE-Computer.RAM_SIZE);
            return stick2.get((index % Computer.RAM_SIZE));
        }
    }
}
