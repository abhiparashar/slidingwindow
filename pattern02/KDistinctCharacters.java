package pattern02;

import java.util.HashMap;
import java.util.Map;

public class KDistinctCharacters {
  public int lengthOfLongestSubstringKDistinct(String str, int k) {
    int maxlen = 0;
    int left = 0;
    Map<Character, Integer> map = new HashMap<>();
    for (int right = 0; right < str.length(); right++) {
      // Expand
      char ch = str.charAt(right);
      map.put(ch, map.getOrDefault(ch, 0) + 1);

      // Shrink
      while (map.size() > k) {
        char leftChar = str.charAt(left);
        map.put(ch, map.get(leftChar) - 1);
        if (map.get(leftChar) == 0) {
          map.remove(leftChar);
        }
        left++;
      }
      maxlen = Math.max(maxlen, right - left + 1);
    }
    return maxlen;
  }

  public static void main(String[] args) {
    KDistinctCharacters sol = new KDistinctCharacters();
    String s = "eceba";
    int k = 2;
    int ans = sol.lengthOfLongestSubstringKDistinct(s, k);
    System.out.println(ans);
  }
}
