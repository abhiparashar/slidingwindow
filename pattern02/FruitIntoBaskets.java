package pattern02;

import java.util.HashMap;
import java.util.Map;

public class FruitIntoBaskets {
  public int totalFruit(int[] nums) {
    int left = 0;
    int maxLen = 0;
    Map<Integer, Integer> map = new HashMap<>();
    for (int right = 0; right < nums.length; right++) {
      // Expand
      int rightNum = nums[right];
      map.put(rightNum, map.getOrDefault(rightNum, 0) + 1);
      // Shrink
      while (map.size() > 2) {
        int leftNum = nums[left];
        map.put(leftNum, map.get(leftNum) - 1);
        if (map.get(leftNum) == 0) {
          map.remove(leftNum);
        }
        left++;
      }
      maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
  }

  public static void main(String[] args) {
    FruitIntoBaskets sol = new FruitIntoBaskets();
    System.out.println(sol.totalFruit(new int[] { 1, 2, 1 })); // 3
    System.out.println(sol.totalFruit(new int[] { 0, 1, 2, 2 })); // 3
    System.out.println(sol.totalFruit(new int[] { 1, 2, 3, 2, 2 })); // 4
    System.out.println(sol.totalFruit(new int[] { 3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4 })); // 5

    // Additional edge cases
    System.out.println(sol.totalFruit(new int[] { 1 })); // 1
    System.out.println(sol.totalFruit(new int[] { 1, 1, 1, 1 })); // 4
    System.out.println(sol.totalFruit(new int[] { 1, 2, 1, 2, 1, 2 })); // 6
  }
}
