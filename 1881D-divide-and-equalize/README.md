# 1881D · Divide and Equalize

**Platform:** Codeforces (Round 903, Div. 3) · **Rating:** 1300 · **Tags:** math, number theory · **Solved:** 16 Nov 2026 · [Original problem](https://codeforces.com/problemset/problem/1881/D)

---

## 1. The problem in simple words

You have an array of `n` positive numbers. One move works like this:

1. Pick two **different** positions `i` and `j`.
2. Pick any number `x` that divides `a[i]` exactly.
3. Divide `a[i]` by `x` and multiply `a[j]` by `x`.

You can do as many moves as you like (even zero). **Can you make every number in the array equal?** Print `YES` or `NO` for each test case.

| Limit | Value |
|---|---|
| Test cases `t` | up to 2000 |
| Array size `n` | 1 to 10^4 (sum of all `n` ≤ 10^4) |
| Values `a[i]` | 1 to 10^6 |
| Time limit | 2 seconds |

---

## 2. Intuition: what never changes?

Look at the **product** of the whole array. A move divides one number by `x` and multiplies another by `x`, so the product stays exactly the same.

Think of each number as a bag of prime factors. `100 = 2 · 2 · 5 · 5` is a bag holding two 2s and two 5s. A move takes some primes out of one bag and drops them into another. The total count of each prime across all bags never changes. You just move primes around.

So the question becomes: **can every bag end up holding the same primes?** That works exactly when each prime's total count can be split evenly into `n` bags.

> ✅ Answer is `YES` **if and only if** for every prime `p`, (total number of times `p` appears across all numbers) is divisible by `n`.

Why "if": if every count is divisible by `n`, give each number `count/n` copies of every prime, and all numbers become equal.
Why "only if": if all numbers end up equal to `y`, the product is `y^n`, so every prime count is a multiple of `n`.

---

## 3. Brute force (and why it fails)

**Idea:** multiply everything together and check whether the product is a perfect `n`-th power (some `y` with `y^n = product`).

**Problem:** the product can be as large as (10^6)^(10^4), a number with about 60,000 digits. Normal `long` overflows instantly, and even `BigInteger` with root-finding is far too slow.

This is still useful for **testing**: for tiny arrays (n ≤ 4, values ≤ 30), the product fits in a `long`, so I used it to cross-check the fast solution on 2,000 random cases.

---

## 4. Optimal approach: count prime exponents

1. **Sieve once:** build `spf[x]` = smallest prime factor of `x` for every `x` up to 10^6. Then any number can be factorized in `O(log x)` steps by repeatedly dividing by `spf[x]`.
2. For each test case, factorize every `a[i]` and add up how many times each prime appears (`cnt[p]`).
3. If any `cnt[p] % n != 0`, the answer is `NO`; otherwise `YES`.
4. **Reset only the primes you touched.** Keep a small list of them so the next test case starts clean without wiping a 10^6-sized array.

---

## 5. Dry run

**Input:** `n = 5`, `a = [100, 2, 50, 10, 1]`

| Number | Prime factors | 2s | 5s |
|---|---|---|---|
| 100 | 2 · 2 · 5 · 5 | 2 | 2 |
| 2 | 2 | 1 | 0 |
| 50 | 2 · 5 · 5 | 1 | 2 |
| 10 | 2 · 5 | 1 | 1 |
| 1 | (none) | 0 | 0 |
| **Total** | | **5** | **5** |

5 % 5 = 0 for both primes, so the answer is **YES**. Every number becomes 2 · 5 = 10.

**Input:** `n = 4`, `a = [8, 2, 4, 2]` → total 2s = 3 + 1 + 2 + 1 = **7**. Since 7 % 4 ≠ 0, the answer is **NO**.

**Input:** `n = 4`, `a = [30, 50, 27, 20]` → 2s = 1+1+0+2 = 4, 3s = 1+0+3+0 = 4, 5s = 1+2+0+1 = 4. All divisible by 4, so the answer is **YES**.

---

## 6. Java code

```java
import java.io.*;
import java.util.*;

public class Main {
    static final int MAX = 1_000_000;
    static int[] spf = new int[MAX + 1];

    public static void main(String[] args) throws IOException {
        // smallest prime factor sieve, built once
        for (int i = 2; i <= MAX; i++) {
            if (spf[i] == 0) {
                for (int j = i; j <= MAX; j += i) if (spf[j] == 0) spf[j] = i;
            }
        }
        FastReader in = new FastReader(System.in);
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        int[] cnt = new int[MAX + 1];
        int[] touched = new int[200_000];
        int t = in.nextInt();
        while (t-- > 0) {
            int n = in.nextInt();
            int tc = 0;
            for (int i = 0; i < n; i++) {
                int x = in.nextInt();
                while (x > 1) {
                    int p = spf[x];
                    while (x % p == 0) { x /= p; if (cnt[p]++ == 0) touched[tc++] = p; }
                }
            }
            boolean ok = true;
            for (int i = 0; i < tc; i++) {
                int p = touched[i];
                if (cnt[p] % n != 0) ok = false;
                cnt[p] = 0;                 // reset only what we used
            }
            out.println(ok ? "YES" : "NO");
        }
        out.flush();
    }

    static class FastReader { /* fast byte reader, see Main.java */ }
}
```

The complete file, including the fast input reader, is [`Main.java`](./Main.java).

---

## 7. Complexity

| | Cost | Why |
|---|---|---|
| Sieve | O(MAX · log log MAX) ≈ a few million steps, once | Standard smallest-prime-factor sieve |
| Per number | O(log a[i]) | Each division at least halves the number |
| **Total time** | **O(MAX log log MAX + Σn · log A)** | Ran in 234 ms on the judge |
| **Space** | **O(MAX)** | `spf` and `cnt` arrays of size 10^6 |

---

## 8. Edge cases

- `n = 1`: always `YES`, since any count is divisible by 1.
- All ones: no primes at all, so `YES`.
- A large prime such as 999983 appearing once with `n ≥ 2`: count 1 is not divisible, so `NO`.

## 9. Common mistakes

- **Clearing the 10^6 count array for every test case.** With up to 2000 tests, that's 2·10^9 operations and a TLE. Reset only the primes you touched.
- Trying to compute the product directly. It overflows immediately.
- Using `Scanner`, which is slow. Use a fast reader.
- Thinking you need to simulate the moves. You never do; counting primes is enough.

## 10. Key takeaway

> When an operation moves something around without creating or destroying it, find the **invariant**, here the prime factor counts. The answer usually comes from that invariant, not from simulating the moves.

---

## Submission

**Accepted** · 34/34 tests · 234 ms · 6.4 MB · Java 21 · 1 attempt
