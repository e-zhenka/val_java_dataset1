private File file(String id) throws IOException {
        File storageDir = directory();
        if (storageDir == null) {
            return null;
        }

        String filename = id + FILE_EXT;
        File file = new File(storageDir, filename);

        // Check the file is within the storage directory
        if (!file.getCanonicalPath().startsWith(storageDir.getCanonicalPath())) {
            log.warn(sm.getString("fileStore.invalid", file.getPath(), id));
            return null;
        }

        return file;
    }