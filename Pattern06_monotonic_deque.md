# Pattern 6: Monotonic Deque Window
## When you need MAX or MIN inside every window in O(n).

---

## When to Recognise This Pattern

| Signal in Problem | What It Means |
|---|---|
| "maximum/minimum in each window of size k" | Monotonic deque — fixed window |
| "longest subarray where max − min ≤ limit" | Two deques — variable window |
| "shortest subarray with sum ≥ k" (with negatives!) | Deque + prefix sum |
| "max score reachable jumping at most k steps" | DP + deque |

---

## Which Variant to Use?

| What the problem needs | Variant |
|---|---|
| Max (or min) of every fixed-size window of size k | **Variant 1 — Single deque (fixed window)** |
| Longest window where max − min ≤ limit | **Variant 2 — Two deques (variable window)** |
| Shortest subarray with sum ≥ k, array has negatives | **Variant 3 — Prefix sum + deque** |
| DP where each state depends on max of previous k states | **Variant 4 — DP + deque** |

---

## Why Normal Approaches Fail

```
Naive (scan each window for max): O(nk). For n=10^5, k=10^4 → 10^9 ops. TLE.
TreeMap: O(n log k). Works, but deque gives O(n) — strictly better.
Max-heap: O(n log k). Also works, but lazy deletion is messy. Deque is cleaner.
```

---

## The Core Deque Invariants

```
MAX DEQUE:
  Values at stored indices are DECREASING  front → back.
  Front = index of window's MAXIMUM.
  Back eviction: remove indices whose values are <= current  (they're dominated).

MIN DEQUE:
  Values at stored indices are INCREASING  front → back.
  Front = index of window's MINIMUM.
  Back eviction: remove indices whose values are >= current  (they're dominated).

WHY STORE INDICES NOT VALUES?
  Need to check: "is this element still inside the window?"
  dq.peekFirst() < windowLeftBound → out of window → evict.
  If we stored values, this check is impossible.
```

---

## ─────────────────────────────────────────────
## Variant 1 — Single Deque (Fixed Window)
## ─────────────────────────────────────────────

**Use when**: find the max (or min) of every fixed-size window of size k.

**Signal words**: "sliding window maximum", "maximum of each subarray of size k", "sliding window minimum".

```java
/**
 * VARIANT 1 — SINGLE MONOTONIC DEQUE (FIXED WINDOW)
 *
 * Deque stores INDICES. Values at those indices are DECREASING front→back (for MAX).
 * Front = index of the MAXIMUM in the current window.
 *
 * For each new right:
 *   ① Evict FRONT: remove indices that have slid out of [right-k+1 .. right].
 *      Condition: dq.peekFirst() < right - k + 1
 *
 *   ② Evict BACK: remove indices whose values are <= nums[right].
 *      They are DOMINATED — while nums[right] is in the window,
 *      any earlier element with smaller value can never be the max.
 *      Condition: nums[dq.peekLast()] <= nums[right]
 *
 *   ③ Add current index to back.
 *
 *   ④ Record: once window is full (right >= k-1), front = max index.
 *
 * For MIN: flip back-eviction to nums[dq.peekLast()] >= nums[right].
 *
 * Complexity: O(n) time — each index added and removed at most once.
 *             O(k) space — deque holds at most k indices.
 *
 * Use ArrayDeque, not LinkedList — same complexity, better constants.
 * Use pollFirst/pollLast (return null) not removeFirst/removeLast (throw exception).
 */
public int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> dq = new ArrayDeque<>(); // stores INDICES

    for (int right = 0; right < n; right++) {
        // ① Evict FRONT — index out of window [right-k+1 .. right]
        while (!dq.isEmpty() && dq.peekFirst() < right - k + 1) {
            dq.pollFirst();
        }

        // ② Evict BACK — values <= current are dominated (for MAX)
        while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[right]) {
            dq.pollLast();
        }

        // ③ Add current index
        dq.addLast(right);

        // ④ Record when window is full
        if (right >= k - 1) {
            result[right - k + 1] = nums[dq.peekFirst()];
        }
    }

    return result;
}
```

#### Deque API Cheatsheet
```java
Deque<Integer> dq = new ArrayDeque<>(); // always ArrayDeque, not LinkedList

dq.addLast(x);      // push to back   ← add new elements here
dq.pollFirst();     // remove front   ← evict stale (out-of-window)
dq.pollLast();      // remove back    ← evict dominated (monotonicity)
dq.peekFirst();     // view front     ← the answer (max or min index)
dq.peekLast();      // view back      ← compare with incoming element
dq.isEmpty();       // always check before peek/poll!

// pollFirst/pollLast return null if empty — safe.
// removeFirst/removeLast throw NoSuchElementException if empty — avoid.
```

