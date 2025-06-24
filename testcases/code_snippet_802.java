protected static byte[] readFully(InputStream inp, int length, boolean shortDataIsFatal)
            throws IOException {
        byte[] b = new byte[length];

        int pos = 0;
        int read;
        while (pos < length) {
            read = inp.read(b, pos, length-pos);
            if (read == -1) {
                if(shortDataIsFatal) {
                   throw new IOException("Tried to read " + length + " bytes, but only " + pos + " bytes present");
                } else {
                   // Give them what we found
                   // TODO Log the short read
                   return b;
                }
            }
            pos += read;
        }

        return b;
    }