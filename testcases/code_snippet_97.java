@SuppressJava6Requirement(reason = "Guarded by version check")
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        if (javaVersion() >= 7) {
            if (directory == null) {
                return Files.createTempFile(prefix, suffix).toFile();
            }
            return Files.createTempFile(directory.toPath(), prefix, suffix).toFile();
        }
        final File file;
        if (directory == null) {
            file = File.createTempFile(prefix, suffix);
        } else {
            file = File.createTempFile(prefix, suffix, directory);
        }

        // Try to adjust the perms, if this fails there is not much else we can do...
        if (!file.setReadable(false, false)) {
            throw new IOException("Failed to set permissions on temporary file " + file);
        }
        if (!file.setReadable(true, true)) {
            throw new IOException("Failed to set permissions on temporary file " + file);
        }
        return file;
    }