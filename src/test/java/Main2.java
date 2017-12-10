import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main2 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String text = sc.next();
		String pat = sc.next();
//		String text = "abaacxbcbbbbacc";
//		String pat = "cbc";
		List<int[]> list = new ArrayList<>();
		for(int i=0; i<text.length(); i++) {
			list.add(match(text.substring(i), pat, i));
		}
		int max = text.length()+2;
		int index = 0;
		for (int i=0; i<list.size(); i++) {
//			System.out.println(list.get(i)[0]+" "+list.get(i)[1] + " " + list.get(i)[2]);
			if(list.get(i)[2]<max) {
				max = list.get(i)[1]-list.get(i)[0];
				index = i;
			}
		}
		System.out.println(list.get(index)[0]+" "+list.get(index)[1] );
	}
	
	private static int[] match(String text, String pat, int s) {
		int[] indexs = {-1,-1,text.length()+s+1};
		char[] c_pat = pat.toCharArray();
		int s_index = text.indexOf(c_pat[0]);
		if(s_index<0 || text.length()-s_index<c_pat.length) {
			return indexs;
		}
		int c_index = s_index+1;
		for(int i=1; i<c_pat.length; i++) {
			if(c_index>=text.length()) {
				break;
			} else {
				int temp = text.substring(c_index).indexOf(c_pat[i]);
				if(temp>=0) {
					c_index += temp;
					if(i==c_pat.length-1) {
						indexs[0] = s_index+s;
						indexs[1] = c_index+s;
						indexs[2] = c_index-s_index;
						break;
					}
					c_index++;
				}
			}
		}
//		System.out.println(indexs[0]+" "+indexs[1]);
		return indexs;
	}
}