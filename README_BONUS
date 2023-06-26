Algoritmi paraleli și distribuiți - Bonus
Tema 2 - Bloțiu Mihnea Andrei - 333CA - 18.12.2022


In the bonus of this problem it was required to do an implementation of this algorithm that the orders
threads (the ones reading orders from the „orders.txt” file) to be able to read the orders in parallel
but for each of them to always read a section of the file that they should take care of.

In other words, the order threads should be able to read the orders in such a way that they don't have
to read the entire file, but only a section of it when they need one.


The solution I implemented to this problem can be found in Tema2.java and Orders.java files and consists
of the following steps:
    - First of all in the main function of this program we just open the input order file and we send
    the reader of this file as a reference to each of the Orders class instances. This means that each
    of the order threads will have a reference to the same file reader;
    - In the Orders class we have a part of the run method that reads the next order from the file using
    this reference to the file reader. This part is done by using a synchronized block on the file reader
    meaning that only one order thread can read from the file at a time so there will never be two
    threads reading the same line from the file;
    - Also, what this also means is that after one order thread reads a line from the file, the next
    order thread that joins the synchronized block will read the next line from the file and so on
    because the file reader will be already positioned at the next line;
    - By doing this we can ensure that:
        * There will never be two threads reading the same line from the file so there will never be
        two threads solving the same order;
        * Each of the threads, when it need to read a new order from the file will know exactly what
        line to read from the file because the file reader will be already positioned at the next
        available line;
        * At the end of the program the orders input file will be read completely only once.
