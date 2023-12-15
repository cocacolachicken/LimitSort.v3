
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
public class BinaryInsertionSort
{

    // This algorithm assumes that Computer.DRIVE_SIZE is divisible by Computer.RAM_SIZE
    static RAM memory = new RAM();
    static RAM stick2 = new RAM();

    public static void main(String[] args)
    {
        // Variable declaration
        int temp, middle, right, left;


        // x is two things: x is the index after the boundary of sorted elements, and x is the element that is being
        // compared to the array of sorted elements
        for (int x = 1; x != Computer.DRIVE_SIZE; x++) {
            memory.load(x - x % Computer.RAM_SIZE); // I couldn't figure out how to optimize this part so that there aren't as many
            // load operations. It's loaded this way because doing memory.load(x) would cause errors if x > DRIVE_SIZE-MEMORY_SIZE
            temp = memory.get(x % Computer.RAM_SIZE);

            // Setting up the variables for left and right. Left also represents the variable that is the index of where element x will be inserted
            left = 0;
            right = x-1;

            while (left < right) {
                middle = (right + left)/2; // Calculate middle
                if (temp > fetch(middle, stick2)) {
                    left = middle + 1; // If middle is above temp, middle becomes left
                } else if (temp < fetch(middle, stick2)) {
                    right = middle; // If middle is below temp, right becomes middle
                    // This one doesn't have -1 because if it did some elements would not get sorted properly
                } else {
                    left = middle; // Else, left becomes middle, and it breaks. It found the index
                    break;
                }
            }
            // The code above did not set the correct index for left when the element being compared was bigger than all
            // elements in the sorted section of memory, so the code below is needed
            if (temp > fetch(x-1, stick2)) left = x;

            // Inserts it. What more can I say?
            insert(left, x, temp);
        }
        stick2.driveDump();

        System.out.println("Sorted! This takes an average of O(n * log(n)) seconds to do (log n for binary search, n for each item).");

        memory.printStats();
        stick2.printStats();

    }