---

### P6.1 — Sliding Window Maximum
**LeetCode 239** | Hard | ⭐ Classic | Google, Amazon, Uber, Microsoft

#### Problem
Given `nums` and window size `k`, return an array where each element is the maximum of the corresponding window.

#### Solution
```java
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();

        for (int right = 0; right < n; right++) {
            // ① Evict stale front
            while (!dq.isEmpty() && dq.peekFirst() < right - k + 1) dq.pollFirst();

            // ② Evict dominated back (for MAX: remove <= current)
            while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[right]) dq.pollLast();

            dq.addLast(right);                               // ③ Add index

            if (right >= k - 1) result[right - k + 1] = nums[dq.peekFirst()]; // ④ Record
        }

        return result;
    }
}
```

#### Full Dry Run
```
nums = [3,1,2,5,4,6,7,2],  k = 3

right=0: dq=[]. Front evict: none. Back evict: none. Add 0. dq=[0](val 3).
right=1: Front: 0 >= -1+1=0, OK. Back: nums[0]=3 <= nums[1]=1? No. Add 1. dq=[0,1](3,1).
right=2: Front OK. Back: nums[1]=1 <= 2? Yes → pop 1. nums[0]=3 <= 2? No. Add 2. dq=[0,2](3,2).
  right>=2 → result[0] = nums[dq.front=0] = 3  ✓

right=3: Front: 0 >= 3-3+1=1? No → pop 0. Back: nums[2]=2 <= 5? Yes → pop 2. Add 3. dq=[3](5).
  result[1] = nums[3] = 5  ✓

right=4: Front: 3 >= 2? Yes OK. Back: nums[3]=5 <= 4? No. Add 4. dq=[3,4](5,4).
  result[2] = nums[3] = 5  ✓

right=5: Front: 3 >= 3? Yes OK. Back: nums[4]=4 <= 6? Yes → pop 4. nums[3]=5 <= 6? Yes → pop 3. Add 5. dq=[5](6).
  result[3] = nums[5] = 6  ✓

right=6: Front: 5 >= 4? Yes OK. Back: nums[5]=6 <= 7? Yes → pop 5. Add 6. dq=[6](7).
  result[4] = nums[6] = 7  ✓

right=7: Front: 6 >= 5? Yes OK. Back: nums[6]=7 <= 2? No. Add 7. dq=[6,7](7,2).
  result[5] = nums[6] = 7  ✓

Result: [3,5,5,6,7,7]  ✓
```

#### Edge Cases
```
k = 1       → result = nums (each window has one element)
k = n       → result has one element = max of entire array
all same    → every window returns that value
descending  → front of deque always has leftmost (largest) index in window
```

---

### P6.2 — Sliding Window Minimum
**Classic** | Google, Amazon

#### Problem
Given `nums` and window size `k`, return an array of the minimum in each window.

#### Solution
```java
public int[] minSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> dq = new ArrayDeque<>();

    for (int right = 0; right < n; right++) {
        while (!dq.isEmpty() && dq.peekFirst() < right - k + 1) dq.pollFirst();  // ① stale front
        while (!dq.isEmpty() && nums[dq.peekLast()] >= nums[right]) dq.pollLast(); // ② dominated back (MIN: >=)
        dq.addLast(right);
        if (right >= k - 1) result[right - k + 1] = nums[dq.peekFirst()];        // ③ front = min
    }

    return result;
}
```

#### The Only Difference From Max ⚠️
```java
// MAX deque: evict back when  nums[dq.peekLast()] <= nums[right]  (remove smaller)
// MIN deque: evict back when  nums[dq.peekLast()] >= nums[right]  (remove larger)

// Everything else — front eviction, addLast, result recording — is identical.
```

---

## ─────────────────────────────────────────────
## Variant 2 — Two Deques (Variable Window)
## ─────────────────────────────────────────────

**Use when**: the validity condition involves BOTH max AND min of the window simultaneously — e.g. `max(window) - min(window) <= limit`.

**Signal words**: "longest subarray where absolute difference between any two elements ≤ limit", "max minus min constraint".

