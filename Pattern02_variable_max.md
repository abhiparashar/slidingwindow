# Pattern 2: Variable Window — Maximize Length
## Expand right always. Shrink left WHILE invalid. Record AFTER the while loop.

---

## When to Recognise This Pattern

| Signal in Problem | What It Means |
|---|---|
| "longest subarray/substring" | Variable max window |
| "maximum length" + some constraint | Variable max window |
| "at most k [bad things]" | Variable max — track bad count, shrink when > k |
| "no repeating characters" | Variable max — duplicate = invalid |
| "at most k distinct characters" | Variable max — distinct count > k = invalid |

---

## Which Variant to Use?

| What the problem tracks as "invalid" | Variant |
|---|---|
| A count of bad elements (zeros, odds, negatives) exceeds k | **Variant 1 — Bad Count** |
| Number of distinct values in window exceeds k | **Variant 2 — HashMap distinct count** |
| A duplicate character exists in the window | **Variant 3 — HashSet / LastIndex jump** |
| Replacements needed exceed k (most frequent char trick) | **Variant 4 — maxFreq trick (IF not WHILE)** |

---

## ─────────────────────────────────────────────
## Variant 1 — Bad Count
## ─────────────────────────────────────────────

**Use when**: the window becomes invalid when too many "bad" elements (zeros, odds, negatives, non-matching chars) accumulate — more than k of them.

**Signal words**: "at most k zeros", "flip at most k bits", "at most k replacements", "at most k odd numbers".

```java
/**
 * VARIANT 1 — BAD COUNT
 *
 * Define what makes an element "bad" for this problem.
 * Track count of bad elements in the current window.
 * Window is INVALID when badCount > k.
 *
 * ① EXPAND: include arr[right]. If bad, increment badCount.
 * ② SHRINK: while badCount > k, remove arr[left]. If bad, decrement badCount. left++.
 * ③ UPDATE: window [left..right] is now valid. Record right - left + 1.
 *
 * Why WHILE not IF?
 *   WHILE is always safe — keeps shrinking until valid.
 *   IF works only when adding one element can invalidate by at most 1.
 *   Use WHILE unless you have a specific reason for IF (see Variant 4).
 *
 * Amortised O(n): left moves forward at most n times total across the whole run.
 * Total work = O(n) right moves + O(n) left moves = O(2n) = O(n).
 */
public int variableMaxBadCount(int[] arr, int k) {
    int n = arr.length;
    if (arr == null || n == 0) return 0;

    int left     = 0;
    int maxLen   = 0;
    int badCount = 0;

    for (int right = 0; right < n; right++) {
        if (isBad(arr[right])) badCount++;           // ① EXPAND

        while (badCount > k) {                       // ② SHRINK while INVALID
            if (isBad(arr[left])) badCount--;
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1); // ③ UPDATE — window is valid
    }

    return maxLen;
}

private boolean isBad(int val) {
    return val == 0; // change per problem: == 0, % 2 == 1, < 0, != target, etc.
}
```

---

### P2.1 — Max Consecutive Ones III
**LeetCode 1004** | Google, Facebook, Amazon

#### Problem
Given binary array `nums` and integer `k`, return the maximum number of consecutive 1s if you can flip at most `k` zeros.

#### Translation
"Flip at most k zeros" = window may contain at most k zeros. Bad element = zero.

#### Solution
```java
class Solution {
    public int longestOnes(int[] nums, int k) {
        int left = 0, zeros = 0, maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) zeros++;               // ① EXPAND — count zeros

            while (zeros > k) {                          // ② SHRINK while invalid
                if (nums[left] == 0) zeros--;
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1); // ③ UPDATE
        }

        return maxLen;
    }
}
```

#### Dry Run
```
nums = [1,1,0,0,0,1,1,1,1,1],  k = 2

right=0: zeros=0, maxLen=1
right=1: zeros=0, maxLen=2
right=2: zeros=1, maxLen=3
right=3: zeros=2, maxLen=4
right=4: zeros=3 > 2 → SHRINK:
  left=0 (1, no change), left=1
  left=1 (1, no change), left=2
  left=2 (0, zeros=2),   left=3. OK.
  maxLen = max(4, 4-3+1=2) = 4
right=5..9: window grows to [3..9], zeros ≤ 2, maxLen = 7

Answer: 7  ✓  (flip zeros at index 3 and 4)
```

