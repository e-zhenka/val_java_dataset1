public static boolean isFileOutsideDir(
          @NonNull final String filePath, @NonNull final String baseDirPath) throws IOException {
    File file = new File(filePath);
    File baseDir = new File(baseDirPath);
    return !file.getCanonicalFile().toPath().startsWith(baseDir.getCanonicalFile().toPath());
  }