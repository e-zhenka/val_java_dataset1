private String getSkinResourcePath(String resource)
    {
        String skinFolder = getSkinFolder();
        String resourcePath = skinFolder + resource;

        // Prevent inclusion of templates from other directories
        Path normalizedResource = Paths.get(resourcePath).normalize();
        // Protect against directory attacks.
        if (!normalizedResource.startsWith(skinFolder)) {
            LOGGER.warn("Direct access to skin file [{}] refused. Possible break-in attempt!", normalizedResource);
            return null;
        }

        return resourcePath;
    }