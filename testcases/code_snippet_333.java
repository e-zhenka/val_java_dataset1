public static void unJar(File jarFile, File toDir, Pattern unpackRegex)
      throws IOException {
    try (JarFile jar = new JarFile(jarFile)) {
      int numOfFailedLastModifiedSet = 0;
      String targetDirPath = toDir.getCanonicalPath() + File.separator;
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        final JarEntry entry = entries.nextElement();
        if (!entry.isDirectory() &&
            unpackRegex.matcher(entry.getName()).matches()) {
          try (InputStream in = jar.getInputStream(entry)) {
            File file = new File(toDir, entry.getName());
            ensureDirectory(file.getParentFile());
            if (!file.getCanonicalPath().startsWith(targetDirPath)) {
              throw new IOException("expanding " + entry.getName()
                  + " would create file outside of " + toDir);
            }
            try (OutputStream out = new FileOutputStream(file)) {
              IOUtils.copyBytes(in, out, BUFFER_SIZE);
            }
            if (!file.setLastModified(entry.getTime())) {
              numOfFailedLastModifiedSet++;
            }
          }
        }
      }
      if (numOfFailedLastModifiedSet > 0) {
        LOG.warn("Could not set last modfied time for {} file(s)",
            numOfFailedLastModifiedSet);
      }
    }
  }