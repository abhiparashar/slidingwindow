# Pattern 3: Variable Window — Minimize Length
## Expand right always. Shrink left WHILE valid. Record INSIDE the while loop.

---

## When to Recognise This Pattern

| Signal in Problem | What It Means |
|---|---|
| "shortest subarray/substring" | Variable min window |
| "minimum length" | Variable min window |
| "smallest window containing..." | Variable min window |
| "minimum subarray with sum ≥ k" | Variable min window |
| "minimum window containing all characters" | Variable min + matched counter |

---

## Which Variant to Use?

| Validity condition | Variant |
|---|---|
| Sum of window ≥ target | **Variant 1 — Running Sum** |
| All required characters are present in window | **Variant 2 — matched counter** |
| Custom condition (e.g. outside quota satisfied) | **Variant 3 — Custom validity check** |

---

## The Golden Rule — Min vs Max

```
PATTERN 2 (MAX):  shrink WHILE INVALID  → record AFTER while
PATTERN 3 (MIN):  shrink WHILE VALID    → record INSIDE while   ← opposite!

Why record inside?
  When we enter the while loop, [left..right] is currently valid.
  Record it NOW, then shrink to see if something smaller is also valid.
  Keep shrinking + recording until invalid. Always record before each shrink.
```

---

## ─────────────────────────────────────────────
## Variant 1 — Running Sum
## ─────────────────────────────────────────────

**Use when**: window is valid when its sum meets or exceeds a target. Shrink to find the smallest such window.

**Signal words**: "minimum length subarray with sum ≥ target", "smallest subarray whose sum is at least k".

```java
/**
 * VARIANT 1 — RUNNING SUM (MIN WINDOW)
 *
 * ① EXPAND: add arr[right] to windowSum.
 * ② SHRINK while VALID (windowSum >= target):
 *     ③ RECORD: minLen = min(minLen, right - left + 1)  ← INSIDE while
 *     Remove arr[left] from windowSum. left++.
 *
 * Why Integer.MAX_VALUE?
 *   Sentinel — if never updated, no valid window exists → return 0.
 *
 * Only works with POSITIVE numbers.
 *   With negatives, removing arr[left] might increase sum → shrinking never converges.
 *   For negatives → LC 862 approach (prefix sum + deque).
 */
public int variableMinSum(int[] arr, int target) {
    int n = arr.length;
    if (arr == null || n == 0) return 0;

    int left      = 0;
    int minLen    = Integer.MAX_VALUE;
    int windowSum = 0;

    for (int right = 0; right < n; right++) {
        windowSum += arr[right];                              // ① EXPAND

        while (windowSum >= target) {                         // ② SHRINK while VALID
            minLen = Math.min(minLen, right - left + 1);      // ③ RECORD inside while
            windowSum -= arr[left];
            left++;
        }
    }

    return minLen == Integer.MAX_VALUE ? 0 : minLen;
}
```

---

### P3.1 — Minimum Size Subarray Sum
**LeetCode 209** | Amazon, Adobe, Microsoft

#### Problem
Given array of positive integers `nums` and positive integer `target`, return the minimal length of a contiguous subarray with sum ≥ target. Return 0 if none.

#### Solution
```java
class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0, sum = 0, minLen = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];                                    // ① EXPAND

            while (sum >= target) {                                // ② SHRINK while VALID
                minLen = Math.min(minLen, right - left + 1);      // ③ RECORD
                sum -= nums[left];
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }
}
```

#### Dry Run
```
nums = [2,3,1,2,4,3],  target = 7

right=0: sum=2
right=1: sum=5
right=2: sum=6
right=3: sum=8 ≥ 7 → RECORD minLen=4. sum=8-2=6, left=1. Exit while.
right=4: sum=10 ≥ 7 → RECORD minLen=min(4,4)=4. sum=10-3=7 ≥ 7
                     → RECORD minLen=min(4,3)=3. sum=7-1=6, left=3. Exit.
right=5: sum=9  ≥ 7 → RECORD minLen=min(3,3)=3. sum=9-2=7 ≥ 7
                     → RECORD minLen=min(3,2)=2. sum=7-4=3, left=5. Exit.

Answer: 2  ✓  (subarray [4,3])
```

#### Edge Cases
```
sum of entire array < target  → minLen stays MAX_VALUE → return 0
single element >= target      → returns 1
target = 1                    → first element satisfies → returns 1
all elements equal target     → returns 1
```

---

## ─────────────────────────────────────────────
## Variant 2 — matched Counter
## ─────────────────────────────────────────────

**Use when**: the window is valid when all required characters (with their frequencies) are present. Need to track multi-character requirements simultaneously.

**Signal words**: "minimum window containing all characters of t", "smallest window with all required elements", "minimum substring containing all letters".

