# Pattern 1: Fixed Window
## Window size `k` is given. Every window is valid. Slide one step at a time.

---

## When to Recognise This Pattern

| Signal in Problem | What It Means |
|---|---|
| "subarray/substring of size k" | Fixed window |
| "k consecutive elements" | Fixed window |
| "every window of size k" | Fixed window |
| Window size never grows or shrinks | Fixed window |

---

## Which Variant to Use?

| What the problem asks you to track inside the window | Variant |
|---|---|
| A sum, average, product — any single number | **Variant 1 — Running Sum** |
| Whether window matches a char pattern (anagram / permutation) | **Variant 2 — Freq Array + matched counter** |
| Whether a duplicate exists within distance k | **Variant 3a — HashSet** |
| The first/last element satisfying a condition in every window | **Variant 3b — Deque of indices** |

---

## ─────────────────────────────────────────────
## Variant 1 — Running Sum
## ─────────────────────────────────────────────

**Use when**: the window state is a single aggregated number — sum, product, count, average.

**Signal words**: "maximum/minimum sum", "average", "product", "count of elements" in a window of size k.

```java
/**
 * VARIANT 1 — RUNNING SUM
 *
 * Phase 1: build first window [0 .. k-1] from scratch.
 * Phase 2: slide — add arr[right] (coming IN), subtract arr[right-k] (going OUT).
 * Update result on EVERY iteration — every window of size k is valid.
 *
 * Element coming IN  → arr[right]
 * Element going OUT  → arr[right - k]
 *
 * Adapt:
 *   max sum     → Math.max(result, windowSum)
 *   min sum     → Math.min(result, windowSum)
 *   average     → return (double) result / k  at the end
 *   product     → windowProduct *= arr[right]; windowProduct /= arr[right-k]
 *   count check → if (windowSum >= threshold) count++
 */
public int fixedWindowSum(int[] arr, int k) {
    int n = arr.length;
    if (arr == null || n == 0) return 0;
    if (k <= 0 || k > n)      return 0;

    // Phase 1: build first window [0 .. k-1]
    int windowSum = 0;
    for (int i = 0; i < k; i++) windowSum += arr[i];
    int result = windowSum;

    // Phase 2: slide one step at a time
    for (int right = k; right < n; right++) {
        windowSum += arr[right];      // ① ADD   — element entering from the right
        windowSum -= arr[right - k]; // ② REMOVE — element leaving from the left
        result = Math.max(result, windowSum); // ③ UPDATE — every window is valid
    }

    return result;
}
```

---

### P1.1 — Maximum Average Subarray I
**LeetCode 643** | Google, Amazon, Bloomberg

#### Problem
Given integer array `nums` and integer `k`, find the contiguous subarray of length `k` with the maximum average. Return the average.

#### Solution
```java
class Solution {
    public double findMaxAverage(int[] nums, int k) {
        if (nums == null || nums.length < k) return 0.0;

        long windowSum = 0;
        for (int i = 0; i < k; i++) windowSum += nums[i];
        long maxSum = windowSum;

        for (int right = k; right < nums.length; right++) {
            windowSum += nums[right] - nums[right - k]; // add in, remove out — one line
            maxSum = Math.max(maxSum, windowSum);
        }

        return (double) maxSum / k; // divide ONCE at the end
    }
}
```

#### Dry Run
```
nums = [1,12,-5,-6,50,3],  k = 4

Phase 1: windowSum = 1+12-5-6 = 2,  maxSum = 2
right=4: 2 + 50 - 1 = 51,  maxSum = 51
right=5: 51 + 3 - 12 = 42, maxSum = 51

Answer: 51 / 4 = 12.75  ✓
```

#### Traps ⚠️
```java
// 1. NEVER divide at each step — floating point drift + slower.
//    Compare sums throughout. Divide once at the very end.

// 2. Overflow: nums[i] up to 10^4, k up to 10^4 → max sum = 10^8.
//    Fits in int (max ~2.1×10^9), but use long to be safe in interviews.

// 3. k > nums.length → guard clause, return 0.0 immediately.
```

#### Edge Cases
```
nums=[1],      k=1  → 1.0
nums=[3,3,3],  k=3  → 3.0
nums=[-1,-2],  k=2  → -1.5  (fixed window handles negatives fine)
k > n               → return 0.0
```

---

### P1.2 — Maximum Sum of K Consecutive Elements
**Classic** | Google, Amazon, Microsoft

