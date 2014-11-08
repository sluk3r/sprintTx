package countDown;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by baiing on 2014/11/8.
 */
public class CountDownDemo {
    private static final Logger logger = Logger.getLogger(CountDownDemo.class);

    @Test
    public void countDown() throws InterruptedException {
        final CountDownLatch c = new CountDownLatch(1);
        final  int sleepSeconds = 10;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(sleepSeconds);
                    logger.info("CountDownLatch await, CountDownLatch#getCount  " + c.getCount());
                    c.await();
                    logger.info("CountDownLatch await ends, CountDownLatch#getCount  " + c.getCount());
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };

        Thread t = new  Thread(r, "task thread");
        t.start();
        logger.info("thread started");

        TimeUnit.SECONDS.sleep(30);

        logger.info("before CountDownLatch countDown, CountDownLatch#getCount  " + c.getCount());
        c.countDown();
        logger.info("after CountDownLatch countDown, CountDownLatch#getCount  " + c.getCount());

        logger.info("main ends");
    }
}
