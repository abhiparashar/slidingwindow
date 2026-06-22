# Pattern 4 + 5: Count Subarrays
## Pattern 4: At Most K  |  Pattern 5: Exactly K = atMost(K) − atMost(K−1)

---

## When to Recognise These Patterns

| Signal in Problem | Pattern |
|---|---|
| "count subarrays with at most k distinct" | Pattern 4 directly |
| "number of subarrays with product < k" | Pattern 4 directly |
| "count subarrays with exactly k distinct" | Pattern 5 = P4(k) − P4(k−1) |
| "count subarrays with exactly k odd numbers" | Pattern 5 |
| "count subarrays with sum equal to goal" (binary array) | Pattern 5 |

---

## Which Variant to Use?

| What the problem counts as the metric | Variant |
|---|---|
| Number of distinct integers in window | **Variant 1 — HashMap distinct count** |
| Count of specific elements (odds, ones) in window | **Variant 2 — Element counter** |
| Product of all elements in window | **Variant 3 — Running product** |
| All three required characters present | **Variant 4 — All-present check (count from left)** |

---

## The Core Counting Insight

```
When window [left..right] is valid, ALL subarrays ending at right
that start anywhere from left to right are ALSO valid:
  [left..right], [left+1..right], ..., [right..right]
Count = right - left + 1

This works because a sub-window of a valid window is also valid,
provided the metric is monotone (distinct count, product of positives, etc.)
```

---

## ─────────────────────────────────────────────
## Pattern 4 Template — At Most K
## ─────────────────────────────────────────────

```java
/**
 * PATTERN 4 — COUNT SUBARRAYS WITH AT MOST K [CONDITION]
 *
 * ① EXPAND: include arr[right], update state.
 * ② SHRINK: while state exceeds k, undo arr[left], left++.
 * ③ COUNT:  count += right - left + 1.
 *           (all subarrays ending at right with start in [left..right])
 *
 * Amortised O(n): left moves forward at most n times total.
 */
public int atMostK(int[] arr, int k) {
    if (arr == null || arr.length == 0 || k < 0) return 0;

    // state = whatever you're measuring: Map, counter, product, etc.
    int left = 0, count = 0;

    for (int right = 0; right < arr.length; right++) {
        // ① EXPAND — add arr[right] to state

        while (/* state exceeds k */) {              // ② SHRINK
            // undo arr[left] from state
            left++;
        }

        count += right - left + 1;                   // ③ COUNT
    }

    return count;
}
```

---

## ─────────────────────────────────────────────
## Pattern 5 Template — Exactly K
## ─────────────────────────────────────────────

```java
/**
 * PATTERN 5 — COUNT SUBARRAYS WITH EXACTLY K [CONDITION]
 *
 * Why direct "exactly k" has no clean single-window invariant:
 *   If you shrink when > k, you skip past some exactly-k windows.
 *   If you stop when == k, you miss others starting at different lefts.
 *   No single left pointer isolates exactly k.
 *
 * Mathematical identity:
 *   count(exactly k) = count(at most k) − count(at most k−1)
 *
 * Implementation: write one atMost() helper, call it twice.
 * atMost(-1) must return 0 — guard for when caller passes k=0.
 */
public int exactlyK(int[] arr, int k) {
    return atMost(arr, k) - atMost(arr, k - 1);
}

private int atMost(int[] arr, int k) {
    if (k < 0) return 0;
    // Pattern 4 template here — atMost() IS Pattern 4
    int left = 0, count = 0;
    // ... state tracking ...
    for (int right = 0; right < arr.length; right++) {
        // expand, shrink, count
        count += right - left + 1;
    }
    return count;
}
```

---

## ─────────────────────────────────────────────
## Variant 1 — HashMap Distinct Count
## ─────────────────────────────────────────────

**Use when**: the metric is the number of **distinct integers** in the window.

**Signal words**: "at most k distinct integers", "exactly k different integers", "k unique values".

