/**
 * 
 */
package com.etrans.etsv5.app.redis.cacher;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: FutureTest.java  <br />
 * 说       明: etsv5-redis-cacher  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016上午10:23:15  <br />
 * 最后修改: 2016上午10:23:15  <br />
 * 修改历史:   <br />
 */

public class FutureTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		Task task = new Task();
		
		Future<Integer> result = executor.submit(task);
		executor.shutdown();
		
		try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
         
        System.out.println("主线程在执行任务");
         
        try {
            System.out.println("task运行结果"+result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
         
        System.out.println("所有任务执行完毕");

	}

}


class Task implements Callable<Integer>{

	@Override
	public Integer call() throws Exception {
		System.out.println("子线程在进行计算");
        Thread.sleep(3000);
        int sum = 0;
        for(int i=0;i<100;i++)
            sum += i;
        return sum;
	}
	
}
