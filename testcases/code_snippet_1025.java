public static String detectDataDirectory() {

        String dataPath = System.getProperty(JBOSS_SERVER_DATA_DIR);

        if (dataPath != null){
            // we assume jboss.server.data.dir is managed externally so just use it as is.
            File dataDir = new File(dataPath);
            if (!dataDir.exists() || !dataDir.isDirectory()) {
                throw new RuntimeException("Invalid " + JBOSS_SERVER_DATA_DIR + " resources directory: " + dataPath);
            }

            return dataPath;
        }

        // we generate a dynamic jboss.server.data.dir and remove it at the end.
        try {
          File tempKeycloakFolder = Files.createTempDirectory("keycloak-server-").toFile();
          File tmpDataDir = new File(tempKeycloakFolder, "/data");

          if (tmpDataDir.mkdirs()) {
            tmpDataDir.deleteOnExit();
          } else {
            throw new IOException("Could not create directory " + tmpDataDir);
          }

          dataPath = tmpDataDir.getAbsolutePath();
        } catch (IOException ioe){
          throw new RuntimeException("Could not create temporary " + JBOSS_SERVER_DATA_DIR, ioe);
        }

        return dataPath;
    }