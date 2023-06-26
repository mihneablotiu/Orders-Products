import java.io.*;
import java.util.concurrent.Semaphore;

public final class Products implements Runnable {
    private final String productsInputFile;
    private final BufferedWriter productsWriter;
    private final String orderId;
    private final int productNumberFromOrder;
    private final Semaphore semaphore;

    /**
     * The constructor of the Products class.
     * @param productsInputFile The path of the products input file.
     * @param productsWriter The writer of the products output file.
     * @param orderId The id of the current order that the thread is searching for.
     * @param productNumberFromOrder The number of product from the current order that the thread is searching for.
     * @param semaphore The semaphore that will be incremented by the thread when it will find a product.
     */
    public Products(final String productsInputFile, final BufferedWriter productsWriter, final String orderId,
                    final int productNumberFromOrder, final Semaphore semaphore) {
        this.productsInputFile = productsInputFile;
        this.productsWriter = productsWriter;
        this.orderId = orderId;
        this.productNumberFromOrder = productNumberFromOrder;
        this.semaphore = semaphore;
    }


    /**
     * The run method of the Products class. Each of the threads is going to open the products file
     * and read one by one a line from the file. For each of the lines, the thread is going to extract
     * the order id and the product id and if the order id is the same as the one from the order that
     * we are processing, we have to check if the number of the product from the current order in the
     * file is the same as the one that we are searching for. If it is we have to release the semaphore
     * once because we found one product from the order, and we have to mark the product as shipped.
     *
     * Otherwise, if the order id is not the same as the one from the order that we are processing, we
     * just read the next line from the file. If the order id is the same but the product number is
     * different, we have to read the next line from the file and mark that we found one more product
     * form the current order but not the one that we are searching for.
     */
    @Override
    public void run() {
        try {
            BufferedReader productsReader = new BufferedReader(new FileReader(this.productsInputFile));
            int numberOfProductFromCurrentOrder = 1;

            String currentProductLine = productsReader.readLine();

            while (currentProductLine != null) {
                String[] tokens = currentProductLine.split(",");
                String currentOrderId = tokens[0];

                if (currentOrderId.equals(this.orderId)) {
                    if (numberOfProductFromCurrentOrder == this.productNumberFromOrder) {
                        this.semaphore.release();

                        synchronized (this.productsWriter) {
                            this.productsWriter.write(currentProductLine + ",shipped");
                            this.productsWriter.newLine();
                        }

                        break;
                    } else {
                        numberOfProductFromCurrentOrder++;
                    }
                }

                currentProductLine = productsReader.readLine();
            }

            productsReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
