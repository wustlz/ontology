package util;

import org.junit.Test;

public class OrderTest {

	// 冒泡排序
	@Test
	public void Maopao(){
		int[] nums = {3,7,5,7,8,3,4,6,9,0};
		int leg = nums.length;
		System.out.println("----before: ");
		printInts(nums);
		for (int i = 0; i < leg-1; i++) {
			for (int j = i+1; j < leg; j++) {
				if(nums[j]>nums[i]){
					int temp = nums[i];
					nums[i] = nums[j];
					nums[j] = temp;
				}
			}
			System.out.println(i + " order: ");
			printInts(nums);
		}
		System.out.println("---after: ");
		printInts(nums);
	}
	
	private void printInts(int[] nums){
		for (int i : nums) {
			System.out.print(i + " , ");
		}
		System.out.println();
	}
}
