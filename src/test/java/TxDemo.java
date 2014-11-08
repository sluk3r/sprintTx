import cn.baiing.dao.TxDao;
import cn.baiing.dao.impl.TxDaoImpl;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by baiing on 2014/11/7.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/common_dao.xml"})
public class TxDemo {
    private static Logger logger = Logger.getLogger(TxDao.class);
    @Autowired
    TxDao txDao;

    @After
    public void tearDown() {
        try {
            logger.info("before tearDown");
            txDao.deleteTable(TxDaoImpl.springTx1, TxDaoImpl.ID_VALUE);
            txDao.deleteTable(TxDaoImpl.springTx2, TxDaoImpl.ID_VALUE);
            logger.info("after tearDown");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void readCommit() throws InterruptedException, ExecutionException {
        ExecutorService e = Executors.newCachedThreadPool();
        Future<Date> future = e.submit(makeTestTask());

        logger.info("future result get: " + future.get());
    }


    private Callable<Date> makeTestTask() {
        Callable<Date> result = new Callable<Date>() {
            @Override
            public Date call() throws Exception {
                CountDownLatch countDownLatch = txDao.getCountDownLatch();

                Runnable tx = new Runnable() {
                    @Override
                    public void run() {
                        logger.info("readCommit test");
                        try {
                            txDao.save2TablesData();
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage(),e);
                        }
                        logger.info("tx ends, and junit thread sleep for 1 minuts before clean data");
                    }
                };

                Thread txThread = new Thread(tx, "txThread");
                txThread.start();
                logger.info("txThread.start, and wait for 30s in order to make sure the data  insertted into table one  has been processed by MySQL");
                TimeUnit.SECONDS.sleep(5);
                logger.info("sleep ends");


                try {
                    logger.info("before row from tableOne, which should be lock, and this would throw ");
                    boolean rowFromTableOne =txDao.getById(TxDaoImpl.springTx2, TxDaoImpl.ID_VALUE);
                    assertFalse("due to repeatable read isolation level, the result should be false", rowFromTableOne);

                    logger.info("before countDownLatch.countDown, CountDownLatch#getCount  " + countDownLatch.getCount());
                    countDownLatch.countDown();
                    logger.info("after  countDownLatch.countDown, CountDownLatch#getCount  " + countDownLatch.getCount());
                    assertTrue(txDao.getById(TxDaoImpl.springTx2, TxDaoImpl.ID_VALUE));
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

                return new Date();
            }
        };

        return result;
    }

}



/*
1, 现在的测试是

 */