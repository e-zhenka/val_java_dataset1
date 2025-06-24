private void unzip(File dir, File zipFile) throws IOException {
        dir = dir.getAbsoluteFile();    // without absolutization, getParentFile below seems to fail
        ZipFile zip = new ZipFile(zipFile);
        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> entries = zip.getEntries();

        try {
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                File f = new File(dir, e.getName());
                if (!f.toPath().normalize().startsWith(dir.toPath())) {
                    throw new IOException(
                        "Zip " + zipFile.getPath() + " contains illegal file name that breaks out of the target directory: " + e.getName());
                }
                if (e.isDirectory()) {
                    mkdirs(f);
                } else {
                    File p = f.getParentFile();
                    if (p != null) {
                        mkdirs(p);
                    }
                    try (InputStream input = zip.getInputStream(e)) {
                        IOUtils.copy(input, writing(f));
                    }
                    try {
                        FilePath target = new FilePath(f);
                        int mode = e.getUnixMode();
                        if (mode!=0)    // Ant returns 0 if the archive doesn't record the access mode
                            target.chmod(mode);
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING, "unable to set permissions", ex);
                    }
                    f.setLastModified(e.getTime());
                }
            }
        } finally {
            zip.close();
        }
    }