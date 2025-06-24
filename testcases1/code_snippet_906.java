public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
            ClassLoader tccl = null;
            try {
                tccl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException ex) {
                LOG.warn("Unable to get context classloader instance.", ex);
            }
            return tccl;
        });
    }