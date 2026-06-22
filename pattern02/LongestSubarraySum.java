package pattern02;

public class LongestSubarraySum {
  public int longestSubarraySum(int[] nums, int k) {
    if (nums == null || nums.length == 0)
      return 0;
    int maxLength = 0;
    int left = 0;
    int windowSum = 0;
    for (int right = 0; right < nums.length; right++) {
      windowSum += nums[right];
      while (windowSum > k) {
        windowSum -= nums[left];
        left++;
      }
      maxLength = Math.max(maxLength, right - left + 1);
    }
    return maxLength;
  }

  public static void main(String[] args) {
    LongestSubarraySum sol = new LongestSubarraySum();
    int[] arr = { 1, 2, 3, 4, 5 };
    int k = 9;
    int ans = sol.longestSubarraySum(arr, k);
    System.out.println(ans);
  }
}
