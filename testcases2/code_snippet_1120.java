@Override
    public long skip(long ln) throws IOException {
        long n = IOUtils.skip(super.in, ln);
        if (n != ln) {
            throw new IOException("tried to skip "+ln + " but actually skipped: "+n);
        }
        position += n;
        return n;
    }