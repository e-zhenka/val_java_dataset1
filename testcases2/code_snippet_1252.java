public static File createTempFile() throws IOException {
        final AtomicReference<IOException> exceptionReference = new AtomicReference<>();
        final File file = AccessController.doPrivileged(new PrivilegedAction<File>() {
            public File run() {
                File tempFile = null;
                try {
                    tempFile = Files.createTempFile("rep", "tmp").toFile();
                    // Make sure the file is deleted when JVM is shutdown at last.
                    tempFile.deleteOnExit();
                } catch (IOException e) {
                    exceptionReference.set(e);
                }
                return tempFile;
            }
        });
        if (exceptionReference.get() != null) {
            throw exceptionReference.get();
        }
        return file;
    }