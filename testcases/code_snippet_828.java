public File createTempFile(final Project project, String prefix, String suffix,
            final File parentDir, final boolean deleteOnExit, final boolean createFile) {
        File result;
        final String parent;
        if (parentDir != null) {
            parent = parentDir.getPath();
        } else if (project != null && project.getProperty(MagicNames.TMPDIR) != null) {
            parent = project.getProperty(MagicNames.TMPDIR);
        } else {
            parent = System.getProperty("java.io.tmpdir");
        }
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