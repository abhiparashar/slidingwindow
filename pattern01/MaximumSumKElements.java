package pattern01;

public class MaximumSumKElements {

  public int maxSum(int[] nums, int k) {
    int maxSum = 0;
    int windowSum = 0;
    for (int i = 0; i < k; i++) {
      windowSum += nums[i];
    }
    maxSum = windowSum;
    for (int i = k; i < nums.length; i++) {
      windowSum += nums[i] - nums[i - k];
      maxSum = Math.max(maxSum, windowSum);
    }
    return maxSum;
  }

  public static void main(String[] args) {
    MaximumSumKElements sol = new MaximumSumKElements();

    // Classic Example 1
    System.out.println(
        sol.maxSum(new int[] { 2, 1, 5, 1, 3, 2 }, 3)); // 9

    // Classic Example 2
    System.out.println(
        sol.maxSum(new int[] { 2, 3, 4, 1, 5 }, 2)); // 7

    // Edge Cases

    System.out.println(
        sol.maxSum(new int[] { 1, 1, 1, 1, 1 }, 2)); // 2

    System.out.println(
        sol.maxSum(new int[] { 5 }, 1)); // 5

    System.out.println(
        sol.maxSum(new int[] { -1, -2, -3, -4 }, 2)); // -3

    System.out.println(
        sol.maxSum(new int[] { 4, 2, 1, 7, 8, 1, 2, 8, 1, 0 }, 3)); // 16
  }
}
