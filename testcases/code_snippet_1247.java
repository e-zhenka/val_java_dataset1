@Override
    public InputStream getResourceAsStream(String path) throws IOException {
        return classLoader.getResourceAsStream(THEME_RESOURCES_RESOURCES + path);
    }