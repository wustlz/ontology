package pri.lz.relation.util;

import java.util.ArrayList;
import java.util.List;

public class Analy {
	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		for(int i=1; i<10; i++) {
			list.add(""+i+""+i+""+i);
		}
		Num i = new Num(0); // 新建对象，准备传递给线程
		new OwnThread(i, list).start(); // 新建线程，并启动
		new OwnThread(i, list).start(); // 新建线程，并启动
		System.out.println("主线程中i的值变为了：" + i.i); // 获取目前对象i的数值
	}
}

class OwnThread extends Thread {
	Num id; // 申明对象，默认null，就是没有指向任何实体
	int sno; // 申明int变量。因为系统默认初始化为0，所以应该是定义一个int变量
	List<String> list;

	OwnThread(Num id, List<String> list) {
		this.id = id;
		this.list = list;
	}

	public void run() {
		while (true) {
			synchronized (this) {
				sno = id.i; // 保存id.i的数值，到线程私有变量sno
				if(sno>=list.size()) {
					break;
				}
				id.i++;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
			}
			System.out.println(this.getName() + "," + sno);
		}
	}
}

class Num{ // 定义一个类

	int i;

	Num(int i) {
		this.i = i;
	}
}