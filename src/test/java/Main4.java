import java.util.Scanner;

public class Main4 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while(sc.hasNextLine()) {
			String nm = sc.nextLine();
			String[] nms = nm.split(" ");
			int n = Integer.parseInt(nms[0]);
			int m = Integer.parseInt(nms[1]);
			String[] pics = new String[n];
			for(int i=0;i<n;i++) {
				pics[i] = sc.nextLine();
			}
			int s_row = -1;
			int s_col = -1;
			int e_row = -1;
			int e_col = -1;
			
			for(int i=0; i<n; i++) {
				char[] row = pics[i].toCharArray();
				for(int j=0; j<m; j++) {
					if(row[j]=='*' ) {
						if(s_row==-1) {
							s_row = i;
							e_row = i;
							s_col = j;
							e_col = j;
						}
						if(i>e_row) {
							e_row = i;
						}
						
						if(j<s_col) {
							s_col = j;
						}
						if(j>e_col) {
							e_col = j;
						}
					}
				}
			}
			
			for(int i=s_row; i<=e_row; i++) {
				System.out.println(pics[i].substring(s_col, e_col+1));
			}
		}
	}
}