```java
/**
 * VARIANT 2 — TWO MONOTONIC DEQUES (VARIABLE WINDOW)
 *
 * Maintain two deques simultaneously:
 *   maxDq: decreasing values front→back. Front = window MAX.
 *   minDq: increasing values front→back. Front = window MIN.
 *
 * On each right:
 *   Maintain both deques (add to back, evict dominated from back).
 *   Check validity: nums[maxDq.front] - nums[minDq.front] > limit → SHRINK.
 *   When shrinking (left++), evict stale fronts from BOTH deques.
 *
 * Why evict fronts AFTER incrementing left, not before?
 *   The condition checks whether front index < left (new left).
 *   Must increment left first, then check.
 */
public int twoDequeWindow(int[] nums, int limit) {
    Deque<Integer> maxDq = new ArrayDeque<>(); // decreasing → front = max
    Deque<Integer> minDq = new ArrayDeque<>(); // increasing → front = min
    int left = 0, maxLen = 0;

    for (int right = 0; right < nums.length; right++) {
        // Maintain maxDq
        while (!maxDq.isEmpty() && nums[maxDq.peekLast()] <= nums[right]) maxDq.pollLast();
        maxDq.addLast(right);

        // Maintain minDq
        while (!minDq.isEmpty() && nums[minDq.peekLast()] >= nums[right]) minDq.pollLast();
        minDq.addLast(right);

        // SHRINK while window is INVALID (max - min > limit)
        while (nums[maxDq.peekFirst()] - nums[minDq.peekFirst()] > limit) {
            left++;
            if (maxDq.peekFirst() < left) maxDq.pollFirst(); // evict stale max
            if (minDq.peekFirst() < left) minDq.pollFirst(); // evict stale min
        }

        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}
```

---

### P6.3 — Longest Continuous Subarray with Absolute Diff ≤ Limit
**LeetCode 1438** | Hard | Google

#### Problem
Given `nums` and integer `limit`, return the length of the longest subarray such that `max(window) - min(window) <= limit`.

#### Solution
```java
class Solution {
    public int longestSubarray(int[] nums, int limit) {
        Deque<Integer> maxDq = new ArrayDeque<>();
        Deque<Integer> minDq = new ArrayDeque<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            // Maintain MAX deque (decreasing)
            while (!maxDq.isEmpty() && nums[maxDq.peekLast()] <= nums[right]) maxDq.pollLast();
            maxDq.addLast(right);

            // Maintain MIN deque (increasing)
            while (!minDq.isEmpty() && nums[minDq.peekLast()] >= nums[right]) minDq.pollLast();
            minDq.addLast(right);

            // Shrink while invalid
            while (nums[maxDq.peekFirst()] - nums[minDq.peekFirst()] > limit) {
                left++;
                if (maxDq.peekFirst() < left) maxDq.pollFirst();
                if (minDq.peekFirst() < left) minDq.pollFirst();
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}
```

#### Dry Run
```
nums = [8,2,4,7],  limit = 4

right=0: maxDq=[0](8), minDq=[0](8). max-min=0 ≤ 4. len=1
right=1: maxDq: 8>2 keep → [0,1](8,2). minDq: 8>=2 pop → [1](2).
  max=8, min=2. 8-2=6 > 4 → SHRINK: left=1. maxDq.front=0 < 1 → pop. maxDq=[1](2).
  max=2, min=2. 0 ≤ 4. len=1
right=2: maxDq: 2<=4 pop → [2](4). minDq: 2<=4 keep → [1,2](2,4).
  max=4, min=2. 2 ≤ 4. len = max(1, 2-1+1=2) = 2
right=3: maxDq: 4<=7 pop → [3](7). minDq: 4<=7 keep → [1,2,3](2,4,7).
  max=7, min=2. 5 > 4 → SHRINK: left=2. minDq.front=1 < 2 → pop. minDq=[2,3](4,7).
  max=7, min=4. 3 ≤ 4. len = max(2, 3-2+1=2) = 2

Answer: 2  ✓
```

---

## ─────────────────────────────────────────────
## Variant 3 — Prefix Sum + Deque (Negatives Allowed)
## ─────────────────────────────────────────────

**Use when**: find the shortest subarray with sum ≥ k, but the array **can have negative numbers**. Standard shrinking breaks with negatives — use prefix sums + monotonic deque.

**Signal words**: "shortest subarray with sum ≥ k" + constraints mention negative integers.

