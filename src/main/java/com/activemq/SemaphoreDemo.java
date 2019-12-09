package com.activemq;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author li_hhui
 * @date:2019年12月7日
 * @version:
 * 使用semaphore 模拟停车场
 * 假设停车场有两个车位
 * 同时最多能放两辆车，其他车需要等待
 */
public class SemaphoreDemo {
	
	private static Semaphore semaphore = new Semaphore(2);
	/**
	 * 多线程处理车辆进入停车场
	 * @author lihonghui
	 *
	 */
	public static  class ParkTest implements Runnable{
		private String name;
		public ParkTest(String name){
			this.name = name;
			
		}
		@Override
		public void run() {
			int stoptime=new Random().nextInt(10);
			try {
				semaphore.acquire();//车辆运行进入
				System.out.println("车"+this.name+"车辆进入");
				TimeUnit.SECONDS.sleep(stoptime);//
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				semaphore.release();
				System.out.println("车"+this.name+"车辆在停车场停了"+stoptime+"秒后离开");
			}
			
		}
		
		
	}
	
	
	public static void main(String[] args) {
		ExecutorService pool= Executors.newCachedThreadPool();//创建一个线程池
		pool.submit(new ParkTest("1"));
		pool.submit(new ParkTest("2"));
		pool.submit(new ParkTest("3"));
		pool.submit(new ParkTest("4"));
		pool.submit(new ParkTest("5"));
		pool.submit(new ParkTest("6"));
		pool.submit(new ParkTest("7"));
		pool.submit(new ParkTest("8"));
		pool.submit(new ParkTest("9"));
		pool.submit(new ParkTest("10"));
		pool.shutdown();
		
		
	}
	
	

}