#### Edge Cases
```
k = 0          → no flips allowed → longest run of existing 1s
k >= all zeros → entire array → return nums.length
all zeros      → return k (flip k of them)
all ones       → return nums.length (no flips needed)
```

---

### P2.2 — Longest Subarray with Sum ≤ K (Positive Numbers Only)
**Classic** | Amazon, Google

#### Problem
Given an array of **positive** integers and value `K`, find the longest contiguous subarray with sum ≤ K.

#### Translation
Bad state = sum exceeds K. Shrink while sum > K.

#### Solution
```java
public int longestSubarraySum(int[] arr, int k) {
    if (arr == null || arr.length == 0) return 0;

    int left = 0, sum = 0, maxLen = 0;

    for (int right = 0; right < arr.length; right++) {
        sum += arr[right];                               // ① EXPAND

        while (sum > k) {                                // ② SHRINK while invalid
            sum -= arr[left];
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);     // ③ UPDATE
    }

    return maxLen;
}
```

#### Dry Run
```
arr = [1,2,3,4,5],  k = 9

right=0: sum=1, maxLen=1
right=1: sum=3, maxLen=2
right=2: sum=6, maxLen=3
right=3: sum=10 > 9 → SHRINK: sum=10-1=9, left=1. OK. maxLen=3
right=4: sum=9+5=14 > 9 → SHRINK: 14-2=12>9 → 12-3=9, left=3. maxLen=max(3,2)=3

Answer: 3  ✓  ([1,2,3] or [2,3,4])
```

#### Critical Warning ⚠️
```java
// ONLY works when all elements are POSITIVE.
// With negatives: removing arr[left] might INCREASE the sum (if arr[left] < 0).
// Shrinking then doesn't fix the window — wrong answer or infinite loop.
// For negatives → prefix sum + monotonic deque (LC 862).
// ALWAYS ask the interviewer: "Can elements be negative?"
```

---

### P2.3 — Longest Subarray of 1s After Deleting One Element
**LeetCode 1493** | Google, Amazon

#### Problem
Given binary array `nums`, delete exactly one element. Return the length of the longest subarray of 1s remaining.

#### Translation
After deleting one element, window can have at most 1 zero. Answer = window size − 1 (the deleted element).

#### Solution
```java
class Solution {
    public int longestSubarray(int[] nums) {
        int left = 0, zeros = 0, maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) zeros++;               // ① EXPAND

            while (zeros > 1) {                          // ② SHRINK — at most 1 zero
                if (nums[left] == 0) zeros--;
                left++;
            }

            maxLen = Math.max(maxLen, right - left);     // ③ UPDATE — note: no +1!
        }

        return maxLen;
    }
}
```

#### The Subtle Difference from LC 1004 ⚠️
```java
// LC 1004: flip at most k zeros → answer = right - left + 1  (full window)
// LC 1493: delete exactly 1 element → answer = right - left + 1 - 1 = right - left
// One element must always be deleted so the window size shrinks by 1.
```

---

## ─────────────────────────────────────────────
## Variant 2 — HashMap Distinct Count
## ─────────────────────────────────────────────

**Use when**: the window becomes invalid when it contains more than k **distinct values**. You need to track frequency of each value to know when to evict it.

**Signal words**: "at most k distinct characters/integers", "at most 2 types", "k different values".

```java
/**
 * VARIANT 2 — HASHMAP DISTINCT COUNT
 *
 * freq map tracks count of each element in the current window.
 * freq.size() = number of distinct elements in window.
 * Window is INVALID when freq.size() > k.
 *
 * ① EXPAND: add arr[right] to freq map.
 * ② SHRINK: while freq.size() > k:
 *     decrement freq[arr[left]].
 *     if freq[arr[left]] == 0 → remove from map (distinct count drops by 1).
 *     left++.
 * ③ UPDATE: record right - left + 1.
 *
 * Why remove from map when count hits 0?
 *   freq.size() counts map entries. If we leave zero-count entries in,
 *   size() won't decrease even though that element is no longer in the window.
 *   Always remove the entry when its count drops to 0.
 */
public int variableMaxDistinct(int[] arr, int k) {
    if (arr == null || arr.length == 0 || k == 0) return 0;

    Map<Integer, Integer> freq = new HashMap<>();
    int left = 0, maxLen = 0;

    for (int right = 0; right < arr.length; right++) {
        freq.merge(arr[right], 1, Integer::sum);             // ① EXPAND

        while (freq.size() > k) {                            // ② SHRINK while > k distinct
            freq.merge(arr[left], -1, Integer::sum);
            if (freq.get(arr[left]) == 0) freq.remove(arr[left]);
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);         // ③ UPDATE
    }

    return maxLen;
}
```

