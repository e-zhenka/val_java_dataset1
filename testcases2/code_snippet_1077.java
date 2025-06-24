public File createTempFile(final Project project, String prefix, String suffix,
            final File parentDir, final boolean deleteOnExit, final boolean createFile) {
        File result;
        String p = null;
        if (parentDir != null) {
            p = parentDir.getPath();
        } else if (project != null && project.getProperty(MagicNames.TMPDIR) != null) {
            p = project.getProperty(MagicNames.TMPDIR);
        } else if (project != null && deleteOnExit) {
            if (project.getProperty(MagicNames.AUTO_TMPDIR) != null) {
                p = project.getProperty(MagicNames.AUTO_TMPDIR);
            } else {
                final Path systemTempDirPath =
                    new File(System.getProperty("java.io.tmpdir")).toPath();
                final PosixFileAttributeView systemTempDirPosixAttributes =
                    Files.getFileAttributeView(systemTempDirPath, PosixFileAttributeView.class);
                if (systemTempDirPosixAttributes != null) {
                    // no reason to create an extra temp dir if we cannot set permissions
                    try {
                        final File projectTempDir = Files.createTempDirectory(systemTempDirPath,
                            "ant", TMPDIR_ATTRIBUTES)
                            .toFile();
                        projectTempDir.deleteOnExit();
                        p = projectTempDir.getAbsolutePath();
                        project.setProperty(MagicNames.AUTO_TMPDIR, p);
                    } catch (IOException ex) {
                        // silently fall back to system temp directory
                    }
                }
            }
        }
        final String parent = p != null ? p : System.getProperty("java.io.tmpdir");
        if (prefix == null) {
            prefix = NULL_PLACEHOLDER;
        }
        if (suffix == null) {
            suffix = NULL_PLACEHOLDER;
        }

        if (createFile) {
            try {
                final Path parentPath = new File(parent).toPath();
                final PosixFileAttributeView parentPosixAttributes =
                    Files.getFileAttributeView(parentPath, PosixFileAttributeView.class);
                result = Files.createTempFile(parentPath, prefix, suffix,
                    parentPosixAttributes != null ? TMPFILE_ATTRIBUTES : NO_TMPFILE_ATTRIBUTES)
                    .toFile();
            } catch (IOException e) {
                throw new BuildException("Could not create tempfile in "
                        + parent, e);
            }
        } else {
            DecimalFormat fmt = new DecimalFormat("#####");
            synchronized (rand) {
                do {
                    result = new File(parent, prefix
                            + fmt.format(rand.nextInt(Integer.MAX_VALUE)) + suffix);
                } while (result.exists());
            }
        }

        if (deleteOnExit) {
            result.deleteOnExit();
        }
        return result;
    }