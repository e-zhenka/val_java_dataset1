@Override
    public long skip(long ln) throws IOException {
        //On TIKA-3092, we found that using the static byte array buffer
        //caused problems with multithreading with the FlateInputStream
        //from a POIFS document stream
        if (skipBuffer == null) {
            skipBuffer = new byte[4096];
        }
        long n = IOUtils.skip(super.in, ln, skipBuffer);
        if (n != ln) {
            throw new IOException("tried to skip "+ln + " but actually skipped: "+n);
        }
        position += n;
        return n;
    }