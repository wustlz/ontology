package util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;

import pri.lz.relation.util.ConstantValue;

public class OrderTest {
	
	@Test
	public void computeDate() throws ParseException {
		String s_start = "2012-12-1";
		String s_end = "2017-9-8";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d_start = sdf.parse(s_start);
		Date d_end = sdf.parse(s_end);
		long start = d_start.getTime();
		long end = d_end.getTime();
		long times = (end-start)/1000;
		times /= 3600;
		System.out.println(d_end + " - " + d_start + " = " + times/24);
	}
	private AtomicInteger counter = new AtomicInteger(0);
	   ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	   public int mutexBiz() {
	      try {
	         if (!readWriteLock.writeLock().tryLock()) {
	            return -1;
	         }
	         return counter.getAndIncrement();
	      } finally {
	         readWriteLock.writeLock().unlock();
	      }
	   }
	 
	   public static void main(String[] args) throws InterruptedException {
	      final OrderTest lockTest = new OrderTest();
	      for (int i = 0; i < 5; i++) {
	         new Thread(new Runnable() {
	            public void run() {
	                try {
	                   System.out.print(lockTest.mutexBiz());
	                } catch (Exception e) {
	                   System.out.print("-2");
	                }
	            }
	         }).start();
	      }
	      Thread.sleep(5000);
	   }

	// 冒泡排序
	@Test
	public void Maopao(){
		int[] nums = {3,7,5,7,8,3,4,6,9,0};
		int leg = nums.length;
		System.out.println("----before: ");
		printInts(nums);
		for (int i = 0; i < leg-1; i++) {
			for (int j = i+1; j < leg; j++) {
				if(nums[j]>nums[i]){
					int temp = nums[i];
					nums[i] = nums[j];
					nums[j] = temp;
				}
			}
			System.out.println(i + " order: ");
			printInts(nums);
		}
		System.out.println("---after: ");
		printInts(nums);
	}
	
	private void printInts(int[] nums){
		for (int i : nums) {
			System.out.print(i + " , ");
		}
		System.out.println();
	}
	
	@Test
	public void testRootpath() {
		String rootpath = System.getProperty("user.dir");
		System.out.println(rootpath);
		rootpath = ConstantValue.RELATION_PATH;
		System.out.println(rootpath);
		File file = new File(rootpath);
		if(file.isDirectory()) {
			System.out.println("true");
		}
	}
}
