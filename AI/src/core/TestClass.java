package core;

class TestClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i=0;
		int[] a={1,2,3,4,5,6};
		while(i<a.length-1){
			a[++i]=a[i-1];
			System.out.println(a[i]);
		}
		

	}
	public static void print(int a,int b){
		System.out.println(a);
		System.out.println(b);
	}
}