```java
/**
 * VARIANT 1 — HASHMAP DISTINCT COUNT
 *
 * freq map tracks count of each integer in the window.
 * freq.size() = distinct count.
 * Invalid when freq.size() > k.
 *
 * On expand: freq.merge(arr[right], 1, Integer::sum)
 * On shrink: decrement freq[arr[left]]; remove if 0. left++.
 *
 * Why remove when count hits 0?
 *   freq.size() counts map entries. Leaving zero-count entries inflates size().
 *   Always remove entry when count drops to 0.
 */
private int atMostDistinct(int[] nums, int k) {
    if (k < 0) return 0;

    Map<Integer, Integer> freq = new HashMap<>();
    int left = 0, count = 0;

    for (int right = 0; right < nums.length; right++) {
        freq.merge(nums[right], 1, Integer::sum);            // ① EXPAND

        while (freq.size() > k) {                            // ② SHRINK
            freq.merge(nums[left], -1, Integer::sum);
            if (freq.get(nums[left]) == 0) freq.remove(nums[left]);
            left++;
        }

        count += right - left + 1;                           // ③ COUNT
    }

    return count;
}
```

---

### P4/5.1 — Subarrays with K Different Integers
**LeetCode 992** | Hard | ⭐ | Google

#### Problem
Given integer array `nums` and integer `k`, return the number of subarrays with **exactly** `k` different integers.

#### Solution
```java
class Solution {
    public int subarraysWithKDistinct(int[] nums, int k) {
        return atMost(nums, k) - atMost(nums, k - 1);
    }

    private int atMost(int[] nums, int k) {
        if (k < 0) return 0;

        Map<Integer, Integer> freq = new HashMap<>();
        int left = 0, count = 0;

        for (int right = 0; right < nums.length; right++) {
            freq.merge(nums[right], 1, Integer::sum);        // ① EXPAND

            while (freq.size() > k) {                        // ② SHRINK
                freq.merge(nums[left], -1, Integer::sum);
                if (freq.get(nums[left]) == 0) freq.remove(nums[left]);
                left++;
            }

            count += right - left + 1;                       // ③ COUNT
        }

        return count;
    }
}
```

#### Proof the Identity Works
```
nums = [1,2,1,2,3],  k = 2

atMost(2) — subarrays with ≤ 2 distinct:
  [1],[2],[1],[2],[3]                 = 5
  [1,2],[2,1],[1,2],[2,3]            = 4
  [1,2,1],[2,1,2]                    = 2
  [1,2,1,2]                          = 1
  Total = 12

atMost(1) — subarrays with ≤ 1 distinct:
  [1],[2],[1],[2],[3]                 = 5

exactly(2) = 12 − 5 = 7  ✓
```

#### Dry Run (atMost with k=2)
```
nums = [1,2,1,2,3]

right=0: freq={1:1}, size=1. count += 1 = 1
right=1: freq={1:1,2:1}, size=2. count += 2 = 3
right=2: freq={1:2,2:1}, size=2. count += 3 = 6
right=3: freq={1:2,2:2}, size=2. count += 4 = 10
right=4: freq={1:2,2:2,3:1}, size=3 > 2 → SHRINK:
  remove nums[0]=1: freq={1:1,2:2,3:1}, left=1, size=3 → still >2
  remove nums[1]=2: freq={1:1,2:1,3:1}, left=2, size=3 → still >2
  remove nums[2]=1: freq={2:1,3:1}, left=3, size=2 → OK.
  count += 4-3+1=2 → total=12  ✓
```

---

## ─────────────────────────────────────────────
## Variant 2 — Element Counter
## ─────────────────────────────────────────────

**Use when**: the metric is a count of **specific elements** (odds, ones, evens, multiples of k) in the window — not distinct count, but raw count of qualifying elements.

**Signal words**: "exactly k odd numbers", "exactly k ones", "subarrays with k multiples of p".

