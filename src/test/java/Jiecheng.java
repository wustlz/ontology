import java.util.Scanner;

public class Jiecheng {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while(sc.hasNext()) {
			long n = sc.nextLong();
			int num[] = new int[1000];
	        int i, j;
	        if (n == 1 || n == 0) {
	            System.out.println(1);
	        } else {
	            int p, h;// p 存放当前结果的位数，h为进位；
	            p = 1;
	            h = 0;
	            num[1] = 1;
	            for (i = 2; i <= n; i++) {
	                // 使得a[]的每位与i相乘
	                for (j = 1; j <= p; j++) {
	                    num[j] = num[j] * i + h;
	                    h = num[j] / 10;
	                    num[j] = num[j] % 10;
	                }
	                // 表示向新的位置进位
	                while (h > 0) {
	                    num[j] = h % 10;
	                    h = h / 10;
	                    j++;
	                }
	                p = j - 1;
	            }
	            for (i = p; i >= 1; i--) {
	                System.out.print(num[i]);
	            }
	        }
		}
	}
}