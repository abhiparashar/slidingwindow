package pattern01;

public class NumberOfSubarrays {

  public int numOfSubarrays(int[] nums, int k, int threshold) {
    int target = threshold * k;
    int windowSum = 0;
    int count = 0;
    for (int i = 0; i < k; i++) {
      windowSum += nums[i];
    }
    if (windowSum >= target) {
      count++;
    }
    for (int i = k; i < nums.length; i++) {
      windowSum += nums[i] - nums[i - k];
      if (target >= windowSum) {
        count++;
      }
    }
    return count;
  }

  public static void main(String[] args) {
    NumberOfSubarrays obj = new NumberOfSubarrays();

    // LeetCode Example 1
    System.out.println(
        obj.numOfSubarrays(
            new int[] { 2, 2, 2, 2, 5, 5, 5, 8 },
            3,
            4)); // 3

    // LeetCode Example 2
    System.out.println(
        obj.numOfSubarrays(
            new int[] { 11, 13, 17, 23, 29, 31, 7, 5, 2, 3 },
            3,
            5)); // 6

    // Additional Test Cases

    System.out.println(
        obj.numOfSubarrays(
            new int[] { 1, 1, 1, 1, 1 },
            2,
            1)); // 4

    System.out.println(
        obj.numOfSubarrays(
            new int[] { 1, 2, 3, 4, 5 },
            2,
            4)); // 2

    System.out.println(
        obj.numOfSubarrays(
            new int[] { 5 },
            1,
            5)); // 1
  }
}
