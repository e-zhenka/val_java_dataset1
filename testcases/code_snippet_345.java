private boolean isFileWithinDirectory(
            final File dir,
            final File file
    ) throws IOException {
        final File dir_ = dir.getAbsoluteFile();
        if (dir_.isDirectory()) {
            final File fl = new File(dir_, file.getPath());
            if (fl.isFile()) {
                if (fl.getCanonicalPath().startsWith(dir_.getCanonicalPath())) {
                    // Prevent accessing files outside the load-path.
                    // E.g.: ../../coffee
                    return true;
                }
            }
        }

        return false;
    }