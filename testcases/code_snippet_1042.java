public static long readUE7(InputStream stream) throws IOException {
        int i;
        long v = 0;
        while ((i = stream.read()) >= 0) {
            v = v << 7;
            if ((i & 128) == 128) {
                // Continues
                v += (i & 127);
            } else {
                // Last value
                v += i;
                break;
            }
        }
        return v;
    }