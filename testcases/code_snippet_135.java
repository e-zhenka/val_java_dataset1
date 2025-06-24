private File tempFile() throws IOException {
        String newpostfix;
        String diskFilename = getDiskFilename();
        if (diskFilename != null) {
            newpostfix = '_' + diskFilename;
        } else {
            newpostfix = getPostfix();
        }
        File tmpFile;
        if (getBaseDirectory() == null) {
            // create a temporary file
            tmpFile = PlatformDependent.createTempFile(getPrefix(), newpostfix, null);
        } else {
            tmpFile = PlatformDependent.createTempFile(getPrefix(), newpostfix, new File(
                    getBaseDirectory()));
        }
        if (deleteOnExit()) {
            // See https://github.com/netty/netty/issues/10351
            DeleteFileOnExitHook.add(tmpFile.getPath());
        }
        return tmpFile;
    }