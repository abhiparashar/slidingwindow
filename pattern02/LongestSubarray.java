package pattern02;

public class LongestSubarray {
  public int longestSubarray(int[] nums) {
    int maxLength = 0;
    int left = 0;
    int kCount = 0;
    for (int right = 0; right < nums.length; right++) {
      if (nums[right] == 0) {
        kCount++;
      }
      while (kCount > 1) {
        if (nums[left] == 0) {
          kCount--;
        }
        left++;
      }
      maxLength = Math.max(maxLength, right - left);
    }
    return maxLength;
  }

  public static void main(String[] args) {
    LongestSubarray sol = new LongestSubarray();
    int[] arr = { 1, 1, 0, 1 };
    int[] arr1 = { 0, 1, 1, 1, 0, 1, 1, 0, 1 };
    int[] arr2 = { 1, 1, 1 };
    System.out.println(sol.longestSubarray(arr));
    System.out.println(sol.longestSubarray(arr1));
    System.out.println(sol.longestSubarray(arr2));
  }
}
