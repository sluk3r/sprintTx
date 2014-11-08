package cn.baiing.dao;

import java.util.concurrent.CountDownLatch;

/**
 * Created by baiing on 2014/11/7.
 */

public interface TxDao {

    public void save2TablesData() throws InterruptedException;

    public boolean getById(String table,int id);

    public void deleteTable(String table, int id);

    public CountDownLatch getCountDownLatch();

}
