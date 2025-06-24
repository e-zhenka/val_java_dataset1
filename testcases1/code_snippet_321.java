protected static File getTmpFolder() {
        try {
            File outputFolder = Files.createTempDirectory("codegen-").toFile();
            outputFolder.deleteOnExit();
            return outputFolder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }