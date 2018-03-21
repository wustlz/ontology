import java.text.DecimalFormat;
import java.util.Scanner;

public class Main3 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int t = sc.nextInt();
		int[] a = new int[n];
		for(int i=0; i<n; i++) {
			a[i] = sc.nextInt();
		}
		int max = 0;
		for(int i=0; i<n; i++) {
			if(a[i]>max) {
				max = a[i];
			}
		}
		double d = (double) max/t;
		DecimalFormat df = new DecimalFormat("#.00");
		System.out.println(df.format(d));
	}
	
}