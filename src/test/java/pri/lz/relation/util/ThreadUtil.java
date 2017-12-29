package pri.lz.relation.util;

import java.util.ArrayList;
import java.util.List;

/**
* @ClassName: TreadUtil
* @Description: 多线程测试类
* @author 廖劲为
* @date 2017年11月28日 上午11:37:48
* 
*/
public class ThreadUtil {
	
//	public static int index = 0;
	
	public static void main(String[] args) {
		ThreadUtil util = new ThreadUtil();
		util.testThread();
	}
	
	public void testThread() {
		List<String> list = new ArrayList<>();
		for(int i=1; i<10; i++) {
			list.add(""+i+""+i+""+i);
		}
		
		MyThread r = new MyThread(list);
		Thread t1 = new Thread(r);
		Thread t2 = new Thread(r);
		Thread t3 = new Thread(r);
		
		t1.start();
		t2.start();
		t3.start();
	}
}

class MyThread implements Runnable{
	
    int index;
    private List<String> list;
    
    public MyThread(List<String> list) {
		this.list = list;
	}
 
 
    @Override
    public void run() {
//        System.out.println("name:"+name+" 子线程ID:"+Thread.currentThread().getId());
        
        while(index < list.size()) {
        	System.out.println("name: " + Thread.currentThread().getName() + " index: " + index++);
        	try {
//				Thread.sleep((long) Math.random() * 1000);
        		Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
}

class NumIndex{ // 定义一个类
	int i;
	NumIndex(int i) {
		this.i = i;
	}
}