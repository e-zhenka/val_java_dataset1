@SuppressJava6Requirement(reason = "Guarded by version check")
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        if (javaVersion() >= 7) {
            if (directory == null) {
                return Files.createTempFile(prefix, suffix).toFile();
            }
            return Files.createTempFile(directory.toPath(), prefix, suffix).toFile();
        }
        if (directory == null) {
            return File.createTempFile(prefix, suffix);
        }
        File file = File.createTempFile(prefix, suffix, directory);
        // Try to adjust the perms, if this fails there is not much else we can do...
        file.setReadable(false, false);
        file.setReadable(true, true);
        return file;
    }