package pattern01;

public class MaximumAverageSubarray {
  public double findMaxAverage(int[] nums, int k) {
    long windowSum = 0;
    for (int i = 0; i < k; i++) {
      windowSum += nums[i];
    }
    long maxSum = windowSum;
    for (int right = k; right < nums.length; right++) {
      windowSum += nums[right] - nums[right - k];
      maxSum = Math.max(maxSum, windowSum);
    }
    return (double) maxSum / k;
  }

  public static void main(String[] args) {
    MaximumAverageSubarray sol = new MaximumAverageSubarray();

    // LeetCode Example 1
    System.out.println(
        sol.findMaxAverage(new int[] { 1, 12, -5, -6, 50, 3 }, 4)); // 12.75

    // LeetCode Example 2
    System.out.println(
        sol.findMaxAverage(new int[] { 5 }, 1)); // 5.0

    // Additional Test Cases

    System.out.println(
        sol.findMaxAverage(new int[] { 0, 4, 0, 3, 2 }, 1)); // 4.0

    System.out.println(
        sol.findMaxAverage(new int[] { -1 }, 1)); // -1.0

    System.out.println(
        sol.findMaxAverage(new int[] { -1, -12, -5, -6, -50, -3 }, 2)); // -5.5
  }
}