---

### P2.4 — Longest Substring with At Most K Distinct Characters
**LeetCode 340** | Google, Amazon, Meta

#### Problem
Given string `s` and integer `k`, return the length of the longest substring with at most `k` distinct characters.

#### Solution
```java
class Solution {
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (k == 0 || s == null || s.isEmpty()) return 0;

        Map<Character, Integer> freq = new HashMap<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            freq.merge(s.charAt(right), 1, Integer::sum);         // ① EXPAND

            while (freq.size() > k) {                             // ② SHRINK
                char lc = s.charAt(left);
                freq.merge(lc, -1, Integer::sum);
                if (freq.get(lc) == 0) freq.remove(lc);
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);          // ③ UPDATE
        }

        return maxLen;
    }
}
```

#### Dry Run
```
s = "eceba",  k = 2

right=0(e): freq={e:1}, size=1, maxLen=1
right=1(c): freq={e:1,c:1}, size=2, maxLen=2
right=2(e): freq={e:2,c:1}, size=2, maxLen=3
right=3(b): freq={e:2,c:1,b:1}, size=3 > 2 → SHRINK:
  remove s[0]=e: freq={e:1,c:1,b:1}, left=1, size=3 → still >2
  remove s[1]=c: freq={e:1,b:1}, left=2, size=2 → OK. maxLen=max(3,2)=3
right=4(a): freq={e:1,b:1,a:1}, size=3 > 2 → SHRINK:
  remove s[2]=e: freq={b:1,a:1}, left=3, size=2 → OK. maxLen=max(3,2)=3

Answer: 3  ✓  ("ece")
```

#### Optimisation — `if` instead of `while`
```java
// Each iteration adds exactly ONE new char → freq.size() can exceed k by at most 1.
// So one shrink step is always enough → replace while with if.
if (freq.size() > k) {
    char lc = s.charAt(left);
    freq.merge(lc, -1, Integer::sum);
    if (freq.get(lc) == 0) freq.remove(lc);
    left++;
}
// Same O(n), tighter constant. Good to mention in interviews.
```

---

### P2.5 — Fruit Into Baskets
**LeetCode 904** | Google (frequently asked)

#### Problem
Two baskets, each holding one fruit type. Given `fruits[]` (each element = type), find the longest subarray with at most 2 distinct fruit types.

#### Translation
At most 2 distinct values = Variant 2 with k = 2. Identical to LC 340 with k fixed at 2.

#### Solution
```java
class Solution {
    public int totalFruit(int[] fruits) {
        Map<Integer, Integer> basket = new HashMap<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < fruits.length; right++) {
            basket.merge(fruits[right], 1, Integer::sum);         // ① EXPAND

            while (basket.size() > 2) {                           // ② SHRINK
                int lf = fruits[left];
                basket.merge(lf, -1, Integer::sum);
                if (basket.get(lf) == 0) basket.remove(lf);
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);          // ③ UPDATE
        }

        return maxLen;
    }
}
```

#### The Generalisation
```java
// LC 904 is LC 340 with k = 2.
// Swap   while (basket.size() > 2)
// for    while (basket.size() > k)
// and it becomes LC 340 exactly. Don't memorise two solutions.
```

---

## ─────────────────────────────────────────────
## Variant 3 — HashSet / LastIndex Jump
## ─────────────────────────────────────────────

**Use when**: the window becomes invalid when any **single element repeats** inside it. You need either O(1) membership check (Set) or O(1) left pointer jump (HashMap of last seen index).

**Signal words**: "no repeating characters", "all unique", "distinct elements only", "no duplicates in window".

