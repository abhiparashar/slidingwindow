package pattern03;

public class MinimumSizeSubarraySum {
  public int minSubArrayLen(int target, int[] nums) {
    int left = 0;
    int minLen = Integer.MAX_VALUE;
    int maxSum = 0;
    for (int right = 0; right < nums.length; right++) {
      // Expand
      maxSum += nums[right];
      // Shrink
      while (maxSum >= target) {
        minLen = Math.min(maxSum, right - left + 1);
        maxSum -= nums[left];
        left++;
      }
    }
    return minLen == Integer.MAX_VALUE ? 0 : minLen;
  }

  public static void main(String[] args) {
    MinimumSizeSubarraySum sol = new MinimumSizeSubarraySum();
    // LeetCode Example 1
    System.out.println(
        sol.minSubArrayLen(7, new int[] { 2, 3, 1, 2, 4, 3 })); // 2

    // LeetCode Example 2
    System.out.println(
        sol.minSubArrayLen(4, new int[] { 1, 4, 4 })); // 1

    // LeetCode Example 3
    System.out.println(
        sol.minSubArrayLen(11, new int[] { 1, 1, 1, 1, 1, 1, 1, 1 })); // 0

    // Additional Test Cases

    System.out.println(
        sol.minSubArrayLen(15, new int[] { 1, 2, 3, 4, 5 })); // 5

    System.out.println(
        sol.minSubArrayLen(5, new int[] { 5 })); // 1

    System.out.println(
        sol.minSubArrayLen(6, new int[] { 5 })); // 0
  }
}
