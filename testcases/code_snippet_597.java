public static DomainSocketAddress newSocketAddress() {
        try {
            File file;
            do {
                file = PlatformDependent.createTempFile("NETTY", "UDS", null);
                if (!file.delete()) {
                    throw new IOException("failed to delete: " + file);
                }
            } while (file.getAbsolutePath().length() > 128);
            return new DomainSocketAddress(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }