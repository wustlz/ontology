import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
//		Scanner sc = new Scanner(System.in);
//		String s = sc.next();
		
		String s = "123456";
		
		char[] cs = s.toCharArray();
		
		Set<Integer> num = new HashSet<>();
		for(int i=0; i<10; i++) {
			num.add(i);
		}
		
		int t = cs[cs.length-1]-'0';
		if(num.contains(t)) {
			num.remove(t);
		}
		
		for(int i=0; i<cs.length-1; i++) {
			int c_num = cs[i] - '0';
			if(num.contains(c_num)) {
				num.remove(c_num);
			}
			for(int j=i+1; j<cs.length; j++) {
				if(cs[i]>cs[j]) {
					char tmp = cs[i];
					cs[i] = cs[j];
					cs[j] = tmp;
				}
			}
		}
		
		for (Integer integer : num) {
			System.out.print(integer + " ");
		}
		System.out.println();
		
		
		int min_num = -1;
		if(num.size()>0) {
			min_num = 1;
			for (Integer i : num) {
				if(min_num>i && i>0) {
					min_num = i;
				}
				if(i>0) {
					System.out.println(i);
					return;
				}
			}
		}
		
		if(cs[0]!='0') {
			if(cs[1]-'0'>min_num) {
				System.out.println(min_num+"0");
			} else {
				System.out.println(cs[1]+"0");
			}
		} else {
			System.out.println(cs[0]+"0");
		}
	}
	
}