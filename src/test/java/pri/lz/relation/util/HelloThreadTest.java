package pri.lz.relation.util;

public class HelloThreadTest {
	public static void main(String[] args) {
		HelloThread r = new HelloThread("test");

		Thread t1 = new Thread(r);
		Thread t2 = new Thread(r);

		t1.start();
		t2.start();

	}

}

class HelloThread implements Runnable {
	int i;
	private String name;
	
	public HelloThread(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		
		System.out.println("name: " + name);

		while (true) {
			System.out.println("Hello number: " + i++);

			try {
				Thread.sleep((long) Math.random() * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (50 == i) {
				break;
			}
		}

	}
}