```java
/**
 * VARIANT 3a — HASHSET (intuitive, easy to explain)
 *
 * Set tracks which elements are currently in the window.
 * Window is INVALID when the incoming element is already in the Set.
 *
 * ① EXPAND: while Set contains arr[right] → remove arr[left], left++. (SHRINK inside)
 *    Then add arr[right] to Set.
 * ③ UPDATE: record right - left + 1.
 *
 * Note: shrink is inside the while, before adding — ensures no duplicates on add.
 */
public int variableMaxSet(String s) {
    Set<Character> window = new HashSet<>();
    int left = 0, maxLen = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);

        while (window.contains(c)) {                     // ② SHRINK — evict until no dup
            window.remove(s.charAt(left));
            left++;
        }

        window.add(c);                                   // ① EXPAND
        maxLen = Math.max(maxLen, right - left + 1);     // ③ UPDATE
    }

    return maxLen;
}

/**
 * VARIANT 3b — LAST INDEX JUMP (optimal — no inner loop execution)
 *
 * HashMap stores the last seen index of each character.
 * When a duplicate is found inside the window, jump left directly past it.
 * No inner loop — O(n) with zero redundant iterations.
 *
 * CRITICAL: only jump if lastSeen.get(c) >= left.
 *   If the last occurrence is BEFORE the current window, don't move left backward.
 */
public int variableMaxJump(String s) {
    Map<Character, Integer> lastSeen = new HashMap<>();
    int left = 0, maxLen = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);

        // If c was seen inside the current window → jump left past it
        if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
            left = lastSeen.get(c) + 1;   // jump — never go backward
        }

        lastSeen.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}
```

---

### P2.6 — Longest Substring Without Repeating Characters
**LeetCode 3** | ⭐ Most Asked | Google, Amazon, Facebook, Microsoft

#### Problem
Find the length of the longest substring with no repeating characters.

#### Solution A — HashSet
```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.isEmpty()) return 0;

        Set<Character> window = new HashSet<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            while (window.contains(c)) {                 // ② SHRINK: evict until no dup
                window.remove(s.charAt(left));
                left++;
            }

            window.add(c);                                       // ① EXPAND
            maxLen = Math.max(maxLen, right - left + 1);        // ③ UPDATE
        }

        return maxLen;
    }
}
```

#### Solution B — LastIndex Jump (optimal)
```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.isEmpty()) return 0;

        Map<Character, Integer> lastSeen = new HashMap<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
                left = lastSeen.get(c) + 1;  // jump past the duplicate
            }

            lastSeen.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}
```

#### The Never-Go-Backward Trap ⚠️
```java
// s = "abba"
// right=3 ('a'), lastSeen.get('a')=0, but left is already at 2.
// WITHOUT >= left check: left = 0+1 = 1 → moves BACKWARD → wrong answer!
// WITH the check: 0 >= 2? No → don't move left → correct.
// RULE: always guard the jump with lastSeen.get(c) >= left.
```

#### Dry Run (Solution B)
```
s = "pwwkew"

right=0(p): lastSeen={p:0}, left=0, len=1
right=1(w): lastSeen={p:0,w:1}, left=0, len=2
right=2(w): w seen at 1 >= left=0 → left=2. lastSeen={p:0,w:2}, len=1
right=3(k): lastSeen={...,k:3}, left=2, len=2
right=4(e): lastSeen={...,e:4}, left=2, len=3
right=5(w): w seen at 2 >= left=2 → left=3. lastSeen={...,w:5}, len=3

Answer: 3  ✓  ("wke")
```

#### Edge Cases
```
s = ""      → 0
s = "a"     → 1
s = "aaaa"  → 1  (left keeps jumping to right)
s = "abcd"  → 4  (no repeats, window = entire string)
```

---

## ─────────────────────────────────────────────
## Variant 4 — maxFreq Trick (IF not WHILE)
## ─────────────────────────────────────────────

**Use when**: replacements needed = `windowSize - maxFreq(window)` and this must be ≤ k. The window should only grow, never shrink below the current best — so use `if` not `while`.

**Signal words**: "replace at most k characters", "longest substring with same letter after k replacements".

```java
/**
 * VARIANT 4 — maxFreq TRICK
 *
 * In a window of size len, the most frequent char appears maxFreq times.
 * Replacements needed = len - maxFreq. Must be <= k.
 * Valid condition: (right - left + 1) - maxFreq <= k.
 *
 * Why IF not WHILE?
 *   We want MAXIMUM window. Once a window of size X is found,
 *   we never want a smaller result. The window SLIDES (maintains size)
 *   when invalid, rather than shrinking below the current best.
 *   IF slides the window by one step. WHILE would actually shrink it.
 *
 * Why not recompute maxFreq after left moves?
 *   maxFreq may be stale (dominant char left the window), but that's fine.
 *   A stale (inflated) maxFreq just means we don't slide when we could.
 *   We only record a better result when a genuinely higher maxFreq appears.
 *   Recomputing would cost O(26) per step → O(26n) total. Not needed.
 */
public int variableMaxFreq(String s, int k) {
    int[] freq  = new int[26];
    int left    = 0;
    int maxFreq = 0;
    int maxLen  = 0;

    for (int right = 0; right < s.length(); right++) {
        int c = s.charAt(right) - 'A';
        freq[c]++;
        maxFreq = Math.max(maxFreq, freq[c]);             // ① EXPAND + track maxFreq

        if ((right - left + 1) - maxFreq > k) {           // ② SLIDE (IF not WHILE)
            freq[s.charAt(left) - 'A']--;
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);       // ③ UPDATE
    }

    return maxLen;
}
```