#### Problem
Given an integer array and `k`, return the maximum sum of any contiguous subarray of size `k`.

#### Solution
```java
public class Solution {
    public int maxSumSubarray(int[] arr, int k) {
        int n = arr.length;
        if (arr == null || n == 0 || k > n || k <= 0) return 0;

        int windowSum = 0;
        for (int i = 0; i < k; i++) windowSum += arr[i];
        int maxSum = windowSum;

        for (int right = k; right < n; right++) {
            windowSum += arr[right] - arr[right - k];
            maxSum = Math.max(maxSum, windowSum);
        }

        return maxSum;
    }
}
```

#### Dry Run
```
arr = [2,1,5,1,3,2],  k = 3

Phase 1: window=[2,1,5], sum=8,  maxSum=8
right=3: 8 + 1 - 2 = 7,  maxSum=8
right=4: 7 + 3 - 1 = 9,  maxSum=9   ← window [5,1,3]
right=5: 9 + 2 - 5 = 6,  maxSum=9

Answer: 9  ✓
```

#### Edge Cases
```
k = 1       → max of individual elements
k = n       → sum of entire array
negatives   → fixed window works fine, finds the least-negative max window
```

---

### P1.3 — Number of Subarrays of Size K with Average ≥ Threshold
**LeetCode 1343** | Amazon, Google

#### Problem
Given array `arr`, integers `k` and `threshold`, return the number of subarrays of size `k` whose average is ≥ threshold.

#### Solution
```java
class Solution {
    public int numOfSubarrays(int[] arr, int k, int threshold) {
        // Avoid division: average >= threshold ↔ sum >= threshold * k
        int target = threshold * k;
        int windowSum = 0, count = 0;

        for (int i = 0; i < k; i++) windowSum += arr[i];
        if (windowSum >= target) count++;

        for (int right = k; right < arr.length; right++) {
            windowSum += arr[right] - arr[right - k];
            if (windowSum >= target) count++;
        }

        return count;
    }
}
```

#### Key Insight
```java
// Convert: average >= threshold  →  sum >= threshold * k
// Now it's a pure sum comparison — no division needed at all.
// This is faster and avoids floating-point errors entirely.
```

---

## ─────────────────────────────────────────────
## Variant 2 — Frequency Array + `matched` Counter
## ─────────────────────────────────────────────

**Use when**: the window must match a character pattern — anagram, permutation, exact char frequency.

**Signal words**: "anagram", "permutation", "contains all characters of", "rearrangement" in a fixed-size window.

**Why NOT `Arrays.equals()` each step?**
That costs O(26) per window → O(26n) total. The `matched` counter makes the check O(1).

```java
/**
 * VARIANT 2 — FREQUENCY ARRAY + matched COUNTER
 *
 * need[]   = frequency of each char required (built from pattern)
 * window[] = frequency of each char in current window
 * required = # of distinct chars in pattern that must ALL be satisfied
 * matched  = # of those chars currently satisfied in the window
 *
 * When matched == required → window is a valid anagram/permutation.
 *
 * On EXPAND (add char `in`):
 *   window[in]++
 *   if window[in] == need[in] → matched++   (just became satisfied)
 *
 * On REMOVE (remove char `out`):
 *   CRITICAL ORDER → check BEFORE decrementing:
 *   if window[out] == need[out] → matched--  (about to become unsatisfied)
 *   window[out]--
 *
 * Why check before decrementing?
 *   After window[out]--, the count drops below need[out].
 *   If you check after, window[out] < need[out] already — condition never fires.
 */
public int fixedWindowFreq(String text, String pattern) {
    int n = text.length(), k = pattern.length();
    if (k > n) return 0;

    int[] need   = new int[26];
    int[] window = new int[26];
    for (char c : pattern.toCharArray()) need[c - 'a']++;

    int required = 0;
    for (int f : need) if (f > 0) required++;
    int matched = 0, result = 0;

    for (int right = 0; right < n; right++) {

        // ① EXPAND
        int in = text.charAt(right) - 'a';
        window[in]++;
        if (window[in] == need[in]) matched++;

        // ② REMOVE outgoing char (fixed window of size k)
        if (right >= k) {
            int out = text.charAt(right - k) - 'a';
            if (window[out] == need[out]) matched--; // check BEFORE decrement ← critical
            window[out]--;
        }

        // ③ UPDATE
        if (right >= k - 1 && matched == required) result++;
    }

    return result;
    // boolean answer (LC 567): return result > 0
    // list of indices (LC 438): collect (right - k + 1) into a List
}
```

