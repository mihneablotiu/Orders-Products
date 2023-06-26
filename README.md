Algoritmi paraleli și distribuiți
Tema 2 - Bloțiu Mihnea Andrei - 333CA - 18.12.2022


The idea of this homework was to implement a parallel processing algorithm of different orders
and for each of the orders to implement a parallel algorithm for shipping the products from that
order. When all the products from an order are shipped, the order is marked as shipped.

That being said, my implementation of this problem is divided into 3 Java code files, one Makefile
and two READMEs as it follows:
    - Makefile: this file contains the commands to compile and run the program;
    - README: this file contains the description of the problem and the solution;
    - README_BONUS: this file contains the description of the bonus problem and the solution;
    - Tema2.java: this file contains the main class of the program;
    - Order.java: this file contains the class that represents an order and the algorithm for a order;
    - Product.java: this file contains the class that represents a product and the algorithm for a product;

Tema2.java:
    * The main logic of the program. It just opens the order input file and the orders and products output files.
    It creates a thread pool for the products with the maximum allowed threads and a cyclic barrier for
    the order threads in order to know when all the orders are processed, and then the thread pool can be
    shut down. Starts all the order threads and waits for them to finish.
    * At the end of the program, it closes the input and output files mentioned above.

Order.java:
     * In the run method of the Orders class, each of the threads is going to read one by one a line
     from the orders file and process it. In this way we can be sure that no two threads will
     process the same line and each of the threads knows exactly which line to process. For
     each of the lines, the thread is going to extract the order id and the number of products
     and if there are more than 0 products we will submit into the thread pool the number of products
     tasks, one for each of the products in that order. Also we will create a semaphore starting with
     0 permits that will be incremented by each of the products threads when they will find a product
     from the current order. When the number of permits will be equal to the number of products
     from the current order, it means that we finished the product processing from the current order
     and the order thread will acquire the permits and will mark the current order as shipped into
     the output file and then go to the next order.

     * At the end of the processing of each order, the threads will wait for the other threads to
     finish their processing before closing the products thread pool, and then the first thread
     will close the thread pool.

Product.java:
     * In the run method of the Products class, each of the threads is going to open the products file
     and read the file from the beginning line by line. For each of the lines, the thread is going
     to extract the order id and the product id and if the order id is the same as the one from the
     order that we are searching for, we have to check if the number of the product from the current
     order id in the file is the same as the one that we are searching for. If it is we have to release
     the semaphore once because we found one product from the order, and we have to mark the product
     as shipped in the output file.

     * Otherwise, if the order id is not the same as the one from the order that we are processing, we
     just read the next line from the file. If the order id is the same but the product number is
     different, we have to read the next line from the file and mark that we found one more product
     form the current order but not the one that we are searching for.