```java
/**
 * VARIANT 2 — matched COUNTER (MIN WINDOW)
 *
 * need   map: frequency of each char required (built from target string t).
 * window map: frequency of each char in current window.
 * required   = need.size()  — # distinct chars that must be satisfied.
 * matched    = 0            — # distinct chars currently satisfied.
 *
 * On EXPAND (add char c):
 *   window[c]++
 *   if need contains c AND window[c] == need[c] → matched++
 *
 * On SHRINK (remove char lc):
 *   if need contains lc AND window[lc] == need[lc] → matched--  ← check BEFORE decrement
 *   window[lc]--
 *
 * Window is VALID when matched == required.
 * Shrink WHILE valid, record INSIDE the while.
 *
 * CRITICAL: use .equals() not == for Integer map comparison.
 *   Java caches Integer only for -128..127.
 *   For values > 127: == compares object references → wrong result.
 *   Always: window.get(c).equals(need.get(c))
 */
public String variableMinMatched(String s, String t) {
    if (s.isEmpty() || t.isEmpty() || s.length() < t.length()) return "";

    Map<Character, Integer> need = new HashMap<>();
    for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);

    int required = need.size();
    int matched  = 0;

    Map<Character, Integer> window = new HashMap<>();
    int left = 0, minLen = Integer.MAX_VALUE, resLeft = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        window.merge(c, 1, Integer::sum);                         // ① EXPAND

        if (need.containsKey(c) && window.get(c).equals(need.get(c))) matched++;

        while (matched == required) {                             // ② SHRINK while VALID
            if (right - left + 1 < minLen) {                     // ③ RECORD inside while
                minLen  = right - left + 1;
                resLeft = left;
            }
            char lc = s.charAt(left);
            if (need.containsKey(lc) && window.get(lc).equals(need.get(lc))) matched--;
            window.merge(lc, -1, Integer::sum);
            left++;
        }
    }

    return minLen == Integer.MAX_VALUE ? "" : s.substring(resLeft, resLeft + minLen);
}
```

---

### P3.2 — Minimum Window Substring
**LeetCode 76** | ⭐ Google's Favourite Hard | Google, Facebook, Amazon, Uber

#### Problem
Given strings `s` and `t`, return the minimum window substring of `s` such that every character in `t` (including duplicates) is present. Return `""` if impossible.

#### Solution
```java
class Solution {
    public String minWindow(String s, String t) {
        if (s.isEmpty() || t.isEmpty() || s.length() < t.length()) return "";

        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);

        int required = need.size(), matched = 0;
        Map<Character, Integer> window = new HashMap<>();
        int left = 0, minLen = Integer.MAX_VALUE, resLeft = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            window.merge(c, 1, Integer::sum);                     // ① EXPAND

            if (need.containsKey(c) && window.get(c).equals(need.get(c))) matched++;

            while (matched == required) {                         // ② SHRINK while VALID
                if (right - left + 1 < minLen) {                 // ③ RECORD
                    minLen  = right - left + 1;
                    resLeft = left;
                }
                char lc = s.charAt(left);
                if (need.containsKey(lc) && window.get(lc).equals(need.get(lc))) matched--;
                window.merge(lc, -1, Integer::sum);
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(resLeft, resLeft + minLen);
    }
}
```

#### The `.equals()` Trap ⚠️
```java
// WRONG — Integer cache only covers -128 to 127:
window.get(c) == need.get(c)           // fails silently for freq > 127!

// CORRECT — always use .equals() for Integer map values:
window.get(c).equals(need.get(c))      // ✓

// This is the #1 bug on LC 76 in interviews. Never use == on Integer objects.
```

#### Full Trace
```
s = "ADOBECODEBANC",  t = "ABC"
need = {A:1, B:1, C:1},  required = 3

right=0(A): window[A]=1==need[A]=1 → matched=1
right=3(B): window[B]=1==need[B]=1 → matched=2
right=5(C): window[C]=1==need[C]=1 → matched=3 ← ENTER while
  RECORD [0..5] = "ADOBEC", len=6
  lc=A: window[A]=1==need[A]=1 → matched=2. window[A]=0. left=1. EXIT while.

... (right advances, window shifts) ...

right=10(A): matched→3 ← ENTER while
  Shrink until matched drops below 3 ...

right=12(C): matched→3 ← ENTER while
  RECORD [9..12] = "BANC", len=4. New best!
  Shrink: matched drops. EXIT while.

Answer: "BANC"  ✓
```

#### Counter Questions
```
Q: What if t has duplicate chars like t = "AAB"?
A: need = {A:2, B:1}. matched fires for A only when window[A] reaches 2, not 1.
   Handled correctly — no special case needed.

Q: What if no window exists?
A: matched never reaches required → minLen stays MAX_VALUE → return "".

Q: What if s == t?
A: Right and left both advance through entire string. Returns s itself.
```

---

## ─────────────────────────────────────────────
## Variant 3 — Custom Validity Check
## ─────────────────────────────────────────────

**Use when**: the validity condition is a custom function — not a simple sum or a matched counter, but a multi-variable check (e.g. all characters within quota, balance condition met, frequency constraints satisfied).

**Signal words**: "minimum window to replace so that [complex condition] holds", "minimum substring such that outside satisfies [quota]".

