import java.io.*;
import java.util.*;

public class Main {
    static final int MAX = 1_000_000;
    static int[] spf = new int[MAX + 1];

    public static void main(String[] args) throws IOException {
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
                cnt[p] = 0;
            }
            out.println(ok ? "YES" : "NO");
        }
        out.flush();
    }

    static class FastReader {
        private final DataInputStream din; private final byte[] buf = new byte[1 << 16]; private int len = 0, ptr = 0;
        FastReader(InputStream is) { din = new DataInputStream(is); }
        private int read() throws IOException {
            if (ptr == len) { len = din.read(buf, 0, buf.length); ptr = 0; if (len <= 0) return -1; }
            return buf[ptr++];
        }
        int nextInt() throws IOException {
            int ret = 0, b = read();
            while (b < '0' || b > '9') { if (b == '-') { b = read(); int r = 0; while (b >= '0' && b <= '9') { r = r * 10 + (b - '0'); b = read(); } return -r; } b = read(); }
            while (b >= '0' && b <= '9') { ret = ret * 10 + (b - '0'); b = read(); }
            return ret;
        }
    }
}
