private static File getTmpFolder() {
        try {
            File outputFolder = Files.createTempDirectory("codegen-tmp").toFile();
            outputFolder.deleteOnExit();
            return outputFolder;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot access tmp folder");
        }
    }