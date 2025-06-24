public static Path getPersistedConfigFile() {
        String homeDir = Environment.getHomeDir();

        if (homeDir == null) {
            return Paths.get(Platform.getPlatform().getTmpDirectory().toString(), PersistedConfigSource.KEYCLOAK_PROPERTIES);
        }

        return Paths.get(homeDir, "conf", PersistedConfigSource.KEYCLOAK_PROPERTIES);
    }