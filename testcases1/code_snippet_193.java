public void copyDirectoryFromPod(
      String namespace,
      String pod,
      String container,
      String srcPath,
      Path destination,
      boolean enableTarCompressing)
      throws IOException, ApiException {
    if (!enableTarCompressing) {
      TreeNode tree = new TreeNode(true, srcPath, true);
      createDirectoryTree(tree, namespace, pod, container, srcPath);
      createDirectoryStructureFromTree(tree, namespace, pod, container, srcPath, destination);
      return;
    }
    final Process proc =
        this.exec(
            namespace,
            pod,
            new String[] {"sh", "-c", "tar cz - " + srcPath + " | base64"},
            container,
            false,
            false);
    try (InputStream is = new Base64InputStream(new BufferedInputStream(proc.getInputStream()));
        ArchiveInputStream archive = new TarArchiveInputStream(new GzipCompressorInputStream(is))) {
      for (ArchiveEntry entry = archive.getNextEntry();
          entry != null;
          entry = archive.getNextEntry()) {
        if (!archive.canReadEntryData(entry)) {
          log.error("Can't read: " + entry);
          continue;
        }
        File f = new File(destination.toFile(), entry.getName());
        if (entry.isDirectory()) {
          if (!f.isDirectory() && !f.mkdirs()) {
            throw new IOException("create directory failed: " + f);
          }
        } else {
          File parent = f.getParentFile();
          if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("create directory failed: " + parent);
          }
          try (OutputStream fs = new FileOutputStream(f)) {
            ByteStreams.copy(archive, fs);
            fs.flush();
          }
        }
      }
    }
    try {
      int status = proc.waitFor();
      if (status != 0) {
        throw new IOException("Copy command failed with status " + status);
      }
    } catch (InterruptedException ex) {
      throw new IOException(ex);
    }
  }