    /**
     * insert method which performs an insert + shift in the drive
     * assumes that insertIndex <= shiftTo
     *
     * @author Tyler
     * @param insertIndex location of where to be inserted
     * @param shiftTo location of where to stop the insert.
     *                effectively deletes that element if insert isn't equal to the element number there
     * @param insert number being inserted in insertIndex
     * */
    public static void insert (int insertIndex, int shiftTo, int insert) {

        if (shiftTo - insertIndex >= Computer.RAM_SIZE) { // Case 1: If the amount of elements shifted (shiftTo-insertIndex) is greater than a "stick" of ram.
                if (shiftTo % Computer.RAM_SIZE != 0) { // If shiftTo % Computer.RAM_SIZE != 0, or if shiftTo isn't a multiple of insertIndex, load shiftTo - shiftTo % Computer.RAM_SIZE then sort a portion of the memory
                    memory.load(shiftTo - shiftTo % Computer.RAM_SIZE);
                    for (int x = shiftTo % Computer.RAM_SIZE; x != 0; x--) { // Shifts all elements starting from shiftTo % Computer.RAM_SIZE (which represents index shiftTo) to the start of the ram stick (shiftTo - shiftTo % Computer.RAM_SIZE)
                        memory.set(x, memory.get(x - 1));
                    }
                    for (int x = 0; x != Computer.RAM_SIZE; x++) { // Sets stick2 to the values of memory
                        stick2.set(x, memory.get(x));
                    }
                } else stick2.load(shiftTo); // Else stick2 just takes shiftTo because there's not really any shifting needed beyong the first element of shiftTo


                for (int x = shiftTo - shiftTo % Computer.RAM_SIZE - Computer.RAM_SIZE /*This starts from shiftTo - shiftTo % Computer.RAM_SIZE - Computer.RAM_SIZE */; x >= insertIndex; x-= Computer.RAM_SIZE) { // This for loop runs as long as there are full ram stick in between the indices insertIndex and shiftTo;
                    // an example is if there were 20 slots of hard drive, 5 ram size, and the insertion was in element 8 and ending at element 16. This part would happen once, as it has the code before this deal with index 15, and this for loop
                    // would run once for index 10, and the code at the bottom would run starting from element 5.
                    // However if I were to insert something from 2 and end the insertion at 17 this code would run twice: once for index 5, and once for index 10
                    memory.load(x);

                    stick2.set(0, memory.get(Computer.RAM_SIZE - 1)); // Sets the first element of stick2 to the last element of memory in order to continue the insertion from stick2
                    stick2.save(x + Computer.RAM_SIZE); // Saves stick2

                    // Shifts all items on the stick one index to the right. This'll delete the last item and create a double item on index 0
                    for (int y = Computer.RAM_SIZE-1; y != 0; y--) {
                        memory.set(y, memory.get(y-1));
                    }

                    for (int y = 0; y != Computer.RAM_SIZE; y++) { // Sets stick2 to memory
                        stick2.set(y, memory.get(y));
                    }
                }

                if (insertIndex % Computer.RAM_SIZE != 0) { // If there's elements that still need to be sorted
                    // This'll happen if insertIndex doesn't divide into Computer.RAM_SIZE neatly

                    // Loads from insertIndex - insertIndex % Computer.RAM_SIZE AKA flooring it
                    memory.load(insertIndex - insertIndex % Computer.RAM_SIZE);
                    stick2.set(0, memory.get(Computer.RAM_SIZE - 1)); // Sets the first element of stick2 to the last element of memory in order to continue the insertion from stick2
                    stick2.save(insertIndex - insertIndex % Computer.RAM_SIZE + Computer.RAM_SIZE); // Saves stick2

                    // Shifts all elements from Computer.RAM_SIZE to insertIndex once to the right
                    for (int y = Computer.RAM_SIZE-1; y != insertIndex % Computer.RAM_SIZE; y--) {
                        memory.set(y, memory.get(y-1));
                    }

                    // Sets insertIndex % Computer.RAM_SIZE once to the right
                    memory.set(insertIndex % Computer.RAM_SIZE, insert);

                    memory.save(insertIndex - insertIndex % Computer.RAM_SIZE);

                } else { // Sets the first element of stick2 to zero if insertIndex % RAM_SIZE == 0 as the insertion ends on the last stick that is sorted above
                    stick2.set(0, insert);
                    stick2.save(insertIndex);
                }

                // Insertion complete
        } else if (shiftTo - insertIndex < Computer.RAM_SIZE && shiftTo != insertIndex) { // Case two: requires less than a stick of ram
            if (shiftTo + Computer.RAM_SIZE > Computer.DRIVE_SIZE) { // This is where shiftTo is above Computer.RAM_SIZE + Comptuer.DRIVE_SIZE
                if (insertIndex < Computer.DRIVE_SIZE-Computer.RAM_SIZE) { // This case is the one where it can be loaded from insertIndex as insertIndex is less than Computer.RAM_SIZE + Comptuer.DRIVE_SIZE
                    stick2.load(insertIndex);
                    for (int x = shiftTo - insertIndex; x != 0; x--) { // Shifts all elements from the last element (shiftTo-insertIndex) 1 to the right
                        stick2.set(x, stick2.get(x-1));
                    }
                    stick2.set(0, insert); // Sets the first element of this to insert to complete the insertion + shift
                    stick2.save(insertIndex);
                    // Insertion complete
                } else { // This is the case where it can't be loaded from from insertIndex, and instead needs to be loaded from Computer.RAM_SIZE + Computer.DRIVE_SIZE
                    stick2.load(Computer.DRIVE_SIZE - Computer.RAM_SIZE);
                    for (int x = shiftTo % Computer.RAM_SIZE; x != insertIndex % Computer.RAM_SIZE; x--) { // Shifts all elements starting from the last element needed to 1 to the right
                        stick2.set(x, stick2.get(x - 1));
                    }
                    stick2.set(insertIndex % Computer.RAM_SIZE, insert); // Sets insertIndex % RAM_SIZE to insert to complete the insertion + shift
                    stick2.save(Computer.DRIVE_SIZE - Computer.RAM_SIZE);
                    // Insertion complete
                }
            } else { // This is when shiftTo is below DRIVE_SIZE - RAM_SIZE, meaning it can be safely loaded from insertIndex
                stick2.load(insertIndex);
                for (int x = shiftTo - insertIndex; x != 0; x--) { // Shifts all elements from the last element (shiftTo-insertIndex) 1 to the right
                    stick2.set(x, stick2.get(x-1));
                }
                stick2.set(0, insert); // Sets the first element of this to insert to complete the insertion + shift
                stick2.save(insertIndex);
                // Insertion complete
            }
        } // Else no insertion done


    }

    /**
     * returns index number index from the hard drive
     *
     * @author tylerr
     * @param index index of number wanted
     * @param stick2 stick2, or the RAM stick wanted.
     *               this is moreso an artifact of when I wanted to optimize the save/load stats.
     * @return drive[index]
     */

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
