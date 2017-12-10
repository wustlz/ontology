public class Main {

	public static void main(String[] args) {
		int n = 100;
		System.out.println(goadd(n));
	}
	
	private static int goadd(int x) {
		if(x==1) {
			return 1;
		} else if (x==2) {
			return 2;
		} else if (x==3) {
			return 4;
		} else {
			return goadd(x-1)+goadd(x-2)+goadd(x-3);
		}
	}
}