---

### P2.7 — Longest Repeating Character Replacement
**LeetCode 424** | Google, Amazon, Bloomberg

#### Problem
Given string `s` (uppercase letters) and integer `k`, replace at most `k` characters to produce the longest substring containing only one repeated letter. Return that length.

#### Solution
```java
class Solution {
    public int characterReplacement(String s, int k) {
        int[] freq  = new int[26];
        int left    = 0;
        int maxFreq = 0;
        int maxLen  = 0;

        for (int right = 0; right < s.length(); right++) {
            int c = s.charAt(right) - 'A';
            freq[c]++;
            maxFreq = Math.max(maxFreq, freq[c]);          // ① EXPAND

            if ((right - left + 1) - maxFreq > k) {        // ② SLIDE — IF not WHILE
                freq[s.charAt(left) - 'A']--;
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);   // ③ UPDATE
        }

        return maxLen;
    }
}
```

#### Dry Run
```
s = "AABABBA",  k = 1

right=0(A): freq[A]=1, maxFreq=1, size=1, 1-1=0 ≤ 1. maxLen=1
right=1(A): freq[A]=2, maxFreq=2, size=2, 2-2=0 ≤ 1. maxLen=2
right=2(B): freq[B]=1, maxFreq=2, size=3, 3-2=1 ≤ 1. maxLen=3
right=3(A): freq[A]=3, maxFreq=3, size=4, 4-3=1 ≤ 1. maxLen=4
right=4(B): freq[B]=2, maxFreq=3, size=5, 5-3=2 > 1 → SLIDE: freq[A]--, left=1. size=4
right=5(B): freq[B]=3, maxFreq=3, size=5, 5-3=2 > 1 → SLIDE: freq[A]--, left=2. size=4
right=6(A): freq[A]=2, maxFreq=3, size=5, 5-3=2 > 1 → SLIDE: freq[B]--, left=3. size=4

Answer: 4  ✓
```

#### Edge Cases
```
k = 0       → longest run of any single repeated char
k >= n      → entire string (replace everything to match most frequent)
all same    → return n
```

---

## Pattern 2 — Summary

```
FOUR VARIANTS — PICK BY WHAT MAKES THE WINDOW INVALID:

  Variant 1 — Bad Count
    Invalid when : count of bad elements > k
    State        : int badCount
    Shrink       : if (isBad(arr[left])) badCount--; left++
    Use for      : LC 1004, longest sum ≤ K, LC 1493

  Variant 2 — HashMap Distinct Count
    Invalid when : freq.size() > k  (too many distinct values)
    State        : Map<T, Integer> freq
    Shrink       : decrement freq[left]; remove if 0; left++
    Use for      : LC 340, LC 904

  Variant 3 — HashSet / LastIndex Jump
    Invalid when : duplicate exists in window
    State        : Set<Char> OR Map<Char, lastIndex>
    Shrink       : Set → remove until no dup; Map → jump left (guard >= left)
    Use for      : LC 3

  Variant 4 — maxFreq Trick
    Invalid when : (windowSize - maxFreq) > k
    State        : int[] freq, int maxFreq
    Shrink       : IF (not WHILE) — window SLIDES, never shrinks
    Use for      : LC 424

UNIVERSAL RULE:
  Update result AFTER the while/if — window is valid at that point.

AMORTISED O(n) PROOF (say this in interviews):
  left moves forward at most n times total.
  right moves forward exactly n times.
  Total = O(2n) = O(n).

TRAPS:
  • Variant 2: always remove map entry when count hits 0 — else size() lies
  • Variant 3 jump: guard with lastSeen.get(c) >= left — never go backward
  • Variant 4: use IF not WHILE — window slides, doesn't shrink
  • Negative numbers break sum-based shrinking — ask the interviewer
```

---

*Next → Pattern 3: Variable Window — Minimize Length*