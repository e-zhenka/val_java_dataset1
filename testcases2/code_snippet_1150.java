public static void unJar(File jarFile, File toDir, Pattern unpackRegex)
    throws IOException {
    JarFile jar = new JarFile(jarFile);
    try {
      String targetDirPath = toDir.getCanonicalPath() + File.separator;
      Enumeration<JarEntry> entries = jar.entries();
      while (entries.hasMoreElements()) {
        final JarEntry entry = entries.nextElement();
        if (!entry.isDirectory() &&
            unpackRegex.matcher(entry.getName()).matches()) {
          InputStream in = jar.getInputStream(entry);
          try {
            File file = new File(toDir, entry.getName());
            ensureDirectory(file.getParentFile());
            if (!file.getCanonicalPath().startsWith(targetDirPath)) {
              throw new IOException("expanding " + entry.getName()
                  + " would create file outside of " + toDir);
            }
            OutputStream out = new FileOutputStream(file);
            try {
              IOUtils.copyBytes(in, out, 8192);
            } finally {
              out.close();
            }
          } finally {
            in.close();
          }
        }
      }
    } finally {
      jar.close();
    }
  }