```java
/**
 * VARIANT 2 — ELEMENT COUNTER
 *
 * Count qualifying elements (e.g. odds) in the window.
 * Invalid when count > k.
 *
 * On expand: if qualifies(arr[right]) → count++
 * On shrink: if qualifies(arr[left])  → count--; left++
 *
 * For exactly k: atMost(k) − atMost(k−1)
 * The atMost() shell is identical to Variant 1 — only the metric changes.
 */
private int atMostElements(int[] nums, int k) {
    if (k < 0) return 0;

    int left = 0, qualifyCount = 0, count = 0;

    for (int right = 0; right < nums.length; right++) {
        if (qualifies(nums[right])) qualifyCount++;          // ① EXPAND

        while (qualifyCount > k) {                           // ② SHRINK
            if (qualifies(nums[left])) qualifyCount--;
            left++;
        }

        count += right - left + 1;                           // ③ COUNT
    }

    return count;
}

private boolean qualifies(int val) {
    return val % 2 == 1; // example: odd numbers. Change per problem.
}
```

---

### P4/5.2 — Count Number of Nice Subarrays
**LeetCode 1248** | Amazon

#### Problem
Given `nums` and `k`, count subarrays with **exactly** `k` odd numbers.

#### Solution
```java
class Solution {
    public int numberOfSubarrays(int[] nums, int k) {
        return atMost(nums, k) - atMost(nums, k - 1);
    }

    private int atMost(int[] nums, int k) {
        if (k < 0) return 0;

        int left = 0, odds = 0, count = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] % 2 == 1) odds++;               // ① EXPAND

            while (odds > k) {                               // ② SHRINK
                if (nums[left] % 2 == 1) odds--;
                left++;
            }

            count += right - left + 1;                       // ③ COUNT
        }

        return count;
    }
}
```

#### Dry Run
```
nums = [1,1,2,1,1],  k = 3

atMost(3):
right=0: odds=1, count+=1=1
right=1: odds=2, count+=2=3
right=2: odds=2, count+=3=6
right=3: odds=3, count+=4=10
right=4: odds=4 > 3 → SHRINK: nums[0]=1 odd, odds=3, left=1. OK.
  count += 4-1+1=4 → total=14

atMost(2):
right=3: odds=3 > 2 → SHRINK until odds=2...
... total=7

exactly(3) = 14 - 7 = 7  ✓
```

---

### P4/5.3 — Binary Subarrays with Sum
**LeetCode 930** | Google, Facebook

#### Problem
Given binary array `nums` and integer `goal`, return the number of subarrays with sum equal to `goal`.

#### Translation
Binary array: sum = count of 1s. Exactly `goal` ones = Variant 2 with `qualifies = (val == 1)`.

#### Solution
```java
class Solution {
    public int numSubarraysWithSum(int[] nums, int goal) {
        return atMost(nums, goal) - atMost(nums, goal - 1);
    }

    private int atMost(int[] nums, int goal) {
        if (goal < 0) return 0;

        int left = 0, sum = 0, count = 0;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];                              // ① EXPAND

            while (sum > goal) {                             // ② SHRINK
                sum -= nums[left];
                left++;
            }

            count += right - left + 1;                       // ③ COUNT
        }

        return count;
    }
}
```

---

## ─────────────────────────────────────────────
## Variant 3 — Running Product
## ─────────────────────────────────────────────

**Use when**: the metric is the **product** of all elements in the window. Window is invalid when product ≥ k.

**Signal words**: "product of all elements in subarray less than k", "subarray product strictly less than k".

```java
/**
 * VARIANT 3 — RUNNING PRODUCT
 *
 * product tracks the product of all elements in [left..right].
 * Invalid when product >= k.
 *
 * On expand: product *= arr[right]
 * On shrink: product /= arr[left]; left++
 *
 * GUARD: if k <= 1, return 0 immediately.
 *   All elements are positive integers >= 1.
 *   Any single element has product >= 1 → can never be < 1.
 *   Without this guard: product=1 initially, 1>=1 → infinite shrink loop!
 */
private int atMostProduct(int[] nums, int k) {
    if (k <= 1) return 0;

    int left = 0, product = 1, count = 0;

    for (int right = 0; right < nums.length; right++) {
        product *= nums[right];                              // ① EXPAND

        while (product >= k) {                               // ② SHRINK
            product /= nums[left];
            left++;
        }

        count += right - left + 1;                           // ③ COUNT
    }

    return count;
}
```

