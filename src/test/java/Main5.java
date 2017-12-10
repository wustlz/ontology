import java.util.List;
import java.util.Scanner;

public class Main5 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String nx = sc.nextLine();
		String[] nxs = nx.split(" ");
		int n = Integer.parseInt(nxs[0]);
		int x = Integer.parseInt(nxs[1]);
		
		int[][] ab = new int[n][2];
		
		int min = 0;
		int max = 0;
		boolean flag = false;
		for(int i=0;i<n;i++) {
			String temp = sc.nextLine();
			String[] temps = temp.split(" ");
			
			if(!flag) {
				int t1 = Integer.parseInt(temps[0]);
				int t2 = Integer.parseInt(temps[1]);
				
				if(t1<t2) {
					ab[i][0] = t1;
					ab[i][1] = t2;
				} else {
					ab[i][0] = t2;
					ab[i][1] = t1;
				}
				if(i==0) {
					min = ab[i][0];
					max = ab[i][1];
				} else {
					if(ab[i][0]>max || ab[i][1]<min) {
						flag = true;
					} else {
						if (ab[i][0]>min) {
							min = ab[i][0];
						}
						if(ab[i][1]<max) {
							max = ab[i][1];
						}
					}
				}
			} else {
				continue;
			}
		}
//			System.out.println(min + " " + max);
		if(flag) {
			System.out.println(-1);
		} else if(min<=x && max>=x) {
			System.out.println(0);
		} else if(min>x){
			System.out.println(min-x);
		} else if(max<x) {
			System.out.println(x-max);
		}
		
	}
}