```java
/**
 * VARIANT 3 — PREFIX SUM + MONOTONIC DEQUE
 *
 * Why standard shrinking fails with negatives:
 *   Removing a negative element from the left INCREASES the sum.
 *   Shrinking doesn't converge — the window might become valid again after growing.
 *
 * Key identity: sum(i..j) = prefix[j+1] - prefix[i]
 * We want: prefix[right] - prefix[left] >= k → minimise right - left.
 *
 * Deque stores prefix indices in INCREASING prefix-value order.
 * For each right:
 *   ① Pop front while prefix[right] - prefix[front] >= k:
 *      Valid subarray found! Record length = right - front. Pop front (greedy shortest).
 *   ② Pop back while prefix[back] >= prefix[right]:
 *      Back is dominated — any future right that satisfies back will also satisfy right,
 *      but right gives a shorter subarray (right index is larger). Evict back.
 *   ③ Add right to back.
 *
 * Complexity: O(n) time, O(n) space (prefix array).
 */
public int shortestSubarrayNegatives(int[] nums, int k) {
    int n = nums.length;
    long[] prefix = new long[n + 1];
    for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];

    Deque<Integer> dq = new ArrayDeque<>(); // indices of prefix array, increasing values
    int minLen = Integer.MAX_VALUE;

    for (int right = 0; right <= n; right++) {
        // ① Pop front while valid subarray found (sum >= k)
        while (!dq.isEmpty() && prefix[right] - prefix[dq.peekFirst()] >= k) {
            minLen = Math.min(minLen, right - dq.pollFirst()); // greedy: pop = get shortest
        }

        // ② Pop back while back prefix >= current prefix (dominated)
        while (!dq.isEmpty() && prefix[dq.peekLast()] >= prefix[right]) {
            dq.pollLast();
        }

        // ③ Add current prefix index
        dq.addLast(right);
    }

    return minLen == Integer.MAX_VALUE ? -1 : minLen;
}
```

---

### P6.4 — Shortest Subarray with Sum ≥ K
**LeetCode 862** | Hard | Google, Amazon

#### Problem
Given integer array `nums` (may contain negatives) and integer `k`, return the length of the shortest non-empty subarray with sum ≥ k. Return -1 if no such subarray.

#### Solution
```java
class Solution {
    public int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];

        Deque<Integer> dq = new ArrayDeque<>();
        int minLen = Integer.MAX_VALUE;

        for (int right = 0; right <= n; right++) {
            // Pop front while sum >= k (valid, record shortest)
            while (!dq.isEmpty() && prefix[right] - prefix[dq.peekFirst()] >= k) {
                minLen = Math.min(minLen, right - dq.pollFirst());
            }

            // Pop back while prefix[back] >= prefix[right] (dominated)
            while (!dq.isEmpty() && prefix[dq.peekLast()] >= prefix[right]) {
                dq.pollLast();
            }

            dq.addLast(right);
        }

        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }
}
```

#### Why Back Eviction Works
```
If prefix[j] >= prefix[right] and j < right:
  For any future position r > right, if prefix[r] - prefix[j] >= k,
  then prefix[r] - prefix[right] >= k also (since prefix[right] <= prefix[j]).
  But the subarray starting at right is SHORTER (right > j).
  So j can never give a shorter answer than right → evict j.
```

---

## ─────────────────────────────────────────────
## Variant 4 — DP + Deque
## ─────────────────────────────────────────────

**Use when**: a DP recurrence needs the **maximum of the previous k dp values**, i.e. `dp[i] = f(nums[i], max(dp[i-k .. i-1]))`. The deque replaces the O(k) max scan with O(1).

**Signal words**: "jump at most k steps", "maximum score", "dp over sliding window of size k".

```java
/**
 * VARIANT 4 — DP + MONOTONIC DEQUE
 *
 * dp[i] = optimal value at position i.
 * For each i, we need max(dp[i-k], dp[i-k+1], ..., dp[i-1]) in O(1).
 *
 * Deque stores dp indices in DECREASING dp-value order (for max).
 * Front = index of max dp value in the window [i-k .. i-1].
 *
 * Steps for each i:
 *   ① Evict front: if front index < i - k → out of window.
 *   ② Use front: dp[i] = nums[i] + dp[dq.peekFirst()].
 *   ③ Evict back: while dp[back] <= dp[i] → add current to back.
 *   ④ Add i to back.
 */
public int dpWithDeque(int[] nums, int k) {
    int n = nums.length;
    int[] dp = new int[n];
    dp[0] = nums[0];

    Deque<Integer> dq = new ArrayDeque<>();
    dq.addLast(0);

    for (int i = 1; i < n; i++) {
        // ① Evict front if outside window of k
        while (!dq.isEmpty() && dq.peekFirst() < i - k) dq.pollFirst();

        // ② Compute dp[i] using max dp in window
        dp[i] = nums[i] + dp[dq.peekFirst()];

        // ③ Maintain decreasing dp order — evict dominated back
        while (!dq.isEmpty() && dp[dq.peekLast()] <= dp[i]) dq.pollLast();

        // ④ Add current index
        dq.addLast(i);
    }

    return dp[n - 1];
}
```