---

### P4.4 — Subarray Product Less Than K
**LeetCode 713** | Amazon, Facebook

#### Problem
Given array of positive integers `nums` and integer `k`, return the number of contiguous subarrays where the product of all elements is strictly less than `k`.

#### Solution
```java
class Solution {
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;

        int left = 0, product = 1, count = 0;

        for (int right = 0; right < nums.length; right++) {
            product *= nums[right];                          // ① EXPAND

            while (product >= k) {                           // ② SHRINK
                product /= nums[left];
                left++;
            }

            count += right - left + 1;                       // ③ COUNT
        }

        return count;
    }
}
```

#### Why `count += right - left + 1`
```
Window [left=1, right=4], product < k.
All subarrays ending at right=4:
  [4..4], [3..4], [2..4], [1..4]  → 4 = right - left + 1  ✓

Sub-windows of valid windows are also valid (product only decreases
when you remove a positive element). So all are counted correctly.
```

#### Dry Run
```
nums = [10,5,2,6],  k = 100

right=0: product=10. count+=1=1
right=1: product=50. count+=2=3  ([5],[10,5])
right=2: product=100 >= 100 → SHRINK: product=10, left=1.
  count += 2-1+1=2 → total=5  ([2],[5,2])
right=3: product=60. count += 3-1+1=3 → total=8  ([6],[2,6],[5,2,6])

Answer: 8  ✓
```

#### Edge Case ⚠️
```java
k = 0 or k = 1 → product of positive ints >= 1 → never < 1 → return 0
// Without this guard: product=1 initially, while(1>=1) runs forever!
if (k <= 1) return 0;
```

---

## ─────────────────────────────────────────────
## Variant 4 — All-Present Check (Count From Left)
## ─────────────────────────────────────────────

**Use when**: the window is valid when ALL of a fixed set of elements are present. Instead of counting subarrays ending at right (right-left+1), count subarrays **starting at left** extending rightward (n-right) — because any extension of a valid window is also valid.

**Signal words**: "substrings containing all three characters", "substrings with at least one of each", "contains all of a,b,c".

```java
/**
 * VARIANT 4 — ALL-PRESENT CHECK
 *
 * Valid when all required elements appear at least once in window.
 * Once valid, ALL subarrays starting at left and ending at or beyond right are valid:
 *   [left..right], [left..right+1], ..., [left..n-1]
 *
 * So count += n - right  (not right - left + 1).
 *
 * Shrink WHILE all elements are present — count extensions before each shrink.
 *
 * Two counting directions — know both:
 *   count += right - left + 1  →  subarrays ENDING at right
 *   count += n - right         →  subarrays STARTING at left
 * Use direction 2 here because valid windows extend rightward freely.
 */
public int allPresentCount(String s, int n) {
    int[] freq = new int[3]; // 0=a, 1=b, 2=c
    int left = 0, count = 0;

    for (int right = 0; right < n; right++) {
        freq[s.charAt(right) - 'a']++;                       // ① EXPAND

        while (freq[0] > 0 && freq[1] > 0 && freq[2] > 0) { // ② SHRINK while VALID
            count += n - right;                               // ③ COUNT extensions from left
            freq[s.charAt(left) - 'a']--;
            left++;
        }
    }

    return count;
}
```

---

### P4.5 — Number of Substrings Containing All Three Characters
**LeetCode 1358** | Amazon

#### Problem
Given string `s` of only 'a','b','c', count substrings containing at least one of each.

