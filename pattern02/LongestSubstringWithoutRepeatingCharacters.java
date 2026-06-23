package pattern02;

import java.util.HashSet;
import java.util.Set;

public class LongestSubstringWithoutRepeatingCharacters {
  public int lengthOfLongestSubstring(String str) {
    int maxLen = 0;
    int left = 0;
    Set<Character> set = new HashSet<>();
    for (int right = 0; right < str.length(); right++) {
      // Expand
      char rigthChar = str.charAt(right);

      // Shrink
      while (set.contains(rigthChar)) {
        char leftChar = str.charAt(left);
        set.remove(leftChar);
        left++;
      }

      set.add(rigthChar);
      maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
  }

  public static void main(String[] args) {
    LongestSubstringWithoutRepeatingCharacters sol = new LongestSubstringWithoutRepeatingCharacters();

    System.out.println(sol.lengthOfLongestSubstring("abcabcbb")); // 3
    System.out.println(sol.lengthOfLongestSubstring("bbbbb")); // 1
    System.out.println(sol.lengthOfLongestSubstring("pwwkew")); // 3

    // Edge Cases
    System.out.println(sol.lengthOfLongestSubstring("")); // 0
    System.out.println(sol.lengthOfLongestSubstring("a")); // 1
    System.out.println(sol.lengthOfLongestSubstring("au")); // 2
    System.out.println(sol.lengthOfLongestSubstring("dvdf")); // 3
    System.out.println(sol.lengthOfLongestSubstring("abba")); // 2
    System.out.println(sol.lengthOfLongestSubstring("tmmzuxt")); // 5
  }
}