---

### P6.5 — Jump Game VI
**LeetCode 1696** | Medium | Google

#### Problem
At index 0 of `nums`. Each move, jump at most `k` steps forward. Score = sum of `nums[i]` at all visited indices. Maximise the score.

#### Solution
```java
class Solution {
    public int maxResult(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = nums[0];

        Deque<Integer> dq = new ArrayDeque<>();
        dq.addLast(0);

        for (int i = 1; i < n; i++) {
            // Evict front if outside window of k
            while (!dq.isEmpty() && dq.peekFirst() < i - k) dq.pollFirst();

            // Best score at i = nums[i] + best dp among [i-k .. i-1]
            dp[i] = nums[i] + dp[dq.peekFirst()];

            // Maintain decreasing dp values in deque
            while (!dq.isEmpty() && dp[dq.peekLast()] <= dp[i]) dq.pollLast();
            dq.addLast(i);
        }

        return dp[n - 1];
    }
}
```

#### Dry Run
```
nums = [1,-1,-2,4,-7,3],  k = 2

dp[0]=1, dq=[0]

i=1: front=0 >= 1-2=-1 OK. dp[1]=-1+dp[0]=-1+1=0. dp[0]=1>=0 → keep. dq=[0,1]
i=2: front=0 >= 2-2=0 OK. dp[2]=-2+dp[0]=-2+1=-1. dp[1]=0>=-1→pop 1. dp[0]=1>=-1→keep. dq=[0,2]
i=3: front=0 >= 3-2=1? No → pop 0. front=2 >= 1 OK. dp[3]=4+dp[2]=4-1=3. dp[2]=-1<=3→pop 2. dq=[3]
i=4: front=3 >= 4-2=2 OK. dp[4]=-7+dp[3]=-7+3=-4. dp[3]=3>=-4→keep. dq=[3,4]
i=5: front=3 >= 5-2=3 OK. dp[5]=3+dp[3]=3+3=6. dp[4]=-4<=6→pop. dp[3]=3<=6→pop. dq=[5]

Answer: dp[5] = 6  ✓
```

---

## Pattern 6 — Summary

```
FOUR VARIANTS — PICK BY WHAT THE PROBLEM NEEDS:

  Variant 1 — Single Deque (Fixed Window)
    For      : max or min of every window of size k
    Deque    : one deque (decreasing for MAX, increasing for MIN)
    Front    : the answer (max or min index)
    Use for  : LC 239 (max), sliding window min

  Variant 2 — Two Deques (Variable Window)
    For      : longest window where max - min <= limit
    Deque    : maxDq (decreasing) + minDq (increasing) simultaneously
    Shrink   : while max - min > limit; evict stale fronts after left++
    Use for  : LC 1438

  Variant 3 — Prefix Sum + Deque
    For      : shortest subarray sum >= k with NEGATIVE numbers
    Deque    : increasing prefix values (not nums values!)
    Front    : evict when prefix[right] - prefix[front] >= k (valid, record)
    Back     : evict when prefix[back] >= prefix[right] (dominated)
    Use for  : LC 862

  Variant 4 — DP + Deque
    For      : dp recurrence needing max of previous k dp values
    Deque    : decreasing dp values front→back
    Front    : max dp in window [i-k .. i-1]
    Use for  : LC 1696

UNIVERSAL TRAPS:
  • Store INDICES not values — needed to check window boundary
  • Use pollFirst/pollLast (null-safe) not removeFirst/removeLast (throws exception)
  • Use ArrayDeque not LinkedList — same O, better constants
  • MAX deque: evict back when value <= current
    MIN deque: evict back when value >= current  (flip the comparison)
  • Variant 2: evict stale fronts AFTER incrementing left, not before
  • Variant 3: deque is over PREFIX array (size n+1), not nums (size n)
  • Front eviction bound: < right - k + 1  (not <=, not < right - k)

DEQUE INVARIANTS:
  MAX deque: DECREASING values front→back. Back evict: value <= current.
  MIN deque: INCREASING values front→back. Back evict: value >= current.
```

---

*End of Pattern 6 — You now have all 6 patterns mastered.*