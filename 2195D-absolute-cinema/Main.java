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

    private static long nextLong(DataInputStream in) throws IOException {
        int b = in.read();
        while (b != '-' && (b < '0' || b > '9')) b = in.read();
        boolean neg = false;
        if (b == '-') { neg = true; b = in.read(); }
        long r = 0;
        while (b >= '0' && b <= '9') { r = r * 10 + (b - '0'); b = in.read(); }
        return neg ? -r : r;
    }
}
