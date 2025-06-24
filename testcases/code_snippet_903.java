private synchronized File initCacheDir() {
        if (cacheDir != null) {
            return cacheDir;
        }

        File cacheRoot = new File(Platform.getPlatform().getTmpDirectory(), "kc-gzip-cache");
        File cacheDir = new File(cacheRoot, Version.RESOURCES_VERSION);

        if (cacheRoot.isDirectory()) {
            for (File f : cacheRoot.listFiles()) {
                if (!f.getName().equals(Version.RESOURCES_VERSION)) {
                    try {
                        FileUtils.deleteDirectory(f);
                    } catch (IOException e) {
                        logger.warn("Failed to delete old gzip cache directory", e);
                    }
                }
            }
        }

        if (!cacheDir.isDirectory() && !cacheDir.mkdirs()) {
            logger.warn("Failed to create gzip cache directory");
            return null;
        }

        return cacheDir;
    }