---

### P1.4 — Find All Anagrams in a String
**LeetCode 438** | Google, Microsoft, Amazon, Facebook

#### Problem
Given strings `s` and `p`, return all starting indices of `p`'s anagrams in `s`.

#### Solution
```java
class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        int n = s.length(), k = p.length();
        if (k > n) return result;

        int[] need   = new int[26];
        int[] window = new int[26];
        for (char c : p.toCharArray()) need[c - 'a']++;

        int required = 0;
        for (int f : need) if (f > 0) required++;
        int matched = 0;

        for (int right = 0; right < n; right++) {
            // Expand
            int in = s.charAt(right) - 'a';
            window[in]++;
            if (window[in] == need[in]) matched++;

            // Remove outgoing (fixed window)
            if (right >= k) {
                int out = s.charAt(right - k) - 'a';
                if (window[out] == need[out]) matched--; // check BEFORE decrement
                window[out]--;
            }

            // Record
            if (right >= k - 1 && matched == required) {
                result.add(right - k + 1); // start index of this window
            }
        }

        return result;
    }
}
```

#### Dry Run
```
s = "cbaebabacd",  p = "abc"
need = [a:1, b:1, c:1],  required = 3

right=0(c): window[c]=1 == need[c]=1 → matched=1
right=1(b): window[b]=1 == need[b]=1 → matched=2
right=2(a): window[a]=1 == need[a]=1 → matched=3. right>=2 → add index 0 ✓

right=3(e): Expand e (not in need). Remove s[0]=c:
  window[c]=1==need[c]=1 → matched-- =2. window[c]=0.
  matched=2, no record.

right=4(b): window[b]=2 ≠ need[b]=1. Remove s[1]=b:
  window[b]=2 ≠ need[b]=1 → matched stays 2. window[b]=1.
  matched=2, no record.

right=5(a): window[a]=2 ≠ need[a]=1. Remove s[2]=a:
  window[a]=2 ≠ 1 → matched stays. window[a]=1. matched=2, no record.

right=6(b): window[b]=2 ≠ 1. Remove s[3]=e (not in need). matched=2.

right=7(a): window[a]=2 ≠ 1. Remove s[4]=b:
  window[b]=2 ≠ 1. window[b]=1. matched=2.

right=8(c): window[c]=1==need[c]=1 → matched=3. Remove s[5]=a:
  window[a]=2 ≠ 1. window[a]=1. matched still 3. Add index 6 ✓

right=9(d): Expand d. Remove s[6]=b:
  window[b]=1==need[b]=1 → matched-- =2. window[b]=0. No record.

Answer: [0, 6]  ✓
```

#### Trap ⚠️
```java
// WRONG — decrement first, then check (condition never fires correctly):
window[out]--;
if (window[out] == need[out]) matched--;  // ✗ already decremented!

// CORRECT — check first, then decrement:
if (window[out] == need[out]) matched--;  // ✓ check while still at old value
window[out]--;
```

---

### P1.5 — Permutation in String
**LeetCode 567** | Microsoft, Amazon

#### Problem
Return `true` if `s2` contains a permutation of `s1`.

#### Solution
```java
class Solution {
    public boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;

        int[] need   = new int[26];
        int[] window = new int[26];
        for (char c : s1.toCharArray()) need[c - 'a']++;

        int k = s1.length(), required = 0, matched = 0;
        for (int f : need) if (f > 0) required++;

        for (int right = 0; right < s2.length(); right++) {
            int in = s2.charAt(right) - 'a';
            window[in]++;
            if (window[in] == need[in]) matched++;

            if (right >= k) {
                int out = s2.charAt(right - k) - 'a';
                if (window[out] == need[out]) matched--; // check BEFORE decrement
                window[out]--;
            }

            if (right >= k - 1 && matched == required) return true;
        }

        return false;
    }
}
```

#### Note
LC 567 and LC 438 are the **same template** — one returns boolean, the other returns indices. Master the template once, adapt the return for each.

---

## ─────────────────────────────────────────────
## Variant 3a — HashSet
## ─────────────────────────────────────────────

**Use when**: track which elements are present in the window (membership only, not count). Specifically for position-based duplicate detection within distance k.

**Signal words**: "two distinct indices i, j such that nums[i] == nums[j] and |i-j| <= k", "nearby duplicate", "within distance k".

