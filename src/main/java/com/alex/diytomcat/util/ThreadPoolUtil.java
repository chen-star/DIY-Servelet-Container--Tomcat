package com.alex.diytomcat.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
public class ThreadPoolUtil {

    /**
     * corePoolSize=20: minimum # of thread guaranteed in the pool
     * maximumPoolSize=100: max # of thread in the pool
     * keepAliveTime=60: for the extra threads added (beyond 20), they will be destroyed if no task assigned for 60 sec
     * blockingQueue: when the 20 threads are all busy, the new coming task will be put in the blocking queue.
     * Only if the queue size > 10, we new more threads into poll.
     */
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 100, 60,
            TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(10));

    public static void run(Runnable r) {
        threadPool.execute(r);
    }


}
