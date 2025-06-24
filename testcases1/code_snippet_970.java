public static Path getDataFilePath(String pi, String dataFolderName, String altDataFolderName, String fileName)
            throws PresentationException, IndexUnreachableException {
        //make sure fileName is a pure filename and not a path
        fileName = Paths.get(fileName).getFileName().toString();
        java.nio.file.Path dataFolderPath = getDataFolder(pi, dataFolderName);
        if (StringUtils.isNotBlank(fileName)) {
            dataFolderPath = dataFolderPath.resolve(fileName);
        }
        if (StringUtils.isNotBlank(altDataFolderName) && !Files.exists(dataFolderPath)) {
            return getDataFilePath(pi, altDataFolderName, null, fileName);
        }

        return dataFolderPath;
    }