```java
/**
 * VARIANT 3a — HashSet
 *
 * The Set acts as a membership register for the current window.
 * Window always covers indices [right-k .. right].
 *
 * On each step:
 *   1. Evict the element that just fell out of range (when right > k).
 *   2. Try to add nums[right]. If it's already there → duplicate within distance k.
 *
 * Eviction condition: right > k  (not right >= k — off-by-one trap below).
 * Evict index: right - k - 1  (the element now outside the window).
 */
public boolean fixedWindowSet(int[] nums, int k) {
    Set<Integer> window = new HashSet<>();

    for (int right = 0; right < nums.length; right++) {
        if (right > k) window.remove(nums[right - k - 1]); // evict element out of range
        if (!window.add(nums[right])) return true;          // already present → duplicate!
    }

    return false;
}
```

---

### P1.6 — Contains Duplicate II
**LeetCode 219** | LinkedIn, Palantir, Bloomberg

#### Problem
Return `true` if there exist indices `i`, `j` with `nums[i] == nums[j]` and `|i - j| <= k`.

#### Solution
```java
class Solution {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Set<Integer> window = new HashSet<>();

        for (int right = 0; right < nums.length; right++) {
            if (right > k) window.remove(nums[right - k - 1]); // evict out-of-range
            if (!window.add(nums[right])) return true;          // duplicate in window!
        }

        return false;
    }
}
```

#### Dry Run
```
nums = [1,2,3,1],  k = 3

right=0: right>3? No. window={1}
right=1: right>3? No. window={1,2}
right=2: right>3? No. window={1,2,3}
right=3: right>3? No. window.add(1) → 1 already in set → return true ✓

nums = [1,2,3,1,2,3],  k = 2

right=0: window={1}
right=1: window={1,2}
right=2: window={1,2,3}
right=3: right>2 → remove nums[0]=1. window={2,3}. add 1 → window={2,3,1}. No dup.
right=4: right>2 → remove nums[1]=2. window={3,1}. add 2 → window={3,1,2}. No dup.
right=5: right>2 → remove nums[2]=3. window={1,2}. add 3 → window={1,2,3}. No dup.
return false ✓
```

#### The Off-By-One Trap ⚠️
```java
// Window must span [right-k .. right] to satisfy |i-j| <= k.
// When right = k+1, the oldest valid index is right-k = 1.
// So we evict nums[0] = nums[right-k-1].
// Trigger: right > k  (NOT right >= k — that evicts one step too early).

// Proof: k=3, right=3. Window should be [0,1,2,3] (size 4).
// right > 3? No → don't evict. Index 0 stays in the window. Correct ✓
// If we used right >= 3: right=3 → evict nums[0]. Window = [1,2,3].
// Now right=3 and nums[0]=1 is gone → we'd miss the (0,3) duplicate. ✗
```

---

## ─────────────────────────────────────────────
## Variant 3b — Deque of Indices
## ─────────────────────────────────────────────

**Use when**: you need the **first or last element satisfying a condition** inside every window — not just membership, but **order matters**.

**Signal words**: "first negative in every window", "first element greater than x in every window", "leftmost candidate in each window".

**Use over HashSet when**: you need to know WHICH element and WHERE it is (index), not just whether something exists.

```java
/**
 * VARIANT 3b — DEQUE OF INDICES
 *
 * The Deque stores INDICES of candidate elements in the order they appear.
 * Front of the deque = index of the answer for the current window.
 *
 * On each new window position:
 *   ① Record: answer = arr[dq.peekFirst()] (or 0 if empty)
 *   ② Expand: if the new element qualifies, addLast its index
 *   ③ Evict:  if dq.front index has slid out of the next window → pollFirst()
 *
 * Why store INDICES not values?
 *   We need to check: "is this element still inside the window?"
 *   dq.peekFirst() <= windowStart → out of window → evict.
 *   Storing values makes this check impossible.
 */
public long[] fixedWindowDeque(long[] arr, int k) {
    int n = arr.length;
    long[] result = new long[n - k + 1];
    Deque<Integer> dq = new ArrayDeque<>(); // stores INDICES of candidates

    // Build first window [0 .. k-1]
    for (int i = 0; i < k; i++) {
        if (isCandidate(arr[i])) dq.addLast(i);
    }

    for (int right = k; right <= n; right++) {
        int windowStart = right - k;

        // ① Record answer for window [windowStart .. right-1]
        result[windowStart] = dq.isEmpty() ? 0 : arr[dq.peekFirst()];

        // ② Expand: add new element if it qualifies
        if (right < n && isCandidate(arr[right])) dq.addLast(right);

        // ③ Evict front if it has slid out of the NEXT window
        if (!dq.isEmpty() && dq.peekFirst() <= windowStart) dq.pollFirst();
    }

    return result;
}

private boolean isCandidate(long val) {
    return val < 0; // swap this condition per problem
}
```

