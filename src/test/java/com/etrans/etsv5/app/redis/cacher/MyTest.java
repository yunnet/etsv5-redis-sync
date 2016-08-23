/**
 * 
 */
package com.etrans.etsv5.app.redis.cacher;

import java.util.concurrent.Phaser;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: MyTest.java  <br />
 * 说       明: etsv5-redis-cacher  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016上午11:49:56  <br />
 * 最后修改: 2016上午11:49:56  <br />
 * 修改历史:   <br />
 * 
 * 等待一批任务全部执行完成后，再继续下一个任务的执行
 * 
 */

public class MyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Phaser phaser = new Phaser(3) {// 共有3个工作线程，因此在构造函数中赋值为3  
            @Override  
            protected boolean onAdvance(int phase, int registeredParties) {  
                System.out.println("\n=========华丽的分割线=============");  
                return registeredParties == 0;  
            }  
        };  
        System.out.println("程序开始执行");  
        char a = 'a';  
        for (int i = 0; i < 3; i++) { // 创建并启动3个线程  
            new MyThread((char) (a + i), phaser).start();  
        }  
  
        while (!phaser.isTerminated()) {// 只要phaser不终结，主线程就循环等待  
            Thread.yield();  
        }  
        System.out.println("程序结束");  
	}

}


class MyThread extends Thread {  
    private char c;  
    private Phaser phaser;  
  
    public MyThread(char c, Phaser phaser) {  
        this.c = c;  
        this.phaser = phaser;  
    }  
  
    @Override  
    public void run() {  
        while (!phaser.isTerminated()) {  
            for (int i = 0; i < 10; i++) { // 将当前字母打印10次  
                System.out.print(c + " ");  
            }  
            // 打印完当前字母后，将其更新为其后第三个字母，例如b更新为e，用于下一阶段打印  
            c = (char) (c + 3);  
            if (c > 'z') {  
                // 如果超出了字母z，则在phaser中动态减少一个线程，并退出循环结束本线程  
                phaser.arriveAndDeregister();  
                break;  
            } else {  
                // 反之，等待其他线程到达阶段终点，再一起进入下一个阶段  
                phaser.arriveAndAwaitAdvance();  
            }  
        }  
    }  
}  
