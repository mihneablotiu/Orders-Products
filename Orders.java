import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public final class Orders extends Thread {
    private final int threadId;
    private final BufferedReader ordersReader;
    private final BufferedWriter ordersWriter;
    private final BufferedWriter productsWriter;
    private final String productsInputFile;
    private final ExecutorService productsPool;
    private final CyclicBarrier barrier;

    /**
     * The constructor of the Orders class.
     * @param threadId The id of the current thread.
     * @param ordersReader The reader of the orders file.
     * @param ordersWriter  The writer of the orders file.
     * @param productsWriter The writer of the products file.
     * @param productsInputFile The path of the products file.
     * @param productsPool The pool of threads that will process the products.
     * @param barrier The barrier that will synchronize the orders threads
     *                in order to know when to close the thread pool.
     */
    public Orders(final int threadId, final BufferedReader ordersReader, final BufferedWriter ordersWriter, final BufferedWriter productsWriter,
                  final String productsInputFile, final ExecutorService productsPool, final CyclicBarrier barrier) {
        this.threadId = threadId;
        this.ordersReader = ordersReader;
        this.ordersWriter = ordersWriter;
        this.productsWriter = productsWriter;
        this.productsInputFile = productsInputFile;
        this.productsPool = productsPool;
        this.barrier = barrier;
    }

    /**
     * The run method of the Orders class. Each of the threads is going to read one by one a line
     * from the orders file and process it. In this way we can be sure that no two threads will
     * process the same line and each of the threads knows exactly which line to process. For
     * each of the lines, the thread is going to extract the order id and the number of products
     * and if there are more than 0 products, it will create a semaphore starting with 0 permits
     * that will be incremented by each of the products threads when they will process a product
     * from the current order. When the number of permits will be equal to the number of products
     * from the current order, the thread will acquire the permits and will mark the current order
     * as shipped and then go to the next order.
     *
     * At the end of the processing of each order, the thread will wait for the other threads to
     * finish their processing, and then the first thread will close the thread pool.
     */
    @Override
    public void run() {
        try {
            String currentCommandLine;

            synchronized (this.ordersReader) {
                currentCommandLine = this.ordersReader.readLine();
            }

            while (currentCommandLine != null) {
                String[] tokens = currentCommandLine.split(",");
                String orderId = tokens[0];
                int numberOfProducts = Integer.parseInt(tokens[1]);

                if (numberOfProducts > 0) {
                    Semaphore semaphore = new Semaphore(0);
                    for (int productNumber = 1; productNumber <= numberOfProducts; productNumber++) {
                        this.productsPool.submit(new Products(this.productsInputFile, this.productsWriter, orderId, productNumber, semaphore));
                    }

                    semaphore.acquire(numberOfProducts);
                    synchronized (this.ordersWriter) {
                        this.ordersWriter.write(currentCommandLine + ",shipped");
                        this.ordersWriter.newLine();
                    }
                }

                synchronized (this.ordersReader) {
                    currentCommandLine = this.ordersReader.readLine();
                }
            }

            this.barrier.await();
            if (threadId == 0) {
                this.productsPool.shutdown();
            }

        } catch (IOException | InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

}
