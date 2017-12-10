import java.util.Scanner;

public class zuzhe {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while(sc.hasNext()) {
			long n = sc.nextLong();
			if(n==0) {
				System.out.println(1);
			} else {
				long min = n/2;
				long sum = 1;
				if(n%2!=0) {
					min++;
				}
				for(long i=min; i<n; i++) {
					sum += zuhe(n-i,i);
				}
				System.out.println(sum);
			}
		}
	}
	
	private static long zuhe(long a, long b) {
		double rst = 1;
		for(long i=0; i<a; i++) {
			rst *= (double)(b-i)/(a-i);
		}
		
		return (long)rst;
	}
}