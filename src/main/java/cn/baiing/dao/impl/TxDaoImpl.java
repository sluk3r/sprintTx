package cn.baiing.dao.impl;

import cn.baiing.dao.TxDao;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by baiing on 2014/11/7.
 */

public class TxDaoImpl extends JdbcDaoSupport implements TxDao {
    private static final Logger logger = Logger.getLogger(TxDaoImpl.class);
    private static final String SQL_FORMAT = "insert  into  %s  values (%s) ";
    private static final String SELECT_SQL_FORMAT = "select count(1) cnt from %s where id = %s";
    private static final String DELETE_SQL_FORMAT = "delete  from %s where id = %s";
    public static final String springTx1 = "springTx1";
    public static final String springTx2 = "springTx2";
    public static final int ID_VALUE = 1;

    int txSleepMinuts;
    private CountDownLatch countDownLatch;

    @Transactional(rollbackFor=Exception.class, value="baiingDB")
    public void save2TablesData() throws InterruptedException {

        logger.info("before insert data into table 1");
        //对第一个插入操作
        insertTableValue(springTx1, ID_VALUE);
        logger.info("after insert data into table 1, and wait for countDownLatch, CountDownLatch#getCount  " + countDownLatch.getCount());
        //TimeUnit.MINUTES.sleep(getTxSleepMinuts());
        countDownLatch.await();
        logger.info("sleep end, CountDownLatch#getCount  " + countDownLatch.getCount());

        //对第一个插入操作
        logger.info("before insert data into table 2");
        insertTableValue(springTx2, ID_VALUE);
        logger.info("after insert data into table 2");
    }

    public boolean getById(String table,int id) {
        return getJdbcTemplate().queryForInt(String.format(SELECT_SQL_FORMAT, table, id)) > 0;
    }

    public void deleteTable(String table, int id) {
        getJdbcTemplate().update(String.format(DELETE_SQL_FORMAT, table, id));
    }


    private void insertTableValue(String table, int value) {
        String sql = String.format(SQL_FORMAT, table, value);
        getJdbcTemplate().update(sql);
    }

    public int getTxSleepMinuts() {
        return txSleepMinuts;
    }

    public void setTxSleepMinuts(int txSleepMinuts) {
        this.txSleepMinuts = txSleepMinuts;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