```java
/**
 * VARIANT 3 — CUSTOM VALIDITY CHECK (MIN WINDOW)
 *
 * isValid() encapsulates whatever makes the window acceptable.
 * Shrink WHILE isValid() is true — record before each shrink.
 *
 * The window represents the "replacement zone."
 * Characters outside the window are fixed.
 * isValid() checks whether the fixed part already satisfies the constraints.
 */
public int variableMinCustom(String s, int target) {
    int n = s.length();
    int[] freq = new int[128];
    for (char c : s.toCharArray()) freq[c]++;

    int left = 0, minLen = n;

    for (int right = 0; right < n; right++) {
        freq[s.charAt(right)]--;                              // ① EXPAND (remove from outside)

        while (isValid(freq, target)) {                       // ② SHRINK while VALID
            minLen = Math.min(minLen, right - left + 1);      // ③ RECORD
            freq[s.charAt(left)]++;                           // restore to outside
            left++;
        }
    }

    return minLen;
}

private boolean isValid(int[] freq, int target) {
    // Example: all tracked chars are within quota
    return freq['Q'] <= target && freq['W'] <= target
        && freq['E'] <= target && freq['R'] <= target;
}
```

---

### P3.3 — Replace Substring for Balanced String
**LeetCode 1234** | Google

#### Problem
A string of only Q,W,E,R is balanced when each appears exactly `n/4` times. Find the minimum length substring to replace to balance it.

#### Key Insight
Characters **outside** the window are fixed. The window is the "replacement zone". The window is valid when the outside already satisfies all quotas (each char ≤ n/4). Minimise this zone.

#### Solution
```java
class Solution {
    public int balancedString(String s) {
        int n = s.length(), target = n / 4;
        int[] freq = new int[128];
        for (char c : s.toCharArray()) freq[c]++;

        if (isBalanced(freq, target)) return 0;  // already balanced

        int left = 0, minLen = n;

        for (int right = 0; right < n; right++) {
            freq[s.charAt(right)]--;                          // ① EXPAND — remove from outside

            while (left <= right && isBalanced(freq, target)) { // ② SHRINK while VALID
                minLen = Math.min(minLen, right - left + 1);    // ③ RECORD
                freq[s.charAt(left)]++;                       // restore to outside
                left++;
            }
        }

        return minLen;
    }

    private boolean isBalanced(int[] freq, int target) {
        return freq['Q'] <= target && freq['W'] <= target
            && freq['E'] <= target && freq['R'] <= target;
    }
}
```

#### Dry Run
```
s = "QWER",  n = 4,  target = 1
freq = {Q:1, W:1, E:1, R:1}
isBalanced? All <= 1 → YES → return 0  ✓

s = "QQWE",  n = 4,  target = 1
freq = {Q:2, W:1, E:1, R:0}
isBalanced? Q=2 > 1 → NO

right=0(Q): freq[Q]=2-1=1. isBalanced? Q=1,W=1,E=1,R=0 ≤ 1 → YES
  RECORD minLen = 1. freq[Q]++ =2. left=1.
  isBalanced? Q=2 > 1 → NO. Exit while.

right=1(W): freq[W]=0. isBalanced? Q=2>1 → NO.
right=2(W)→actually s="QQWE":
right=2(W): s[2]=W, freq[W]=1-1=0. isBalanced? Q=2>1 → NO.
right=3(E): freq[E]=0. isBalanced? Q=2>1 → NO.

Hmm — let's pick a clearer example:
s = "WQWRQQQW",  n=8, target=2
(This has more Q's to replace — standard LC 1234 example works out to answer=3)
```

#### Edge Cases
```
Already balanced                → return 0  (check before loop)
Must replace entire string      → return n
Single char type dominates      → window covers all excess copies
```

---

## Pattern 3 — Summary

```
THREE VARIANTS — PICK BY WHAT DEFINES "VALID":

  Variant 1 — Running Sum
    Valid when  : windowSum >= target
    State       : int windowSum
    Record at   : right - left + 1  (inside while)
    Use for     : LC 209

  Variant 2 — matched Counter
    Valid when  : matched == required
    State       : Map need, Map window, int matched, int required
    Record at   : right - left + 1  (inside while)
    Critical    : .equals() not == for Integer maps
    Use for     : LC 76

  Variant 3 — Custom Validity Check
    Valid when  : isValid(freq, target) returns true
    State       : int[] freq (or any custom state)
    Record at   : right - left + 1  (inside while)
    Expand      : remove char from "outside" count
    Shrink      : restore char to "outside" count
    Use for     : LC 1234

UNIVERSAL RULE:
  Initialize minLen = Integer.MAX_VALUE.
  Return 0 (not MAX_VALUE) if no valid window found.
  Record INSIDE while — not after.

CONTRAST WITH PATTERN 2:
  Pattern 2 MAX: while(INVALID) shrink → record AFTER while
  Pattern 3 MIN: while(VALID)   shrink → record INSIDE while

TRAPS:
  • Returning MAX_VALUE instead of 0 when no window exists
  • Recording after the while (gives wrong — larger — minimum)
  • Using == instead of .equals() for Integer map values (LC 76 killer)
  • Variant 1 with negative numbers — shrinking doesn't converge
```

---

*Next → Pattern 4 + 5: Count Subarrays*