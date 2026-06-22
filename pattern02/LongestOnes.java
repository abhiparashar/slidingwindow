package pattern02;
public class LongestOnes {
  public int longestOnes(int[] nums, int k) {
    int maxLength = 0;
    int left = 0;
    int countZeros = 0;
    for (int right = 0; right < nums.length; right++) {
      if (nums[right] == 0) {
        countZeros++;
      }
      while (countZeros > k) {
        if (nums[left] == 0) {
          countZeros--;
        }
        left++;
      }
      maxLength = Math.max(maxLength, right - left + 1);
    }
    return maxLength;
  }

  public static void main(String[] args) {
    LongestOnes sol = new LongestOnes();
    int[] nums = { 1, 1, 0, 0, 0, 1, 1, 1, 1, 1 };
    int k = 2;
    int ans = sol.longestOnes(nums, k);
    System.out.println(ans);
  }
}
