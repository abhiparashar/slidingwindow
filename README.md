# Sliding Window Patterns

Notes on recognizing and implementing the sliding window technique, broken into six patterns. Each pattern file covers when to recognize it, which variant to use, and annotated Java implementations.

## Patterns

| # | File | Pattern | Core Rule |
|---|---|---|---|
| 1 | [pattern01_fixed_window.md](pattern01_fixed_window.md) | Fixed Window | Window size `k` is given and every window is valid. Slide one step at a time. |
| 2 | [Pattern02_variable_max.md](Pattern02_variable_max.md) | Variable Window — Maximize Length | Expand right always. Shrink left **while invalid**. Record length **after** the while loop. |
| 3 | [Pattern03_variable_min.md](Pattern03_variable_min.md) | Variable Window — Minimize Length | Expand right always. Shrink left **while valid**. Record length **inside** the while loop. |
| 4/5 | [Pattern_04_05_count_and_exactlyk.md](Pattern_04_05_count_and_exactlyk.md) | Count Subarrays — At Most K / Exactly K | Count subarrays ending at `right` as `right - left + 1`. Exactly K = atMost(K) − atMost(K−1). |
| 6 | [Pattern06_monotonic_deque.md](Pattern06_monotonic_deque.md) | Monotonic Deque Window | Use a deque to get the max/min of every window in O(n) instead of O(nk). |

## How to pick a pattern

1. **Is the window size fixed (`k` given) and always valid?** → Pattern 1 (Fixed Window).
2. **Are you asked for the longest/maximum length subarray under a constraint?** → Pattern 2.
3. **Are you asked for the shortest/minimum length subarray under a constraint?** → Pattern 3.
4. **Are you asked to count subarrays (at most k / exactly k of something)?** → Pattern 4/5.
5. **Do you need the max or min inside every window, not just whether it's valid?** → Pattern 6 (Monotonic Deque).

## Example

[LongestOnes.java](LongestOnes.java) solves "Max Consecutive Ones III" (longest subarray of 1s after flipping at most `k` zeros) — a textbook application of Pattern 2 (Variable Window — Maximize Length), using a bad-count (zero-count) variant.
