public static File createTempFile() throws IOException {
        final File file = File.createTempFile("rep", "tmp");
        // Make sure the file is deleted when JVM is shutdown at last.
        file.deleteOnExit();
        return file;
    }