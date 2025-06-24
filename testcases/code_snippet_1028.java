public static Path getPersistedConfigFile() {
        String homeDir = Environment.getHomeDir();

        if (homeDir == null) {
            return Paths.get(System.getProperty("java.io.tmpdir"), PersistedConfigSource.KEYCLOAK_PROPERTIES);
        }

        return Paths.get(homeDir, "conf", PersistedConfigSource.KEYCLOAK_PROPERTIES);
    }