#### Solution
```java
class Solution {
    public int numberOfSubstrings(String s) {
        int[] freq = new int[3];
        int left = 0, count = 0, n = s.length();

        for (int right = 0; right < n; right++) {
            freq[s.charAt(right) - 'a']++;                   // ① EXPAND

            while (freq[0] > 0 && freq[1] > 0 && freq[2] > 0) { // ② SHRINK while VALID
                count += n - right;                           // ③ COUNT from left
                freq[s.charAt(left) - 'a']--;
                left++;
            }
        }

        return count;
    }
}
```

#### Dry Run
```
s = "abcabc",  n = 6

right=2(c): freq=[1,1,1]. All present → ENTER while
  count += 6-2=4. Remove a, freq=[0,1,1], left=1. EXIT while.
right=3(a): freq=[1,1,1] → ENTER while
  count += 6-3=3. Remove b, freq=[1,0,1], left=2. EXIT while.
right=4(b): freq=[1,1,1] → ENTER while
  count += 6-4=2. Remove c, freq=[1,1,0], left=3. EXIT while.
right=5(c): freq=[1,1,1] → ENTER while
  count += 6-5=1. Remove a, freq=[0,1,1], left=4. EXIT while.

Total = 4+3+2+1 = 10  ✓
```

#### The Two Counting Directions ⚠️
```java
// Direction 1: count += right - left + 1
// → counts all subarrays ENDING at right (start anywhere from left to right)
// → use when: shrink makes window INVALID, then count what remains

// Direction 2: count += n - right
// → counts all subarrays STARTING at left (ending anywhere from right to n-1)
// → use when: window is VALID, any rightward extension is also valid

// LC 1358 uses Direction 2 because: once [left..right] contains all 3 chars,
// [left..right+1], [left..right+2],..., [left..n-1] also contain all 3.
```

---

### P4/5 Bonus — Count Subarrays with Score Less Than K
**LeetCode 2302** | Google

#### Problem
Score of a subarray = sum × length. Count subarrays with score < k.

#### Solution
```java
class Solution {
    public long countSubarrays(int[] nums, long k) {
        long sum = 0, count = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];                              // ① EXPAND

            while (sum * (right - left + 1) >= k) {         // ② SHRINK while invalid
                sum -= nums[left];
                left++;
            }

            count += right - left + 1;                       // ③ COUNT
        }

        return count;
    }
}
```

---

## Pattern 4 + 5 — Summary

```
FOUR VARIANTS — PICK BY WHAT THE METRIC IS:

  Variant 1 — HashMap Distinct Count
    Metric   : freq.size() (distinct integers)
    Invalid  : freq.size() > k
    Use for  : LC 992 (exactly k distinct)

  Variant 2 — Element Counter
    Metric   : count of qualifying elements (odds, ones, etc.)
    Invalid  : qualifyCount > k
    Use for  : LC 1248 (exactly k odds), LC 930 (sum == goal)

  Variant 3 — Running Product
    Metric   : product of all elements
    Invalid  : product >= k
    Guard    : if (k <= 1) return 0
    Use for  : LC 713 (product < k)

  Variant 4 — All-Present Check
    Metric   : all required elements present
    Valid    : freq[0]>0 && freq[1]>0 && freq[2]>0
    Count    : n - right  (NOT right - left + 1)
    Use for  : LC 1358 (all 3 chars present)

PATTERN 5 (EXACTLY K):
  return atMost(arr, k) - atMost(arr, k - 1)
  atMost(-1) must return 0
  The atMost() shell is Pattern 4 — only the metric changes.

COUNTING DIRECTIONS — KNOW BOTH:
  count += right - left + 1  → subarrays ENDING at right
  count += n - right         → subarrays STARTING at left

TRAPS:
  • Variant 3: guard k <= 1 → return 0, or infinite loop
  • Variant 1: remove map entry when count hits 0 — else size() lies
  • Pattern 5: atMost(-1) = 0 — guard for k = 0 caller
  • Counting direction wrong → double counts or misses subarrays
```

---

*Next → Pattern 6: Monotonic Deque Window*