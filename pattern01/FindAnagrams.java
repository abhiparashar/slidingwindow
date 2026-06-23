package pattern01;

import java.util.ArrayList;
import java.util.List;

public class FindAnagrams {
  public List<Integer> findAnagrams(String str1, String str2) {
    List<Integer> result = new ArrayList<>();
    int n = str1.length(), k = str2.length();
    if (k > n)
      return result;
    int[] need = new int[26];
    int[] window = new int[26];
    for (char ch : str2.toCharArray()) {
      need[ch - 'a']++;
    }
    int required = 0;
    for (int f : need) {
      required++;
    }
    int matched = 0;
    for (int right = 0; right < n; right++) {
      int in = str1.charAt(right) - 'a';
      window[in]++;
      if (window[in] == need[in])
        matched++;

      if (right >= k) {
        int out = str1.charAt(right - k) - 'a';
        if (window[out] == need[out])
          matched--;
        window[out]--;
      }

      if (right >= k - 1 && matched == required) {
        result.add(right - k + 1);
      }
    }

    return result;
  }

  public static void main(String[] args) {
    FindAnagrams sol = new FindAnagrams();
    String str1 = "cbaebabacd";
    String str2 = "abc";
    List<Integer> list = sol.findAnagrams(str1, str2);
    for (Integer num : list) {
      System.out.println(num);
    }
  }
}