---

### P1.7 — First Negative Number in Every Window of Size K
**GeeksForGeeks** | Amazon, Flipkart

#### Problem
For every window of size `k`, print the first negative integer. Print 0 if no negative exists in that window.

#### Solution
```java
public static long[] firstNegative(long[] arr, int k) {
    int n = arr.length;
    long[] result = new long[n - k + 1];
    Deque<Integer> dq = new ArrayDeque<>(); // stores INDICES of negatives only

    // Build first window [0 .. k-1]
    for (int i = 0; i < k; i++) {
        if (arr[i] < 0) dq.addLast(i);
    }

    for (int right = k; right <= n; right++) {
        int windowStart = right - k;

        // ① Record: front = leftmost negative in current window
        result[windowStart] = dq.isEmpty() ? 0 : arr[dq.peekFirst()];

        // ② Expand: add new element if negative
        if (right < n && arr[right] < 0) dq.addLast(right);

        // ③ Evict front if it has slid out of next window
        if (!dq.isEmpty() && dq.peekFirst() <= windowStart) dq.pollFirst();
    }

    return result;
}
```

#### Dry Run
```
arr = [-8, 2, 3, -6, 10],  k = 2

Build first window [0,1]: arr[0]=-8 qualifies → dq=[0]

right=2, windowStart=0:
  ① result[0] = arr[dq.front=0] = -8
  ② arr[2]=3, not negative — skip
  ③ dq.front=0 <= windowStart=0 → evict. dq=[]

right=3, windowStart=1:
  ① result[1] = 0  (dq empty)
  ② arr[3]=-6 < 0 → dq=[3]
  ③ dq.front=3 <= 1? No

right=4, windowStart=2:
  ① result[2] = arr[3] = -6
  ② arr[4]=10 — skip
  ③ dq.front=3 <= 2? No

right=5, windowStart=3:
  ① result[3] = arr[3] = -6
  ② right >= n — skip
  ③ dq.front=3 <= 3 → evict. dq=[]

Result: [-8, 0, -6, -6]  ✓
```

#### Trap ⚠️
```java
// Evict AFTER recording, not before.
// The sequence is: ① record → ② expand → ③ evict.
// If you evict before recording, you might evict the answer for the current window.

// Also: evict condition is dq.peekFirst() <= windowStart
// NOT < windowStart — the element AT windowStart is already outside the NEXT window.
```

---

## Pattern 1 — Summary

```
THREE VARIANTS — PICK BY WHAT YOU TRACK:

  Variant 1 — Running Sum
    Track : a number (sum / product / count)
    State : int windowSum
    Update: windowSum += arr[right] - arr[right-k]
    Use for: LC 643, max sum, threshold count

  Variant 2 — Freq Array + matched counter
    Track : char frequency match against a pattern
    State : int[] need, int[] window, int matched, int required
    Update: expand → if window[in]==need[in] matched++
            remove → if window[out]==need[out] matched-- THEN window[out]--
    Answer: matched == required
    Use for: LC 438, LC 567

  Variant 3a — HashSet
    Track : membership (is this element in the window?)
    State : Set<Integer> window
    Update: if right > k → remove nums[right-k-1]
            if !window.add(nums[right]) → duplicate found
    Use for: LC 219

  Variant 3b — Deque of indices
    Track : order of qualifying elements (first/last in window)
    State : Deque<Integer> dq (stores indices, NOT values)
    Update: ① record dq.peekFirst() → ② addLast if qualifies → ③ evict if stale
    Use for: First Negative in Window

UNIVERSAL TRAPS:
  • Use long for sums that may overflow int
  • Variant 2: check matched BEFORE decrementing window on removal — never after
  • Variant 3a: evict when right > k, not right >= k (off-by-one)
  • Variant 3b: store indices not values; evict front AFTER recording answer
```

---

*Next → Pattern 2: Variable Window — Maximize Length*