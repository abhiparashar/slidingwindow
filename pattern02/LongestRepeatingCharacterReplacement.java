package pattern02;

import java.util.HashMap;
import java.util.Map;

public class LongestRepeatingCharacterReplacement {
  public int characterReplacement(String str, int k) {
    int left = 0;
    int maxLen = 0;
    Map<Character, Integer> map = new HashMap<>();
    int maxFreq = 0;
    for (int right = 0; right < str.length(); right++) {
      // Expand
      char rightCh = str.charAt(right);
      map.put(rightCh, map.getOrDefault(rightCh, 0) + 1);
      maxFreq = Math.max(maxFreq, map.get(rightCh));

      // Shrink
      while ((right - left + 1) - maxFreq > k) {
        char leftCh = str.charAt(left);
        map.put(leftCh, map.get(leftCh) - 1);
        if (map.get(leftCh) == 0) {
          map.remove(leftCh);
        }
        left++;
      }

      maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
  }

  public static void main(String[] args) {
    LongestRepeatingCharacterReplacement sol = new LongestRepeatingCharacterReplacement();

    System.out.println(sol.characterReplacement("ABAB", 2)); // 4
    System.out.println(sol.characterReplacement("AABABBA", 1)); // 4

    // Edge Cases
    System.out.println(sol.characterReplacement("A", 0)); // 1
    System.out.println(sol.characterReplacement("AAAA", 2)); // 4
    System.out.println(sol.characterReplacement("ABCDE", 1)); // 2
    System.out.println(sol.characterReplacement("BAAA", 0)); // 3
    System.out.println(sol.characterReplacement("BAAAB", 2)); // 5
    System.out.println(sol.characterReplacement("ABBB", 2)); // 4
  }
}
