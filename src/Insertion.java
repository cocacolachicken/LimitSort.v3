
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
public class Insertion
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
        memory.driveDump();

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
            if (x % Computer.RAM_SIZE == 0) {
                memory.load(x);
            }
            temp = memory.get(x % Computer.RAM_SIZE);

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
            insert(left, x, temp, stick2);

        }

        memory.driveDump();
    }

    public static void insert (int insertIndex, int shiftTo, int insert, RAM stick2) {
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
            } else {
                System.out.println("Insertion: Tricking it into thinking there's more ram then there really is");


                int e = insert, temp = 0;
                stick2.load(insertIndex);

                for (int x = 0; x != Computer.RAM_SIZE; x++) {
                    temp = stick2.get(x);
                    stick2.set(x, e);
                    e = temp;
                }

                System.out.println("Saving");
                stick2.save(insertIndex);
                stick2.driveDump();

                boolean condition = false;
                int bit = 0;

                //
                for (int x = insertIndex + Computer.RAM_SIZE; x-Computer.RAM_SIZE < shiftTo && x + Computer.RAM_SIZE <= Computer.DRIVE_SIZE; x += Computer.RAM_SIZE) {
                    stick2.load(x);


                    for (int y = 0; y != Computer.RAM_SIZE && y + x - 1 < shiftTo; y++) {
                        temp = stick2.get(y);
                        stick2.set(y, e);
                        e = temp;
                    }

                    stick2.save(x);

                    System.out.println(x);
                    if (x + Computer.RAM_SIZE*2 > Computer.DRIVE_SIZE && shiftTo + Computer.RAM_SIZE > Computer.DRIVE_SIZE) {

                        condition = true;
                        bit = x;
                        System.out.println("Real!!");
                    }
                }

                stick2.driveDump();
                System.out.println(bit % Computer.RAM_SIZE);
                System.out.println(e);
                System.out.println(shiftTo % Computer.RAM_SIZE);

                if (condition) {
                    stick2.load(Computer.DRIVE_SIZE-Computer.RAM_SIZE);

                    temp = stick2.get(bit % Computer.RAM_SIZE);
                    stick2.set(bit % Computer.RAM_SIZE, e);

                    stick2.driveDump();

                    for (int y = bit % Computer.RAM_SIZE; y != shiftTo % Computer.RAM_SIZE && y != 1; y++) {
                        e = temp;
                        stick2.set(y, e);
                        temp = stick2.get(y);
                        stick2.driveDump();
                    }

                    stick2.save(Computer.DRIVE_SIZE-Computer.RAM_SIZE);
                }

                stick2.driveDump();

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
