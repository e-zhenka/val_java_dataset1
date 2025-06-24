private void copyJarEntryTrimmingBasePath(JarFile jarFile,
            ZipEntry jarEntry, String basePath, File outputDirectory) {
        String fullPath = jarEntry.getName();
        String relativePath = fullPath
                .substring(fullPath.toLowerCase(Locale.ENGLISH)
                        .indexOf(basePath.toLowerCase(Locale.ENGLISH))
                        + basePath.length());
        File target = new File(outputDirectory, relativePath);
        try {
            if (target.exists()) {
                File tempFile = File.createTempFile(fullPath, null);
                FileUtils.copyInputStreamToFile(
                        jarFile.getInputStream(jarEntry), tempFile);
                if (!FileUtils.contentEquals(tempFile, target)) {
                    FileUtils.forceDelete(target);
                    FileUtils.moveFile(tempFile, target);
                } else {
                    tempFile.delete();
                }
            } else {
                FileUtils.copyInputStreamToFile(
                        jarFile.getInputStream(jarEntry), target);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(String.format(
                    "Failed to extract jar entry '%s' from jarFile '%s'",
                    jarEntry, outputDirectory), e);
        }
    }