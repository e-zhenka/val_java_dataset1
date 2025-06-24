private static File createTemporaryFolderIn(File parentFolder) throws IOException {
        try {
            return createTemporaryFolderWithNioApi(parentFolder);
        } catch (ClassNotFoundException ignore) {
            // Fallback for Java 5 and 6
            return createTemporaryFolderWithFileApi(parentFolder);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            IOException exception = new IOException("Failed to create temporary folder in " + parentFolder);
            exception.initCause(cause);
            throw exception;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create temporary folder in " + parentFolder, e);
        }
    }