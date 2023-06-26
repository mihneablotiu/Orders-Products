import java.io.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Tema2 {
    /**
     * The main logic of the program. It opens the order input file and the orders and products
     * output files. It creates a thread pool for the products and a cyclic barrier for the order threads in
     * order to know when all the orders are processed, and then the thread pool can be shut down. Starts all
     * the order threads and waits for them to finish.
     * @param args the command line arguments. Args[0] is the orders input directory, args[1] is the
     *             maximum number of threads that can be used at the same time on a particular level.
     */
    public static void main(String[] args) {
        String inputFolder = args[0];
        int numberOfThreadsPerLevel = Integer.parseInt(args[1]);

        try {
            BufferedReader ordersReader = new BufferedReader(new FileReader(inputFolder + "/orders.txt"));
            BufferedWriter ordersWriter = new BufferedWriter(new FileWriter("orders_out.txt"));
            BufferedWriter productsWriter = new BufferedWriter(new FileWriter("order_products_out.txt"));

            ExecutorService productsPool = Executors.newFixedThreadPool(numberOfThreadsPerLevel);
            CyclicBarrier barrier = new CyclicBarrier(numberOfThreadsPerLevel);

            Thread[] orders = new Thread[numberOfThreadsPerLevel];
            for (int i = 0; i < numberOfThreadsPerLevel; i++) {
                orders[i] = new Orders(i, ordersReader, ordersWriter, productsWriter,
                        inputFolder + "/order_products.txt", productsPool, barrier);
                orders[i].start();
            }

            for (int i = 0; i < numberOfThreadsPerLevel; i++) {
                try {
                    orders[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ordersReader.close();
            ordersWriter.close();
            productsWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
