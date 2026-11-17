# 2195D · Absolute Cinema

**Platform:** Codeforces (Round 1080, Div. 3) · **Rating:** 1300 · **Tags:** math · **Solved:** 17 Nov 2026 · [Original problem](https://codeforces.com/problemset/problem/2195/D)

---

## 1. The problem in simple words

There is a hidden array `a[1..n]` of small integers (each between −1000 and 1000).
For every position `x` from 1 to `n`, someone computed

```
f(x) = a[1]·|1 − x| + a[2]·|2 − x| + … + a[n]·|n − x|
```

Each `a[i]` is weighted by its distance from `x`. You are only given the `n` values `f(1), f(2), …, f(n)`. Recover the hidden array. The answer is guaranteed to be unique.

| Limit | Value |
|---|---|
| Test cases `t` | up to 10⁴ |
| `n` | 2 to 300 000 (sum over tests ≤ 300 000) |
| `f(i)` | between −10¹⁴ and 10¹⁴ |
| `a[i]` | between −1000 and 1000 |

---

## 2. Intuition: look at how `f` changes between neighbours

Move `x` one step to the right, from `x` to `x + 1`:

- every element **at or left of `x`** (`i ≤ x`) gets 1 farther away, so it adds `+a[i]`;
- every element **right of `x`** (`i > x`) gets 1 closer, so it adds `−a[i]`.

So with `P(x) = a[1] + … + a[x]` (prefix sum) and `S` = sum of the whole array:

```
D(x) = f(x+1) − f(x) = P(x) − (S − P(x)) = 2·P(x) − S
```

The differences of `f` give us **prefix sums** (up to the unknown `S`). Taking the difference **twice** cancels `S`:

```
D(x) − D(x−1) = 2·(P(x) − P(x−1)) = 2·a[x]        for 2 ≤ x ≤ n−1
```

That gives every middle element. Only the two ends are left, and they need `S`.

**Finding `S`:** add the two end values.
`f(1) = Σ a[i]·(i − 1)` and `f(n) = Σ a[i]·(n − i)`, so `f(1) + f(n) = Σ a[i]·(n − 1) = (n − 1)·S`.

---

## 3. Brute force (and why it fails)

Treat it as a linear system: `n` unknowns, `n` equations, and solve with Gaussian elimination.
That's `O(n³)`, hopeless for `n = 300 000`, and floating point would ruin numbers as large as 10¹⁴.

---

## 4. Optimal approach

1. `S = (f(1) + f(n)) / (n − 1)`
2. `D(x) = f(x+1) − f(x)` for `x = 1..n−1`
3. Middle elements: `a[x] = (D(x) − D(x−1)) / 2` for `x = 2..n−1`
4. First element: `D(1) = 2·a[1] − S`, so `a[1] = (D(1) + S) / 2`
5. Last element: `D(n−1) = 2·(S − a[n]) − S = S − 2·a[n]`, so `a[n] = (S − D(n−1)) / 2`

Every division is exact because the hidden array is made of integers. Use `long`: `f` values reach 10¹⁴.

---

## 5. Dry run

Sample: `n = 4`, `f = [17, 9, 9, 13]`.

| Step | Calculation | Result |
|---|---|---|
| `S` | (17 + 13) / 3 | **10** |
| `D(1)` | 9 − 17 | −8 |
| `D(2)` | 9 − 9 | 0 |
| `D(3)` | 13 − 9 | 4 |
| `a[1]` | (−8 + 10) / 2 | **1** |
| `a[2]` | (0 − (−8)) / 2 | **4** |
| `a[3]` | (4 − 0) / 2 | **2** |
| `a[4]` | (10 − 4) / 2 | **3** |

Output `1 4 2 3`. Check: 1 + 4 + 2 + 3 = 10 = `S` ✓.

`n = 2` also works with no middle loop: `f = [420, −69]` gives `S = 351`, `D(1) = −489`, `a[1] = −69`, `a[2] = 420`.

---

## 6. Java code

```java
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(System.in, 1 << 16));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        int t = (int) nextLong(in);
        StringBuilder sb = new StringBuilder();
        while (t-- > 0) {
            int n = (int) nextLong(in);
            long[] f = new long[n + 1];
            for (int i = 1; i <= n; i++) f[i] = nextLong(in);

            // f(1) + f(n) = (n - 1) * S, where S = a1 + ... + an
            long S = (f[1] + f[n]) / (n - 1);
            long[] a = new long[n + 1];
            // D(x) = f(x+1) - f(x) = 2 * prefix(x) - S
            a[1] = (f[2] - f[1] + S) / 2;
            for (int x = 2; x <= n - 1; x++) {
                long dPrev = f[x] - f[x - 1], dCur = f[x + 1] - f[x];
                a[x] = (dCur - dPrev) / 2;               // D(x) - D(x-1) = 2 * a[x]
            }
            a[n] = (S - (f[n] - f[n - 1])) / 2;          // D(n-1) = S - 2 * a[n]

            for (int i = 1; i <= n; i++) sb.append(a[i]).append(i == n ? '\n' : ' ');
            if (sb.length() > 1 << 16) { out.print(sb); sb.setLength(0); }
        }
        out.print(sb);
        out.flush();
    }

    // nextLong(): fast reader that handles negative numbers. See Main.java.
}
```

Full file with the fast reader: [`Main.java`](./Main.java).

---

## 7. Complexity

| | Value | Why |
|---|---|---|
| Time | `O(n)` per test | One pass to read, one pass to rebuild `a` |
| Space | `O(n)` | The `f` and `a` arrays |

---

## 8. Edge cases

- **`n = 2`:** no middle elements. `a[1]` and `a[n]` come straight from `S` and `D(1)`.
- **Negative `f` values:** the reader must handle the minus sign.
- **Large values:** `f` reaches 10¹⁴ and `f(1) + f(n)` reaches 2·10¹⁴, which overflows `int`. Use `long`.

---

## 9. Common mistakes

- Using only `D(x) − D(x−1)` and forgetting that the first and last elements need `S`.
- Getting the sign of `D(x)` backwards. Moving right puts the left part **farther** away (+) and the right part **closer** (−).
- Printing with `System.out.println` per number: too slow for 300 000 outputs. Buffer the output.

---

## 10. Key takeaway

> When a function is a weighted sum of distances, take differences. The first difference gives prefix sums, and the second difference isolates single elements.

---

## Submission

**Accepted** · 343 ms · Java 21 64bit · 1st attempt
